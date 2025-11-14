<template>
	<view class="container">
		<!-- 搜索头部 -->
		<view class="search-header">
			<view class="search-box">
				<view class="search-input-wrapper">
					<input 
						class="search-input" 
						v-model="searchKey" 
						placeholder="搜索视频..." 
						placeholder-class="placeholder"
						@confirm="handleSearch"
						@input="debounceSearch"
					/>
					<view class="search-btn" @click="handleSearch">
						<uni-icons type="search" size="18" color="#666"></uni-icons>
					</view>
				</view>
				<view class="filter-btn" @click="toggleFilter">
					筛选
				</view>
				<view class="whoosh-btn" @click="goToWhoosh">
					咻咻
				</view>
			</view>
			
			<!-- 筛选面板 -->
			<view v-if="showFilter" class="filter-panel">
				<view class="filter-row">
					<text class="filter-label">视频平台</text>
					<input 
						class="filter-input" 
						v-model="selectedPlatform" 
						placeholder="输入平台名称..." 
						placeholder-class="placeholder"
						@input="onPlatformInput"
					/>
				</view>
				
				<view class="filter-row">
					<text class="filter-label">视频标签</text>
					<input 
						class="filter-input" 
						v-model="selectedTag" 
						placeholder="输入标签名称..." 
						placeholder-class="placeholder"
						@input="onTagInput"
					/>
				</view>
				
				<view class="filter-actions">
					<view class="filter-action-btn clear-btn" @click="clearFilters">
						清空筛选
					</view>
					<view class="filter-action-btn apply-btn" @click="applyFilters">
						应用筛选
					</view>
				</view>
			</view>
		</view>

		<!-- 视频列表 -->
		<view class="content-wrap">
			<!-- 视频列表 -->
			<view class="video-list">
				<!-- 骨架屏卡片 - 只在加载中且列表为空时显示 -->
				<view 
					class="video-card skeleton-card" 
					v-for="n in 6" 
					:key="n"
					v-if="isLoading && list.length === 0"
				>
					<view class="video-card-inner">
						<view class="video-cover skeleton-cover">
							<view class="skeleton-shimmer"></view>
						</view>
						<view class="video-info skeleton-info">
							<view class="video-title skeleton-title">
								<view class="skeleton-shimmer"></view>
							</view>
							<view class="video-meta skeleton-meta">
								<view class="skeleton-shimmer"></view>
							</view>
							<view class="video-desc skeleton-desc">
								<view class="skeleton-shimmer"></view>
							</view>
						</view>
					</view>
				</view>
				
				<!-- 实际视频卡片 -->
				<view 
					class="video-card" 
					v-for="(item, index) in list" 
					:key="index"
					@tap="playVideo(item.videounrealaddr)"
				>
					<view class="video-card-inner">
						<view class="video-cover" @click="togglePlayBtn(index)" @touchstart="showPlayButton(index)" @touchend="hidePlayButton(index)">
							<!-- 懒加载图片 -->
							<image 
								class="cover-image" 
								:src="item.videocover" 
								mode="aspectFill"
								lazy-load="true"
								@load="onImageLoad(index)"
								@error="onImageError(index)"
								v-show="!item.imageLoading && !item.imageError"
							></image>
							<view class="privacy-cover" v-if="item.videoprivacy === '1'">
								<uni-icons type="locked" size="20" color="#fff"></uni-icons>
							</view>
							<!-- 图片加载失败显示 -->
							<view class="image-error" v-if="item.imageError && !item.imageLoading">
								<text class="error-text">图片加载失败</text>
							</view>
							<!-- 图片加载中骨架屏 -->
							<view class="image-skeleton" v-if="item.imageLoading">
								<view class="skeleton-shimmer"></view>
							</view>
							<!-- 播放按钮 -->
							<view class="play-btn" :class="{ 'show': item.showPlayBtn }" @click="playVideo(item)">
								<uni-icons type="play-filled" size="24" color="#fff"></uni-icons>
							</view>
						</view>
						<view class="video-info">
							<view class="video-title">{{ item.videoname }}</view>
							<view class="video-meta">
								<view class="video-platform" v-if="item.videoplatform">{{ item.videoplatform }}</view>
								<view class="video-tag" v-if="item.videotag">{{ item.videotag }}</view>
								<view class="video-time">{{ formatTime(item.createtime) }}</view>
							</view>
							<view class="video-desc" v-if="item.videodesc">{{ item.videodesc }}</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 加载更多 - 只在有数据且正在加载时显示 -->
			<view class="loading-more" v-if="list.length > 0 && isLoading">
				<text class="loading-text">加载中...</text>
			</view>

			<!-- 空状态 - 只在不加载且无数据时显示 -->
			<view class="empty-state" v-if="!isLoading && list.length === 0">
				<text class="empty-text">暂无视频内容</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				list: [],
				fetchPageNum: 1,
				isLoading: false,
				searchKey: '',
				isSearching: false,
				showFilter: false,
				selectedPlatform: '',
				selectedTag: '',
				searchRequestId: 0, // 添加请求ID来处理竞态条件
				searchTimer: null // 添加搜索防抖定时器
			}
		},
		onLoad() {
			console.log('页面加载，开始获取数据');
			this.getData(this.fetchPageNum);
		},
		
		/**
		 * 页面卸载时清理定时器
		 */
		onUnload() {
			console.log('页面卸载，清理定时器');
			if (this.searchTimer) {
				clearTimeout(this.searchTimer);
				this.searchTimer = null;
			}
		},
		
		/**
		 * 下拉刷新
		 */
		onPullDownRefresh() {
			console.log('下拉刷新');
			this.fetchPageNum = 1;
			this.list = [];
			this.getData(this.fetchPageNum);
			setTimeout(() => {
				uni.stopPullDownRefresh();
			}, 1000);
		},
		
		/**
		 * 上拉加载更多
		 */
		onReachBottom() {
			if (!this.isLoading) {
				console.log('上拉加载更多');
				this.getData(this.fetchPageNum);
			}
		},
		methods: {
			/**
			 * 防抖搜索方法
			 */
			debounceSearch() {
				// 清除之前的定时器
				if (this.searchTimer) {
					clearTimeout(this.searchTimer);
				}
				
				// 设置新的定时器
				this.searchTimer = setTimeout(() => {
					this.handleSearch();
				}, 300); // 300ms 防抖延迟
			},
			
			/**
			 * 清空搜索条件
			 */
			clearSearch() {
				console.log('清空搜索条件');
				// 清除搜索防抖定时器
				if (this.searchTimer) {
					clearTimeout(this.searchTimer);
					this.searchTimer = null;
				}
				
				this.searchKey = '';
				this.isSearching = false;
				this.isLoading = false; // 重置加载状态
				this.fetchPageNum = 1;
				this.list = []; // 先清空列表
				this.getData(this.fetchPageNum);
			},
			
			/**
			 * 切换筛选面板显示状态
			 */
			toggleFilter() {
				this.showFilter = !this.showFilter;
			},
			
			/**
			 * 跳转到咻咻页面
			 */
			goToWhoosh() {
				console.log('跳转到咻咻页面');
				uni.navigateTo({
					url: '/pages/video/whoosh'
				});
			},
			
			/**
			 * 平台输入处理
			 */
			onPlatformInput() {
				// 实时筛选可以在这里添加防抖逻辑
				console.log('平台输入变化:', this.selectedPlatform);
			},
			
			/**
			 * 标签输入处理
			 */
			onTagInput() {
				// 实时筛选可以在这里添加防抖逻辑
				console.log('标签输入变化:', this.selectedTag);
			},
			
			/**
			 * 清空筛选条件
			 */
			clearFilters() {
				console.log('清空筛选条件');
				this.selectedPlatform = '';
				this.selectedTag = '';
				this.applyFilters();
			},
			
			/**
			 * 应用筛选条件
			 */
			applyFilters() {
				console.log('应用筛选条件');
				this.fetchPageNum = 1;
				this.isSearching = true;
				this.isLoading = false; // 重置加载状态
				this.showFilter = false; // 应用筛选后关闭筛选面板
				this.list = []; // 先清空列表
				this.getData(this.fetchPageNum);
			},
			
			/**
			 * 图片加载完成处理
			 */
			onImageLoad(index) {
				// console.log('图片加载完成:', index);
				if (this.list[index]) {
					this.$set(this.list[index], 'imageLoading', false);
					this.$set(this.list[index], 'imageError', false);
				}
			},
			
			/**
			 * 图片加载失败处理
			 */
			onImageError(index) {
				// console.log('图片加载失败:', index);
				if (this.list[index]) {
					this.$set(this.list[index], 'imageLoading', false);
					this.$set(this.list[index], 'imageError', true);
				}
			},
			
			/**
			 * 切换播放按钮显示状态
			 */
			togglePlayBtn(index) {
				if (this.list[index]) {
					const currentState = this.list[index].showPlayBtn || false;
					this.$set(this.list[index], 'showPlayBtn', !currentState);
				}
			},
			
			/**
			 * 显示播放按钮（移动端触摸）
			 */
			showPlayButton(index) {
				if (this.list[index]) {
					this.$set(this.list[index], 'showPlayBtn', true);
				}
			},
			
			/**
			 * 隐藏播放按钮（移动端触摸结束）
			 */
			hidePlayButton(index) {
				if (this.list[index]) {
					setTimeout(() => {
						this.$set(this.list[index], 'showPlayBtn', false);
					}, 2000); // 2秒后自动隐藏
				}
			},
			
			/**
			 * 格式化时间显示
			 */
			formatTime(timeStr) {
				if (!timeStr) return '';
				try {
					const date = new Date(timeStr);
					const now = new Date();
					const diff = now - date;
					const days = Math.floor(diff / (1000 * 60 * 60 * 24));
					
					if (days === 0) {
						return '今天';
					} else if (days === 1) {
						return '昨天';
					} else if (days < 7) {
						return `${days}天前`;
					} else {
						return date.toLocaleDateString();
					}
				} catch (e) {
					return timeStr;
				}
			},
			
			/**
			 * 处理搜索请求
			 */
			handleSearch() {
				console.log('开始搜索，关键词:', this.searchKey);
				
				// 防止重复搜索
				if (this.isLoading) {
					console.log('正在搜索中，忽略重复请求');
					return;
				}
				
				// 增加请求ID，用于处理竞态条件
				this.searchRequestId++;
				const currentRequestId = this.searchRequestId;
				
				this.isSearching = true;
				this.isLoading = true; // 设置加载状态
				this.fetchPageNum = 1; // 重置页码
				this.list = []; // 清空列表，显示骨架屏
				
				var that = this;
				var serveraddr = uni.getStorageSync('serveraddr');
				var serverport = uni.getStorageSync('serverport');
				var servertoken = uni.getStorageSync('servertoken');
				var api = `${serveraddr}:${serverport}/api/findVideos?token=${servertoken}`;
				
				uni.showLoading({
					title: '搜索中...'
				});
				
				// 构建搜索参数，包含筛选条件
				var searchData = {
					videodesc: that.searchKey,
					videoname: that.searchKey
				};
				
				// 添加筛选条件
				if (this.selectedPlatform) {
					searchData.videoplatform = this.selectedPlatform;
				}
				if (this.selectedTag) {
					searchData.videotag = this.selectedTag;
				}
				
				uni.request({
					url: api,
					method: "POST",
					header: {
						'content-type': 'application/x-www-form-urlencoded'
					},
					data: searchData,
					success(res) {
						// 检查是否是最新的请求
						if (currentRequestId !== that.searchRequestId) {
							console.log('忽略过期的搜索请求');
							return;
						}
						
						that.isLoading = false; // 重置加载状态
						
						if(res.data.resCode == "000001"){
							var content = res.data.record.content;
							for(var i = 0; i < content.length; i++){
								content[i].videounrealaddr = `${serveraddr}:${serverport}${content[i].videounrealaddr}?apptoken=${servertoken}`;
								content[i].videocover = `${serveraddr}:${serverport}${content[i].videocover}?apptoken=${servertoken}`;
								// 为搜索结果添加图片懒加载状态
								content[i].imageLoading = true;
								content[i].imageError = false;
								content[i].showPlayBtn = false; // 初始化播放按钮状态
							}
							that.list = content;
							that.fetchPageNum = 2; // 设置下一页页码
							console.log('搜索完成，获取到', content.length, '条数据');
						} else {
							uni.showToast({
								title: res.data.resMsg || '搜索失败',
								icon: 'none'
							});
						}
					},
					fail(error) {
						// 检查是否是最新的请求
						if (currentRequestId !== that.searchRequestId) {
							console.log('忽略过期的搜索请求错误');
							return;
						}
						
						that.isLoading = false; // 重置加载状态
						console.error('搜索请求失败:', error);
						uni.showToast({
							title: '搜索失败',
							icon: 'none'
						});
					},
					complete() {
						// 检查是否是最新的请求
						if (currentRequestId !== that.searchRequestId) {
							return;
						}
						
						uni.hideLoading();
					}
				});
			},
			
			/**
			 * 获取视频数据
			 */
			getData(page) {
				console.log('获取数据，页码:', page);
				
				// 防止重复请求
				if (this.isLoading) {
					console.log('正在加载中，忽略重复请求');
					return;
				}
				
				this.isLoading = true;
				
				var that = this;
				var option = {
					pageNo: that.fetchPageNum
				};
				
				// 添加筛选条件
				if (this.selectedPlatform) {
					option.videoplatform = this.selectedPlatform;
				}
				if (this.selectedTag) {
					option.videotag = this.selectedTag;
				}
				
				var serveraddr = uni.getStorageSync('serveraddr');
				var serverport = uni.getStorageSync('serverport');
				var servertoken = uni.getStorageSync('servertoken');
				var api = `${serveraddr}:${serverport}/api/findVideos?token=${servertoken}`;
				
				console.log('请求参数:', option);
				
				uni.request({
					url: api,
					method: "POST",
					header: {
						'content-type': 'application/x-www-form-urlencoded'
					},
					data: option,
					success(res) {
						that.isLoading = false;
						console.log('获取数据成功:', res);
						
						if(res.data.resCode == "000001"){
							var temp = that.list;
							
							// 如果是第一页，显示刷新成功提示
							if (that.fetchPageNum === 1) {
								that.list = [];
								uni.showToast({
								    icon: 'none',
								    title: '刷新成功'
								});
							}
							
							var content = res.data.record.content;
							
							// 为新数据添加图片懒加载状态
							for(var i = 0; i < content.length; i++){
								content[i].videounrealaddr = `${serveraddr}:${serverport}${content[i].videounrealaddr}?apptoken=${servertoken}`;
								content[i].videocover = `${serveraddr}:${serverport}${content[i].videocover}?apptoken=${servertoken}`;
								content[i].imageLoading = true;
								content[i].imageError = false;
								content[i].showPlayBtn = false; // 初始化播放按钮状态
							}
							
							that.list = temp.concat(content);
							that.fetchPageNum = that.fetchPageNum + 1;
							console.log('当前列表长度:', that.list.length);
						} else {
							uni.showToast({
								title: res.data.resMsg || '获取数据失败',
								icon: 'none'
							});
						}
					},
					fail(error) {
						that.isLoading = false;
						console.error('网络请求失败:', error);
						uni.showToast({
							title: '网络请求失败',
							icon: 'none'
						});
					}
				});
			},
			/**
			 * 播放视频
			 */
			playVideo(url) {
				// 找到当前视频的完整信息
				const currentVideo = this.list.find(item => item.videounrealaddr === url);
				if (currentVideo) {
					if (currentVideo.videoprivacy === 1) {
						uni.showModal({
							title: '请输入密码',
							editable: true,
							placeholderText: '请输入访问密码',
							success: (res) => {
								if (res.confirm && res.content === this.PASSWORD) {
									this.navigateToVideo(currentVideo);
								} else if (res.confirm) {
									uni.showToast({
										title: '密码错误',
										icon: 'none'
									});
								}
							}
						});
					} else {
						this.navigateToVideo(currentVideo);
					}
				}
			},
			navigateToVideo(videoInfo) {
				uni.navigateTo({
					url: `/pages/video/videoPlay?videoInfo=${encodeURIComponent(JSON.stringify(videoInfo))}`
				});
			},
		}
	}
