<template>
	<view class="page-main">
		<!-- 顶部横幅 -->
		<view class="topbox">
			<image class="topimg" :src="topBanner" />
			<view class="imgtext">查找身边隐患 共筑安全防线</view>
		</view>

		<!-- 题目滑动容器 -->
		<swiper class="swipercard" previous-margin="0" next-margin="0" :circular="false" :autoplay="false"
			:current="currentIndex" @change="eventHandle">
			<block v-for="(item, index) in newQuestionsAnswer" :key="item.id">
				<swiper-item class="swiperitem">
					<view class="itembox">

						<!-- 头部：只在答题阶段显示 -->
						<view class="box-hd">

							<view class="timer" v-if="submitPhase === 1">
								错题回顾
							</view>
							<view class="hdname">
								当前第<text class="text1">{{ index + 1 }}</text>道题
							</view>
							<view class="hdnum">共{{ totalNum }}道题</view>
							<view class="timer" v-if="submitPhase === 0">
								倒计时：{{ Math.floor(timeLeft / 60) }}分{{ timeLeft % 60 }}秒
							</view>
						</view>

						<!-- 中间内容：题干 + 选项/问答 -->
						<view class="contentbox">
							<view class="boxtitle">
								<text class="textl">{{ index + 1 }}、</text>
								<text class="textr">{{ item.title }}</text>
							</view>

							<!-- 单选 -->
							<view v-if="item.problemType === 'SINGLE'">
								<view class="boxbody" v-for="(opt, idx) in item.children" :key="opt.alias">
									<view class="chooseitem" @click="submitPhase === 0 && singChoose(index, idx)">
										<image v-if="opt.isSelect" class="sinchoose-on" :src="chooseonImg" />
										<view v-else class="sinchoose" />
										<view class="bodyr">{{ opt.alias }}、{{ opt.answer }}</view>
									</view>
								</view>
							</view>

							<!-- 多选 -->
							<view v-else-if="item.problemType === 'MULTY'">
								<view class="boxbody" v-for="(opt, idx) in item.children" :key="opt.alias">
									<view class="chooseitem" @click="submitPhase === 0 && multyChoose(index, idx)">
										<image v-if="opt.isSelect" class="sinchoose-on" :src="chooseonImg2" />
										<view v-else class="sinchoose sinchoose2" />
										<view class="bodyr">{{ opt.alias }}、{{ opt.answer }}</view>
									</view>
								</view>
							</view>

							<!-- 问答题 -->
							<view v-else-if="item.problemType === 'QUESTION'" class="writeitem">
								<textarea class="textInfo" v-model="item.userAnswer" @input="bindTextAreaBlur(index)"
									auto-height maxlength="200" placeholder="请输入您的答案" :disabled="submitPhase === 1" />
							</view>
						</view>

						<!-- 底部按钮：上一/下一/提交 -->
						<view class="footbtn">
							<view class="ftbtn1" @click="back(index)">上一题</view>
							<view class="ftbtn1" v-if="index + 1 < totalNum" @click="next(index)">下一题</view>
							<view class="ftbtn2" v-if="index + 1 === totalNum&&submitPhase === 0" @click="submitData">提交
							</view>
							<view class="ftbtn2" v-if="index + 1 === totalNum&&submitPhase === 1" @click="submitData">返回
							</view>
						</view>

					</view>
				</swiper-item>
			</block>
		</swiper>
	</view>
