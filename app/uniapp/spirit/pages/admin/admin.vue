<template>
	<view class="container">
		<!-- 用户信息卡片 -->
		<view class="user-card">
			<view class="avatar-emoji">👤</view>
			<view class="user-info">
				<text class="username">{{username}}</text>
				<text class="role">管理员</text>
			</view>
		</view>

		<!-- 功能菜单 -->
		<view class="menu-grid">
			<view class="menu-card" @tap="navigateTo('/pages/admin/videoData')">
				<view class="menu-icon">
					<uni-icons type="videocam" size="32" color="#0284da"></uni-icons>
				</view>
				<text class="menu-title">视频列表</text>
				<text class="menu-desc">管理所有视频内容</text>
			</view>

			<view class="menu-card" @tap="navigateTo('/pages/admin/favData')">
				<view class="menu-icon">
					<uni-icons type="star" size="32" color="#0284da"></uni-icons>
				</view>
				<text class="menu-title">收藏管理</text>
				<text class="menu-desc">管理收藏的视频</text>
			</view>

			<view class="menu-card" @tap="showNotOpen">
				<view class="menu-icon">
					<uni-icons type="gear" size="32" color="#0284da"></uni-icons>
				</view>
				<text class="menu-title">系统配置</text>
				<text class="menu-desc">系统参数设置</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				username: '管理员'
			}
		},
		onShow() {
			// 检查cookie是否过期
			const expireTime = uni.getStorageSync('adminCookieExpire');
			const currentTime = new Date().getTime();
			
			if (!expireTime || currentTime > expireTime) {
				// cookie已过期，清除存储并跳转到登录页
				uni.removeStorageSync('adminCookie');
				uni.removeStorageSync('adminCookieExpire');
				uni.redirectTo({
					url: '/pages/admin/login'
				});
				return;
			}
		},
		onLoad() {
			this.checkLogin();
		},
		methods: {
			checkLogin() {
				const adminCookie = uni.getStorageSync('adminCookie');
				if (!adminCookie) {
					uni.showToast({
						title: '请先登录',
						icon: 'none',
						duration: 1500
					});
					setTimeout(() => {
						uni.redirectTo({
							url: '/pages/admin/login'
						});
					}, 1500);
				}
			},
			navigateTo(url) {
				const adminCookie = uni.getStorageSync('adminCookie');
				if (!adminCookie) {
					uni.showToast({
						title: '请先登录',
						icon: 'none'
					});
					return;
				}
				uni.navigateTo({
					url: url
				});
			},
			showNotOpen() {
				uni.showToast({
					title: '暂未开放',
					icon: 'none'
				});
			}
		}
	}
</script>

<style>
.container {
	min-height: 100vh;
	background: #f6f7f8;
	padding: 24rpx;
}

.user-card {
	background: #fff;
	border-radius: 16rpx;
	padding: 32rpx;
	margin-bottom: 32rpx;
	display: flex;
	align-items: center;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.avatar-emoji {
	width: 120rpx;
	height: 120rpx;
	border-radius: 60rpx;
	margin-right: 24rpx;
	background: #f5f5f5;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 72rpx;
}

.user-info {
	flex: 1;
}

.username {
	font-size: 36rpx;
	font-weight: 600;
	color: #333;
	margin-bottom: 8rpx;
	display: block;
}

.role {
	font-size: 24rpx;
	color: #999;
}

.menu-grid {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 24rpx;
}

.menu-card {
	background: #fff;
	border-radius: 16rpx;
	padding: 32rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
	transition: all 0.3s ease;
}

.menu-card:active {
	transform: scale(0.98);
	background: #fafafa;
}

.menu-icon {
	width: 96rpx;
	height: 96rpx;
	background: #fff5f7;
	border-radius: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 16rpx;
}

.menu-title {
	font-size: 32rpx;
	font-weight: 500;
	color: #333;
	margin-bottom: 8rpx;
}

.menu-desc {
	font-size: 24rpx;
	color: #999;
	text-align: center;
}
</style>