</script>

<style>
.container {
	min-height: 100vh;
	background: #f6f7f8;
}

.search-header {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	z-index: 100;
	background: #ffffff;
	padding: 16rpx 24rpx;
	box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	border-bottom: 1rpx solid #f0f0f0;
}

.search-box {
	display: flex;
	align-items: center;
	gap: 12rpx;
}

.search-input-wrapper {
	flex: 1;
	display: flex;
	align-items: center;
	background: #fff;
	border: 1rpx solid #e9ecef;
	border-radius: 24rpx;
	padding: 12rpx 20rpx;
	gap: 8rpx;
}

.search-input {
	flex: 1;
	height: 48rpx;
	font-size: 28rpx;
	color: #333;
	border: none;
	outline: none;
	background: transparent;
}

.search-btn {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 48rpx;
	height: 48rpx;
	border-radius: 50%;
	background: #f8f9fa;
	transition: all 0.3s;
	cursor: pointer;
}

.search-btn:hover {
	background: #e9ecef;
}

.search-btn:active {
	transform: scale(0.95);
	background: #dee2e6;
}

.placeholder {
	color: #999;
}

.filter-btn {
	background: #007aff;
	border: none;
	border-radius: 20rpx;
	padding: 12rpx 20rpx;
	color: white;
	font-size: 24rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	transition: all 0.3s;
}

