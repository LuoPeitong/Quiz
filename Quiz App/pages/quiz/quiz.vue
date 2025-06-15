<template>
  <view class="container">
    <!-- 顶部进度 -->
    <view class="top-bar">
      <text>{{ currentIndex + 1 }}/{{ questions.length }} 题</text>
      <text>累计用时：{{ minutes }}分{{ seconds }}秒</text>
    </view>

    <!-- 题干区域 -->
    <view class="box">
      <text>{{ currentQuestion.question }}</text>
    </view>

    <!-- 选项区域（选择题/判断题） -->
    <view v-if="isChoiceType" class="box">
      <view v-for="(opt, index) in currentQuestion.options" :key="index"
            class="option"
            :class="{ selected: selectedAnswers[currentIndex] === opt }"
            @click="selectOption(opt)">
        <text>{{ opt }}</text>
      </view>
    </view>

    <!-- 填空题答题区 -->
    <view v-else-if="['填空题', '简答题'].includes(currentQuestion.type)" class="box">
      <input class="input" type="text" v-model="selectedAnswers[currentIndex]" placeholder="请输入答案" />
    </view>

    <!-- 导航按钮 -->
    <view class="btn-row">
      <button @click="prevQuestion" :disabled="currentIndex === 0">上一题</button>
      <button v-if="!isLast" @click="nextQuestion">下一题</button>
      <button v-else @click="submit">提交</button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      questions: [],
      currentIndex: 0,
      selectedAnswers: [],
	  userAnswers: {},  // 记录用户答案
	  startTime: 0,     // 开始答题时间
      timer: null,
      timeUsed: 0,
	  openid: ''
    };
  },
  onLoad() {
    this.startTime = Date.now(); // 记录开始时间
  },
  computed: {
    currentQuestion() {
      return this.questions[this.currentIndex] || {};
    },
    isChoiceType() {
      return this.currentQuestion.type === '选择题' || this.currentQuestion.type === '判断题';
    },
    isLast() {
      return this.currentIndex === this.questions.length - 1;
    },
    minutes() {
      return Math.floor(this.timeUsed / 60);
    },
    seconds() {
      return this.timeUsed % 60;
    },
	score() {
	      // 简单评分逻辑（正确得 10 分）
	      let total = 0;
	      this.questions.forEach((q, i) => {
	        if (this.selectedAnswers[i] && this.selectedAnswers[i] === q.answer) {
	          total += 10;
	        }
	      });
	      return total;
	    }
  },
  methods: {
    selectOption(option) {
      if (this.isChoiceType) {
        this.$set(this.selectedAnswers, this.currentIndex, option);
      }
    },
    prevQuestion() {
      if (this.currentIndex > 0) this.currentIndex--;
    },
    nextQuestion() {
      if (this.currentIndex < this.questions.length - 1) this.currentIndex++;
    },
	async submit() {
      if (!this.questions || !Array.isArray(this.questions) || this.questions.length === 0) {
        uni.showToast({ title: '题目未加载或为空', icon: 'none' });
        return;
      }
	    const endTime = Date.now();
	    const timeUsed = Math.floor((endTime - this.startTime) / 1000);
	
	    const answers = this.questions.map(item => {
	      const userAns = this.userAnswers[item.id] || "";
	      const isCorrect = item.answer.includes(userAns);
	      return {
	        question_id: item.id,
	        user_answer: userAns,
	        correct_answer: item.answer.join(","),
	        is_correct: isCorrect
	      };
	    });
	
	    const score = answers.filter(a => a.is_correct).length;
	
	    const res = await uni.request({
	      url: 'http://localhost:8000/api/submit',
	      method: 'POST',
	      data: {
	        openid: uni.getStorageSync('openid') || 'demo_user',
	        time_used: timeUsed,
	        score: score,
	        answers: answers
	      }
	    });
	
	    if (res.data.code === 0) {
	      uni.showModal({
	        title: '成绩',
	        content: `你得了 ${score} 分！`,
	        showCancel: false,
	        success: () => {
	          uni.reLaunch({ url: '/pages/home/home' });
	        }
	      });
	    } else {
	      uni.showToast({ title: '提交失败', icon: 'none' });
	    }
	  },
    submitAnswers() {
      const answerList = this.questions.map((q, index) => {
        const correctAnswerStr = Array.isArray(q.answer) ? q.answer.join(', ') : q.answer;
        const userAnswer = this.selectedAnswers[index] || '';
        return {
          question_id: q.id || index + 1,
          user_answer: userAnswer,
          correct_answer: correctAnswerStr, // 确保为字符串
          is_correct: userAnswer === correctAnswerStr
        };
      });
    
      uni.request({
        url: 'http://localhost:8000/api/submit',
        method: 'POST',
        data: {
          openid: this.openid,
          time_used: this.timeUsed,
          score: this.score,
          answers: answerList
        },
        success: (res) => {
          if (res.statusCode === 200) {
            uni.showToast({ title: '提交成功', icon: 'success' });
            uni.redirectTo({ url: '/pages/home/home' });
          } else {
            uni.showToast({ title: '提交失败', icon: 'error' });
          }
        },
        fail: (err) => {
          uni.showToast({ title: '网络错误', icon: 'none' });
          console.error(err);
        }
      });
    },
    startTimer() {
      this.timer = setInterval(() => {
        this.timeUsed++;
      }, 1000);
    }
  },
  mounted() {
    this.startTimer();
	
	uni.request({
	    url: 'http://localhost:8000/api/questions',
	    success: (res) => {
	      this.questions = res.data;
	    },
	    fail: (err) => {
	      uni.showToast({ title: '获取题目失败', icon: 'none' });
	      console.error(err);
	    }
	  });
  },
  beforeDestroy() {
    clearInterval(this.timer);
  }
};
</script>

<style scoped>
.container {
  padding: 20rpx;
  display: flex;
  flex-direction: column;
}

.top-bar {
  width: 100%;
  display: flex;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.box {
  width: 100%;
  border: 1px solid #ccc;
  border-radius: 12rpx;
  padding: 20rpx;
  margin-bottom: 20rpx;
  box-sizing: border-box;
}

.option {
  padding: 16rpx;
  border: 1px solid #999;
  border-radius: 8rpx;
  margin-top: 12rpx;
}

.option.selected {
  background-color: #d0ebff;
  border-color: #3c9cff;
  font-weight: bold;
}

.input {
  border: 1px solid #ccc;
  padding: 16rpx;
  border-radius: 8rpx;
  width: 100%;
}

.btn-row {
  display: flex;
  justify-content: space-between;
  margin-top: 30rpx;
}

button {
  flex: 1;
  margin: 0 10rpx;
  padding: 20rpx;
  border-radius: 10rpx;
  background-color: #f0f0f0;
}
</style>
