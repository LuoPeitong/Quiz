<template>
	<view class="quiz-page">
		<!-- 有题目时才渲染滑动组件 -->
		<liu-slide-questions v-if="slideQuestions.length" :dataList="slideQuestions" @submit="onSubmit" />
		<!-- 无题目时显示加载或空状态 -->
		<view v-else class="loading">
			正在加载题目...
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				rawQuestions: [], // 原始接口返回
				slideQuestions: [], // 转换后给插件的数据
				startTime: 0 // 记录开始时间
			}
		},
		// 声明 uni_modules 插件组件
		usingComponents: {
			'liu-slide-questions': '/uni_modules/liu-slide-questions/components/liu-slide-questions/liu-slide-questions'
		},
		onLoad() {
			this.startTime = Date.now()
			this.fetchQuestions()
		},
		methods: {
			fetchQuestions() {
				uni.showLoading({
					title: '加载中'
				})
				uni.request({
					url: this.$baseUrl + "api/questions/random",
					success: res => {
						uni.hideLoading()
						if (res.statusCode === 200 && res.data.code === 200) {
							// 直接把后端已经映射好的列表给插件
							this.slideQuestions = res.data.object
						} else {
							uni.showToast({
								title: '获取题目失败',
								icon: 'none'
							})
						}
					},
					fail: err => {
						uni.hideLoading()
						console.error(err)
						uni.showToast({
							title: '网络错误',
							icon: 'none'
						})
					}
				})
			},
			onSubmit(dataList) {
				uni.showLoading({
					title: '提交中'
				})
				uni.request({
					url: this.$baseUrl + "api/submit",
					method: 'POST',
					header: {
						'Content-Type': 'application/json'
					},
					data: {
						userId: Number(uni.getStorageSync('login_user_phone').id), // 确保是 Integer
						timeUsed: this.calcTimeUsed(),
						dataList: dataList // <-- 传插件原始返回数组，包含 problemType & children & userAnswer
					},
					success: res => {
						uni.hideLoading()
						if (res.data.code === 200) {
							uni.showModal({
								title: '成绩',
								content: `你得了 ${res.data.object.score} 分！`,
								showCancel: false,
								success: () => uni.reLaunch({
									url: '/pages/home/home'
								})
							})
						} else {
							uni.showToast({
								title: '提交失败',
								icon: 'none'
							})
						}
					},
					fail: () => {
						uni.hideLoading()
						uni.showToast({
							title: '网络错误',
							icon: 'none'
						})
					}
				})
			},

			calcTimeUsed() {
				return Math.floor((Date.now() - this.startTime) / 1000)
			}
		}
	}
</script>

<style scoped>
	.quiz-page {
		flex: 1;
		background-color: #f5f5f5;
	}

	.loading {
		margin-top: 200rpx;
		text-align: center;
		color: #999;
	}
</style>