.filter-btn:active {
	background: #0056cc;
	transform: scale(0.98);
}

.whoosh-btn {
	background: #ff2d55;
	border: none;
	border-radius: 20rpx;
	padding: 12rpx 20rpx;
	color: white;
	font-size: 24rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	transition: all 0.3s;
	margin-left: 12rpx;
}

.whoosh-btn:active {
	background: #e6194b;
	transform: scale(0.98);
}

.filter-panel {
	background: white;
	border-radius: 16rpx;
	padding: 24rpx;
	margin-top: 16rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.08);
	border: 1rpx solid #f0f0f0;
	animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
	from {
		opacity: 0;
		transform: translateY(-20rpx);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

.filter-row {
	margin-bottom: 24rpx;
}

.filter-row:last-child {
	margin-bottom: 0;
}

.filter-label {
	font-size: 26rpx;
	font-weight: 500;
	color: #333;
	margin-bottom: 12rpx;
	display: block;
}

.filter-input {
	width: 100%;
	height: 72rpx;
	padding: 0 16rpx;
	background: #f8f9fa;
	border: 1rpx solid #e9ecef;
	border-radius: 12rpx;
	font-size: 26rpx;
	color: #333;
	box-sizing: border-box;
}

.filter-input:focus {
	border-color: #007aff;
	background: #ffffff;
}

.filter-actions {
	display: flex;
	gap: 16rpx;
	margin-top: 24rpx;
}

.filter-action-btn {
	flex: 1;
	height: 72rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	border-radius: 12rpx;
	font-size: 26rpx;
	transition: all 0.3s;
}

.clear-btn {
	background: #f8f9fa;
	color: #666;
	border: 1rpx solid #e9ecef;
}

.clear-btn:active {
	background: #e9ecef;
}

.apply-btn {
	background: #007aff;
	color: white;
}

.apply-btn:active {
	background: #0056cc;
}

.content-wrap {
	padding-top: 120rpx;
}

/* 骨架屏样式 */
.skeleton-card .video-cover.skeleton-cover {
	background: #f5f5f5;
	position: relative;
	overflow: hidden;
}

.skeleton-card .video-info.skeleton-info .video-title.skeleton-title {
	height: 40rpx;
	border-radius: 8rpx;
	overflow: hidden;
	margin-bottom: 16rpx;
	background: #f5f5f5;
}

.skeleton-card .video-info.skeleton-info .video-meta.skeleton-meta {
	height: 28rpx;
	border-radius: 8rpx;
	overflow: hidden;
	margin-bottom: 12rpx;
	background: #f5f5f5;
}

.skeleton-card .video-info.skeleton-info .video-desc.skeleton-desc {
	height: 32rpx;
	border-radius: 8rpx;
	overflow: hidden;
	background: #f5f5f5;
}

.skeleton-shimmer {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
	background-size: 200% 100%;
	animation: loading 1.5s infinite;
}

@keyframes loading {
	0% {
		background-position: 200% 0;
	}
	100% {
		background-position: -200% 0;
	}
}

.video-list {
	display: flex;
	flex-wrap: wrap;
	padding: 0 12rpx;
}

.video-card {
	width: 50%;
	padding: 6rpx;
	box-sizing: border-box;
}

.video-card-inner {
	background: #fff;
	border-radius: 12rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
	height: 360rpx;
	display: flex;
	flex-direction: column;
	transition: transform 0.2s, box-shadow 0.2s;
}

.video-card:active .video-card-inner {
	transform: translateY(2rpx);
	box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.12);
}

