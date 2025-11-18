<template>
	<view class="container">
		<view class="login-box">
			<view class="header">
				<view class="logo-emoji">👤</view>
				<text class="title">管理员登录</text>
			</view>
			
			<view class="form">
				<view class="input-group">
					<text class="icon">👤</text>
					<input 
						class="input" 
						type="text" 
						v-model="username" 
						placeholder="请输入账号"
						placeholder-class="placeholder"
					/>
				</view>
				
				<view class="input-group">
					<text class="icon">🔒</text>
					<input 
						class="input" 
						type="password" 
						v-model="password" 
						placeholder="请输入密码"
						placeholder-class="placeholder"
					/>
				</view>

				<!-- 记住密码复选框 -->
				<view class="remember-group">
					<checkbox-group @change="onRememberChange">
						<label class="remember-label">
							<checkbox value="remember" :checked="rememberMe" />
							<text class="remember-text">记住密码</text>
						</label>
					</checkbox-group>
				</view>
				
				<button class="login-btn" @tap="handleLogin" :disabled="!username || !password">
					登录
				</button>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				username: '',
				password: '',
				rememberMe: false
			}
		},
		onLoad() {
			try {
				const remembered = uni.getStorageSync('adminRemember');
				if (remembered) {
					const savedUsername = uni.getStorageSync('adminUsername');
					const savedPassword = uni.getStorageSync('adminPassword');
					if (savedUsername) this.username = savedUsername;
					if (savedPassword) this.password = savedPassword;
					this.rememberMe = true;
				} else {
				}
			} catch (e) {
			}
		},

		methods: {
			onRememberChange(e) {
				const values = e?.detail?.value || [];
				this.rememberMe = values.includes('remember');
				console.log('记住密码状态:', this.rememberMe);
			},

			handleLogin() {
				if (!this.username || !this.password) {
					uni.showToast({
						title: '请输入账号和密码',
						icon: 'none'
					});
					return;
				}

				console.log('开始登录，用户名:', this.username);
				
				const serveraddr = uni.getStorageSync('serveraddr');
				const serverport = uni.getStorageSync('serverport');
				
				if (!serveraddr || !serverport) {
					uni.showToast({
						title: '请先设置服务器地址',
						icon: 'none'
					});
					setTimeout(() => {
						uni.switchTab({
							url: '../index/index'
						});
					}, 1500);
					return;
				}
				
				uni.showLoading({
					title: '登录中...'
				});
				uni.request({
					url: `${serveraddr}:${serverport}/admin/api/login`,
					method: 'POST',
					header: {
						'content-type': 'application/x-www-form-urlencoded'
					},
					data: {
						username: this.username,
						password: this.password
					},
					success: (res) => {
						uni.hideLoading()
						console.log(res);
						if (res.data.resCode === '000001') {
							const cookies = res.header['Set-Cookie'] || res.header['set-cookie'];
							if (cookies) {
								const expireTime = new Date().getTime() + 24 * 60 * 60 * 1000;
								uni.setStorageSync('adminCookie', cookies);
								uni.setStorageSync('adminCookieExpire', expireTime);
							}
							try {
								if (this.rememberMe) {
									uni.setStorageSync('adminRemember', true);
									uni.setStorageSync('adminUsername', this.username);
									uni.setStorageSync('adminPassword', this.password);
								} else {
									uni.removeStorageSync('adminRemember');
									uni.removeStorageSync('adminUsername');
									uni.removeStorageSync('adminPassword');
								}
							} catch (e) {
								console.log('保存本地存储失败', e);
							}
							
							uni.showToast({
								title: '登录成功',
								icon: 'success'
							});
							setTimeout(() => {
								uni.switchTab({
									url: '/pages/admin/admin'
								});
							}, 1500);
						} else {
							console.log("else")
							uni.showToast({
								title: res.data.message || '登录失败',
								icon: 'none'
							});
						}
					},
					fail: () => {
						uni.showToast({
							title: '网络错误',
							icon: 'none'
						});
					},
					complete: () => {
						uni.hideLoading();
					}
				});
			}
		}
	}
</script>

<style>
.container {
	min-height: 100vh;
	background: #f6f7f8;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 40rpx;
	position: relative;
	top: -10vh;
}

.login-box {
	width: 100%;
	background: #fff;
	border-radius: 24rpx;
	padding: 48rpx 40rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
}

.header {
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-bottom: 48rpx;
}

.logo-emoji {
	width: 160rpx;
	height: 160rpx;
	border-radius: 80rpx;
	margin-bottom: 24rpx;
	background: #f5f5f5;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 100rpx;
}

.title {
	font-size: 36rpx;
	font-weight: 600;
	color: #333;
}

.form {
	display: flex;
	flex-direction: column;
	gap: 32rpx;
}

.input-group {
	display: flex;
	align-items: center;
	background: #f8f8f8;
	border-radius: 12rpx;
	padding: 24rpx;
	gap: 16rpx;
	transition: all 0.3s ease;
	border: 2rpx solid transparent;
}

.input-group:focus-within {
	background: #fff;
	border-color: #0284da;
	box-shadow: 0 0 0 4rpx rgba(2, 132, 218, 0.1);
}

.icon {
	font-size: 32rpx;
	color: #999;
}

.input {
	flex: 1;
	height: 48rpx;
	font-size: 28rpx;
	color: #333;
}

.placeholder {
	color: #999;
}

.login-btn {
	background: #0284da;
	height: 88rpx;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-top: 48rpx;
	box-shadow: 0 4rpx 12rpx rgba(2, 132, 218, 0.2);
	transition: all 0.3s ease;
	color: #fff;
	font-size: 32rpx;
	font-weight: 500;
}

.login-btn:active {
	transform: translateY(2rpx);
	box-shadow: 0 2rpx 6rpx rgba(2, 132, 218, 0.15);
}

.login-btn[disabled] {
    background: #a8d4e8;
    box-shadow: none;
    transform: none;
}

/* 记住密码样式 */
.remember-group {
    display: flex;
    align-items: center;
    padding: 0 8rpx;
}
.remember-label {
    display: flex;
    align-items: center;
    gap: 12rpx;
}
.remember-text {
    font-size: 28rpx;
    color: #666;
}
</style>
