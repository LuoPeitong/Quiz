<template>
	<view>
		<view class="home-page">
			<!-- ① 活动标题 -->
			<view class="title">
				<text class="title-main">《中华人民共和国工会法》</text>
				<view class="title-sub">学习有奖竞答活动</view>
				<view class="title-org">主办方：隆回县高新区管委会，隆回县高新区工会联合委员会</view>
			</view>

			<!-- ② 开始答题按钮 -->
			<view class="btn-start">
				<button class="primary-btn" @click="handleStartQuiz">点击答题</button>
			</view>

			<!-- ③ 参与信息 -->
			<view class="participation-info glass-box">
				已有 {{ participantCount }} 人参加活动<br />
				您今天还有 {{ 3-remainingChance }} 次参与机会
			</view>

			<!-- ④ 活动说明 -->
			<view class="activity-section glass-box">
				<view class="section-title">🎉 活动说明</view>

				<view class="sub-title">🎁 活动奖品</view>
				<view>一等奖：5元微信现金红包</view>
				<view>二等奖：3元微信现金红包</view>
				<view>三等奖：2元微信现金红包</view>
				<view>每日奖池：3000元</view>

				<view class="sub-title">📅 活动时间</view>
				<view>2025年07月14日 08:00 ~ 2025年07月18日 18:00</view>

				<view class="sub-title">🏢 主办单位</view>
				<view>隆回县高新区管委会，隆回县高新区工会联合委员会</view>

				<view class="sub-title">📝 活动规则</view>
				<view>
					本次活动为线上答题形式，活动期间，每人每天最多3次答题机会。
					每次从题库中随机抽取5道题目，每题20分，总分100分。
					仅当5道题全部答对（100分）时，才可获得一次抽奖机会。
				</view>

				<view class="sub-title">🏆 组织奖</view>
				<view>
					企业组织50人及以上参与即可参评，按答对次数排名，评选前10家企业，颁发“最佳组织奖”。
				</view>
			</view>

			<!-- ⑤ 排行榜 -->
			<view class="rank-section glass-box">
				<view class="section-title">🏆 排行榜</view>
				<view class="rank-list">
					<view class="rank-row" v-for="(item, index) in otherRanks" :key="index">
						<view>NO.{{ index+1 }}</view>
						<view>{{ item.nickname }}</view>
						<view>{{ Math.floor(item.timeUsed / 60) }}分 {{ item.timeUsed%60 }}秒</view>
						<view class="score">{{ item.score }}分</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				nickname: '',
				participantCount: 0,
				remainingChance: 0,
				otherRanks: []
			};
		},
		onLoad() {
			this.Init();
		},
		onShow() {
			//uni.clearStorageSync();
			this.Init();
		},
		methods: {
			handleStartQuiz() {
				// 1. 先检查 storage 里有没有登录标识
				const phone = uni.getStorageSync('login_user_phone');
				if (!phone) {
					// 如果没登录，则跳到登录页
					uni.navigateTo({
						url: '/pages/index/index?returnUrl=/pages/quiz/quiz'
					});
					return;
				}
				// 2. 判断今天剩余答题次数
				if (this.remainingChance >= 3) {
					uni.showToast({
						icon: 'none',
						title: '您今天的答题次数已用完'
					});
					return;
				}
				uni.navigateTo({
					url: '/pages/quiz/quiz'
				});
			},
			async Init() {
				const loginTime = uni.getStorageSync('login_timeing')
				const currentTimestamp = Date.now();
				const timeDifference = currentTimestamp - loginTime;
				
				// 判断时间差是否超过30分钟（30分钟 = 30 * 60 * 1000 毫秒）
				if (timeDifference > 5 * 60 * 1000) {
				  uni.removeStorageSync('login_user_phone')
				  uni.removeStorageSync('login_timeing')
				}
				const phone = uni.getStorageSync('login_user_phone');
				if (!phone) {
					// 如果没登录，则跳到登录页
					uni.navigateTo({
						url: '/pages/index/index?returnUrl=/pages/home/home'
					});
					return;
				}
				uni.request({
					url: this.$baseUrl + "api/init",
					method: 'post',
					header: {
						'content-type': 'application/json'
					},
					data: {
						id: Number(uni.getStorageSync('login_user_phone').id), // 确保是 Integer
					},
					success: res => {
						this.otherRanks = res.data.object.otherRanks;
						this.participantCount = res.data.object.participantCount;
						this.remainingChance = res.data.object.remainingChance;
					}
				})
			}
		}
	}
</script>

<style scoped>
	.home-page {
		padding: 30rpx;
		background-image: url('@/static/1.png');
		background-size: cover;
		background-repeat: no-repeat;
		background-position: center center;
		min-height: 100vh;
	}

	.title {
		text-align: center;
		margin-bottom: 40rpx;
		margin-top: 200rpx;
		/* 顶部留白 */
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

	.section-title {
		font-size: 20px;
		font-weight: bold;
		margin: 20rpx 0;
	}

	.sub-title {
		font-weight: bold;
		margin-top: 16rpx;
		color: #c40000;
	}


	.score {
		color: red;
		font-weight: bold;
	}

	.rank-row {
		display: flex;
		justify-content: space-between;
		padding: 12rpx 0;
		border-bottom: 1px solid #eee;
	}

	.glass-box {
		background: rgba(255, 255, 255, 0.3);
		backdrop-filter: blur(12px);
		-webkit-backdrop-filter: blur(12px);
		border-radius: 20rpx;
		padding: 24rpx;
		margin-bottom: 30rpx;
		border: 1px solid rgba(255, 255, 255, 0.3);
	}
</style>