.video-cover {
	position: relative;
	width: 100%;
	padding-top: 56.25%; /* 16:9 比例 */
	flex-shrink: 0;
}

.cover-image {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	object-fit: cover;
	transition: opacity 0.3s;
}

.image-skeleton {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: #f0f0f0;
	overflow: hidden;
}

.image-error {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: #f5f5f5;
	display: flex;
	align-items: center;
	justify-content: center;
}

.error-text {
	font-size: 20rpx;
	color: #999;
	text-align: center;
}

.privacy-cover {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: rgba(255, 255, 255, 0.3);
	backdrop-filter: blur(10px);
	-webkit-backdrop-filter: blur(10px);
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 15;
	border-radius: 8rpx;
	border: 1px solid rgba(255, 255, 255, 0.2);
}

.play-btn {
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		width: 60rpx;
		height: 60rpx;
		background: rgba(0, 0, 0, 0.6);
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 10;
		opacity: 0;
		visibility: hidden;
		transition: all 0.3s ease;
		pointer-events: none;
	}
	
	.play-btn.show {
		opacity: 1;
		visibility: visible;
		pointer-events: auto;
		transform: translate(-50%, -50%) scale(1.1);
	}

.play-btn:active {
	transform: translate(-50%, -50%) scale(0.9);
}