</template>
<script>
	export default {
		props: {
			//问题列表数据
			dataList: {
				type: Array,
				default () {
					return []
				}
			}
		},
		watch: {
			dataList: {
				deep: true,
				immediate: true,
				handler(newArr) {
					if (newArr.length) {
						this.newQuestionsAnswer = JSON.parse(JSON.stringify(newArr))
						this.totalNum = newArr.length
						this.setEmptyData()
					}
				},
			}
		},
		data() {
			return {
				topBanner: require('../../static/banner.png'),
				chooseonImg: require('../../static/chooseon.png'),
				chooseonImg2: require('../../static/chooseon2.png'),
				totalNum: 0,
				currentIndex: 0,
				newQuestionsAnswer: [],
				formSubmitData: [], //提交所需数据
				// 倒计时相关
				timeLeft: 5 * 60, // 300 秒
				timer: null,
				submitPhase: 0, // 0=复习，1=最终提交
			};
		},
		mounted() {
			this.startTimer();
		},
		beforeDestroy() {
			this.clearTimer();
		},
		methods: {
			//创建提交数组的数据结构
			setEmptyData() {
				this.newQuestionsAnswer.forEach((res) => {
					let userAnswer = ''
					res.children && res.children.forEach(item => {
						if (!item.isSelect) item.isSelect = 0
						else userAnswer = item
					})
					if (res.problemType == 'QUESTION') userAnswer = res.userAnswer
					this.formSubmitData.push({
						id: res.id, //题目id
						userAnswer: userAnswer, //答案
					})
				})
			},
			// submitData() {
			// 	for (var i = 0; i < this.formSubmitData.length; i++) {
			// 		if (!this.formSubmitData[i].userAnswer) {
			// 			let toast = '请完成第' + (i + 1) + '题后提交！'
			// 			uni.showToast({
			// 				title: toast,
			// 				icon: 'none'
			// 			})
			// 			this.currentIndex = i
			// 			return
			// 		}
			// 	}
			// 	console.log(JSON.stringify(this.dataList))
			// 	console.log(JSON.stringify(this.formSubmitData))
			// 	//this.$emit("submit", this.formSubmitData)
			// },
			submitData() {
				if (this.submitPhase === 0) {
					// 第一次点击：生成复习列表
					const wrongQuestions = this.dataList.filter(q => {
						const entry = this.formSubmitData.find(f => f.id === q.id) || {};
						const userAns = entry.userAnswer;
						const correctAliases = q.answer.split(',');
						let matched = false;

						if (q.problemType === 'SINGLE') {
							matched = !!(userAns && userAns.alias === correctAliases[0]);
						} else if (q.problemType === 'MULTY') {
							const userAliases = (userAns || []).map(a => a.alias).sort();
							matched = JSON.stringify(userAliases) === JSON.stringify(correctAliases.sort());
						} else {
							matched = String(userAns || '').trim() === String(q.answer).trim();
						}

						return !matched;
					});

					// 生成 review 列表：勾选正确答案并禁用操作
					const reviewList = wrongQuestions.map(q => {
						const clone = JSON.parse(JSON.stringify(q));
						const correctAliases = q.answer.split(',');
						clone.children.forEach(opt => {
							opt.isSelect = correctAliases.includes(opt.alias) ? 1 : 0;
						});
						if (clone.problemType === 'QUESTION') {
							clone.userAnswer = q.answer;
						}
						return clone;
					});

					// 渲染复习列表
					this.newQuestionsAnswer = reviewList;
					this.totalNum = reviewList.length;
					this.currentIndex = 0;
					this.reviewMode = true;

					// 进入复习模式后，phase +1
					this.submitPhase++;
					const score = (5-this.newQuestionsAnswer.length)*20;
					uni.showModal({
					  title: '成绩',
					  content: '你得了 ' + score + ' 分！',
					  showCancel: false,
					  success: (res) => {
					    if (res.confirm) {
					      // 用户点了“确定”以后再判断
					      if (score === 100) {
					        this.$emit('submit', this.formSubmitData);
					      }
					    }
					  }
					});
				} else {
					// 第二次点击：真正提交
					this.$emit('submit', this.formSubmitData);
				}
			},
			//单选事件
			singChoose(j, e) {
				if (this.newQuestionsAnswer[j].children[e].isSelect) {
					this.newQuestionsAnswer[j].children[e].isSelect = 0
					this.formSubmitData[j].userAnswer = ''
				} else {
					for (var i = 0; i < this.newQuestionsAnswer[j].children.length; i++) {
						if (this.newQuestionsAnswer[j].children[i].isSelect) {
							this.newQuestionsAnswer[j].children[i].isSelect = 0
						}
					}
					this.newQuestionsAnswer[j].children[e].isSelect = 1
					// 这块结合业务逻辑可以进行优化处理
					// 例如只保存用户已选择答案Id等
					this.formSubmitData[j].userAnswer = this.newQuestionsAnswer[j].children[e]
				}
				this.newQuestionsAnswer = JSON.parse(JSON.stringify(this.newQuestionsAnswer))
			},
			//多选事件
			multyChoose(j, e) {
				this.newQuestionsAnswer[j].children[e].isSelect = this.newQuestionsAnswer[j].children[e].isSelect ^ 1
				let obj = []
				for (var i = 0; i < this.newQuestionsAnswer[j].children.length; i++) {
					if (this.newQuestionsAnswer[j].children[i].isSelect) {
						// 这块结合业务逻辑可以进行优化处理
						// 例如 只保存用户已选择答案Id等
						// obj.push(this.newQuestionsAnswer[j].children[i].id)
						obj.push(this.newQuestionsAnswer[j].children[i])
					}
				}
				this.formSubmitData[j].userAnswer = obj
				this.newQuestionsAnswer = JSON.parse(JSON.stringify(this.newQuestionsAnswer))
			},
			//富文本
			bindTextAreaBlur(index) {
				this.formSubmitData[index].userAnswer = this.newQuestionsAnswer[index].userAnswer
			},
			//swiper改变时
			eventHandle(e) {
				// 这块可以结合业务逻辑进行优化处理，如左右滑动切换题目时做一些提示处理等
				this.currentIndex = e.detail.current
			},
			// 上一题
			back(index) {
				if (!index) return
				this.currentIndex = index - 1
			},
			//下一题
			next(index) {
				this.currentIndex = index + 1
			},
			startTimer() {
				if (this.timer) return;
				this.timer = setInterval(() => {
					if (this.timeLeft > 0) {
						this.timeLeft--;
					} else {
						// 时间到，自动提交
						this.clearTimer();
						uni.showToast({
							title: '时间到，自动提交',
							icon: 'none'
						});
						this.submitData();
					}
				}, 1000);
			},
			// 清除定时器
			clearTimer() {
				if (this.timer) {
					clearInterval(this.timer);
					this.timer = null;
				}
			},
		}
	};
