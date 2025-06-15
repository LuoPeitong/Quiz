<template>
  <view class="home-page">
    <!-- ① 活动标题 -->
    <view class="title">
      <text class="title-main">《xxxx工会法》</text>
      <view class="title-sub">学习有奖竞答活动</view>
      <view class="title-org">xxx主办方</view>
    </view>

    <!-- ② 开始答题按钮 -->
    <view class="btn-start">
      <button class="primary-btn" @click="startQuiz">点击答题</button>
    </view>

    <!-- ③ 参与信息 -->
    <view class="participation-info">
      已有 {{ participantCount }} 人参加活动<br />
      您今天还有 {{ remainingChance }} 次参与机会
    </view>

    <!-- ④ 活动说明 -->
    <view class="card card-description">
      <view class="card-title">📘 活动说明</view>
      <view class="card-content">{{ activityDescription }}</view>
    </view>

    <!-- ⑤ 排行榜 -->
    <view class="card card-rank">
      <view class="card-title">🏆 排行榜</view>
      <view class="rank-top3">
        <view class="rank-circle">第二</view>
        <view class="rank-circle first">第一</view>
        <view class="rank-circle">第三</view>
      </view>
      <view
        v-for="(item, index) in rankList.slice(3)"
        :key="index"
        class="rank-item"
      >
        <text>no.{{ index + 4 }} {{ item.name }}</text>
        <text>{{ item.time_used }} 秒 | {{ item.score }} 分</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      participantCount: 0,
      remainingChance: 1,
      activityDescription: "这里是活动说明内容...",
      rankList: []
    }
  },
  onLoad() {
    this.fetchRank()
    this.fetchUserInfo()
  },
  methods: {
    async getRankList() {
      const res = await uni.request({
        url: 'http://localhost:8000/api/rank',
        method: 'GET'
      });
      if (res[1].statusCode === 200) {
        this.rankList = res[1].data;
      }
    },

    startQuiz() {
      uni.navigateTo({ url: '/pages/quiz/quiz' })
    },
    fetchRank() {
      uni.request({
        url: 'http://localhost:8000/api/rank',
        success: (res) => {
          this.rankList = res.data
        }
      })
    },
    fetchUserInfo() {
      this.participantCount = 342
      this.remainingChance = 1
    }
  }
}
</script>

<style scoped>
.home-page {
  padding: 30rpx;
  background-image: url('@/static/1.png'); /* 替换为你的图片路径 */
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center center;
  min-height: 100vh; /* 保证至少铺满屏幕 */
}


.title {
  text-align: center;
  margin-bottom: 40rpx;
}

.title-main {
  font-size: 40rpx;
  font-weight: bold;
}

.title-sub {
  font-size: 28rpx;
  margin-top: 10rpx;
}

.title-org {
  font-size: 24rpx;
  color: #888;
  margin-top: 6rpx;
}

.btn-start {
  text-align: center;
  margin-bottom: 20rpx;
}

.primary-btn {
  background-color: #3c9cff;
  color: #fff;
  padding: 16rpx 40rpx;
  border-radius: 10rpx;
  font-size: 30rpx;
  border: none;
}

.participation-info {
  text-align: center;
  font-size: 24rpx;
  color: #666;
  margin-bottom: 40rpx;
  line-height: 1.6;
}

.card {
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 30rpx;
}

.card-title {
  font-weight: bold;
  font-size: 30rpx;
  margin-bottom: 16rpx;
}

.card-content {
  font-size: 26rpx;
  color: #333;
  line-height: 1.6;
}

/* 活动说明卡片 */
.card-description {
  background-color: #eaf4ff;
  border: 1rpx solid #cce0f5; /* 添加边框，颜色浅一点，与背景协调 */
}


/* 排行榜卡片 */
.card-rank {
  background-color: #fff;
  border: 1rpx solid #ddd;
  box-shadow: 0 4rpx 10rpx rgba(0, 0, 0, 0.03);
}

/* 排行榜样式 */
.rank-top3 {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20rpx;
}

.rank-circle {
  width: 60rpx;
  height: 60rpx;
  border-radius: 50%;
  background-color: #f0f0f0;
  text-align: center;
  line-height: 60rpx;
  font-weight: bold;
  font-size: 24rpx;
}

.first {
  background-color: gold;
}

.rank-item {
  display: flex;
  justify-content: space-between;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #eee;
  font-size: 26rpx;
  color: #444;
}
</style>
