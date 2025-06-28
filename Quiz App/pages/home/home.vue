<template>
	<view>
		<view class="home-page">
			<!-- ① 活动标题 -->
			<view class="title">
				<text class="title-main">《中华人民共和国工会法》</text>
				<view class="title-sub">学习有奖竞答活动</view>
				<view class="title-org">主办方：湖南省总工会</view>
			</view>

			<!-- ② 开始答题按钮 -->
			<view class="btn-start">
				<button class="primary-btn" @click="handleStartQuiz">点击答题</button>
			</view>

			<!-- ③ 参与信息 -->
			<view class="participation-info glass-box">
				已有 {{ participantCount }} 人参加活动<br />
				您今天还有 {{ remainingChance }} 次参与机会
			</view>

			<!-- ④ 活动说明 -->
			<view class="activity-section glass-box">
				<view class="section-title">🎉 活动说明</view>

				<view class="sub-title">🎁 活动奖品</view>
				<view>一等奖：10元微信现金红包。</view>
				<view>二等奖：5元微信现金红包。</view>
				<view>三等奖：2元微信现金红包。</view>

				<view class="sub-title">📅 活动时间</view>
				<view>2025年05月19日 08:00 ~ 2025年05月23日 18:00</view>

				<view class="sub-title">🏢 主办单位</view>
				<view>湖南省总工会</view>

				<view class="sub-title">📝 活动规则</view>
				<view>
					本次活动为线上答题形式，活动期间，每人每日答题机会不限。每次答题限时1分钟，每次从题库中随机抽取5道题目，每题20分，总分100分。达到100分即可获得抽奖机会。
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
				participantCount: 342,
				remainingChance: 1,
				otherRanks: []
			};
		},
		onLoad() {
			this.getRankList();
		},
		methods: {
			handleStartQuiz() {
				// 1. 先检查 storage 里有没有登录标识
				const phone = uni.getStorageSync('login_user_phone');
				if (!phone) {
					// 如果没登录，则跳到登录页
					uni.navigateTo({
						url: '/pages/index/index'
					});
					return;
				}
				uni.navigateTo({
					url: '/pages/quiz/quiz'
				});
			},
			async getRankList() {
				uni.request({
					url: this.$baseUrl + "api/getRank",
					method: 'post',
					header: {
						'content-type': 'application/json'
					},
					success: res => {
						console.log(res.data.message);
						this.otherRanks = res.data.object;
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