</script>

<style scoped>
	.page-main {
		width: 100%;
		height: 100vh;
	}

	.topbox {
		width: 100%;
		height: 140rpx;
		position: relative;
	}

	.footbtn {
		display: flex;
		justify-content: space-between;
		margin-top: 50rpx;
		padding: 0 24rpx;
	}

	.swipercard {
		width: 100%;
		height: calc(100vh - 316rpx);
		background: #FFFFFF;
	}

	.topbox .topimg {
		width: 100%;
		height: 100%;
	}

	.topbox .imgtext {
		position: absolute;
		bottom: 60rpx;
		left: 58rpx;
		font-size: 36rpx;
		font-weight: normal;
		color: #FFFFFF;
		line-height: 36rpx;
		text-shadow: 0rpx 2rpx 4rpx rgba(0, 0, 0, 0.5);

	}

	.itembox {
		width: calc(100% - 96rpx);
		padding: 32rpx 48rpx;
	}

	.box-hd {
		display: flex;
		justify-content: space-between;
		align-items: center;
		border-bottom: 4rpx dashed #f0f0f0;
		padding-bottom: 16rpx;
		padding-left: 16rpx;
	}

	.hdname {
		width: 200rpx;
		font-size: 28rpx;
		display: flex;
		align-items: flex-start;
		font-weight: 500;
		color: #666666;
		line-height: 40rpx;
	}

	.text1 {
		color: #1FA474;
		font-size: 40rpx;
		line-height: 32rpx;
	}

	.hdnum {
		font-size: 28rpx;
		font-weight: 400;
		color: #666666;
		line-height: 42rpx;
	}

	.timer {
		font-size: 28rpx;
		font-weight: 400;
		color: #666666;
		line-height: 42rpx;
	}

	.contentbox {
		font-size: 30rpx;
		color: #333333;
		margin-top: 48rpx;
	}

	.boxtitle .textl {
		width: 50rpx;
		height: 34rpx;
		background: linear-gradient(90deg, #C3FFDA 0%, #ffffff 100%);
	}

	.boxbody {
		padding-left: 40rpx;
		line-height: 64rpx;
		margin: 16rpx 0;
	}

	.chooseitem {
		display: flex;
		align-items: center;
	}

	.sinchoose {
		width: 28rpx;
		height: 28rpx;
		margin-right: 16rpx;
		border-radius: 50%;
		background: #FFFFFF;
		border: 2rpx solid #BFBDBD;
	}

	.sinchoose2 {
		border-radius: 6rpx;
	}

	.sinchoose-on {
		width: 32rpx;
		height: 32rpx;
		margin-right: 16rpx;
	}

	.writeitem textarea {
		min-height: 164rpx;
		margin: 24rpx auto;
		padding: 16rpx;
		border: 2rpx solid #EBEBEB;
		border-radius: 4px;
		font-size: 30rpx;
		color: #333333;
	}

	.ftbtn1 {
		width: 270rpx;
		height: 80rpx;
		line-height: 80rpx;
		text-align: center;
		border: 2rpx solid #1FA474;
		font-size: 30rpx;
		font-weight: 500;
		color: #1FA474;
	}

	.ftbtn2 {
		width: 270rpx;
		height: 80rpx;
		line-height: 80rpx;
		text-align: center;
		background: #1FA474;
		font-size: 30rpx;
		font-weight: 500;
		color: #FFFFFF;
	}

	/* 新增对 .bodyr 的样式 */
	.bodyr {
		flex: 1;
		/* 占据剩余空间 */
		min-width: 0;
		/* 允许收缩，否则会因为单词/汉字撑开 */
		line-height: 64rpx;
		/* 和 .boxbody 里的行高保持一致 */
		/* 换行设置，保证文字超长自动断行 */
		white-space: normal;
		word-break: break-all;
	}
</style>