.video-info {
	padding: 12rpx 16rpx;
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.video-title {
	font-size: 26rpx;
	font-weight: 500;
	color: #333;
	line-height: 1.4;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
	text-overflow: ellipsis;
	height: 72rpx;
}

.video-desc {
	font-size: 22rpx;
	color: #999;
	line-height: 1.3;
	display: -webkit-box;
	-webkit-line-clamp: 1;
	-webkit-box-orient: vertical;
	overflow: hidden;
	text-overflow: ellipsis;
	height: 28rpx;
}

.video-meta {
	display: flex;
	align-items: center;
	flex-wrap: wrap;
	gap: 8rpx;
	margin: 8rpx 0;
}

.video-platform {
	padding: 4rpx 8rpx;
	background: #e3f2fd;
	color: #1976d2;
	border-radius: 8rpx;
	font-size: 20rpx;
	flex-shrink: 0;
}

.video-tag {
	padding: 4rpx 8rpx;
	background: #f3e5f5;
	color: #7b1fa2;
	border-radius: 8rpx;
	font-size: 20rpx;
	flex-shrink: 0;
}

.video-time {
	font-size: 20rpx;
	color: #999;
	margin-left: auto;
	flex-shrink: 0;
}

.loading-more {
	text-align: center;
	padding: 30rpx 0;
}

.loading-text {
	font-size: 26rpx;
	color: #999;
}

.empty-state {
	padding: 120rpx 0;
	text-align: center;
}

.empty-text {
	font-size: 28rpx;
	color: #999;
}

.video-player {
	width: 100vw;
	height: 56.25vw; /* 16:9 比例 */
	background: #000;
	position: relative;
}

.player {
	width: 100%;
	height: 100%;
}

/* 下拉刷新样式 */
.uni-refresh {
	background: #f6f7f8 !important;
}

.uni-refresh-icon {
	color: #fb7299 !important;
}

/* 响应式优化 */
@media (max-width: 750rpx) {
	.video-card {
		width: 100%;
	}
	
	.video-card-inner {
		height: 280rpx;
	}
	
	.video-info {
		padding: 12rpx;
	}
	
	.video-title {
		font-size: 26rpx;
	}
}
</style>
