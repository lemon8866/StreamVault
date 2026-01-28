package com.flower.spirit.executor;

import okhttp3.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.GraphicContentDao;
import com.flower.spirit.entity.GraphicContentEntity;
import com.flower.spirit.entity.ProcessHistoryEntity;
import com.flower.spirit.service.ProcessHistoryService;
import com.flower.spirit.utils.DouUtil;
import com.flower.spirit.utils.FileUtil;
import com.flower.spirit.utils.HttpUtil;
import com.flower.spirit.utils.StringUtil;
import com.flower.spirit.utils.sendNotify;

@Service
public class WeiBoExecutor {
	
	private static Logger logger = LoggerFactory.getLogger(WeiBoExecutor.class);
	
	
	@Autowired
	private GraphicContentDao graphicContentDao;
	
	@Autowired
	private ProcessHistoryService processHistoryService;
	

    private static final String WEIBO_IMG_DOMAIN = "https://wx4.sinaimg.cn";
    

    private static final String HIGHEST_QUALITY_SUFFIX = "/mw2000";
	
	
	
    // 微博ID提取正则表达式
    private static final Pattern WEIBO_ID_PATTERN = Pattern.compile(
        "(?:https?://)?(?:www\\.)?(?:weibo\\.com|weibo\\.cn|m\\.weibo\\.cn)/(?:\\d{10}|status)/(\\w{9}|\\w{16})(?:/|\\?|#.*$|$)"
    );
    
    private static String showDetail = "https://weibo.com/ajax/statuses/show";
    private static OkHttpClient client;
    static {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }
    
    public void dataExecutor(String weibourl) throws IOException {
    	//校验参数
    	String weibocookie = Global.cookie_manage.getWeibocookie();
    	if(null== weibocookie || weibocookie.equals("")) {
    		logger.error("weibo cookie未设置  当前不执行解析");
    	}
    	String weiboId = extractWeiboId(weibourl);
		Optional<GraphicContentEntity> byVideoidAndPlatform = graphicContentDao.findByVideoidAndPlatform(weiboId,Global.platform.weibo.name());
		if(byVideoidAndPlatform.isPresent()) {
			return;
		}
		
    	ProcessHistoryEntity saveProcess = processHistoryService.saveProcess(null, weibourl, "微博");
    	if(weiboId != null) {
    		String fetchWeiboDetail = fetchWeiboDetail(weiboId);
    		
    		HashMap<String, String> header = new HashMap<String, String>();
			header.put("Referer", "https://weibo.com/");
			header.put("User-Agent", DouUtil.ua);
			header.put("cookie", Global.cookie_manage.getWeibocookie());
			
			//构建存储位置
			GraphicContentEntity graphicContentEntity = new GraphicContentEntity();
			graphicContentEntity.setVideoid(weiboId);
			graphicContentEntity.setPlatform(Global.platform.weibo.name());
    		JSONObject object = JSONObject.parseObject(fetchWeiboDetail);
    		String title = object.getString("text");
    		String text_raw = object.getString("text_raw");
    		String username = object.getJSONObject("user").getString("screen_name");
    		MediaInfo mediaInfo = extractMediaInfo(object);
    		String videoUrl = mediaInfo.getVideoUrl();
    		List<String> imageUrls = mediaInfo.getImageUrls();
    		if ((videoUrl == null || videoUrl.isBlank()) 
    		        && (imageUrls == null || imageUrls.isEmpty())) {
    		    logger.info("weibo url 没有图片或视频");
    		    return;
    		}
    		JSONArray imageList=  new JSONArray();
    		boolean isVideo = mediaInfo.isVideo;
			String filename = StringUtil.getFileName(text_raw, weiboId);
			String markroute = FileUtil.generateDir(true, Global.platform.weibo.name(), filename, null, null,0);
    		if(isVideo) {
    			//视频类型就一个文件
    			String storage = FileUtil.generateDir(true, Global.platform.weibo.name(), filename, null, null,0);
				String cos = FileUtil.generateDir(false, Global.platform.weibo.name(), filename, null, "mp4",0);
				HttpUtil.downloadFileWithOkHttp(videoUrl, filename+"-index-"+0 + ".mp4", storage, header);
				imageList.add(cos);
    		}else {
    			for(int i =0;i<imageUrls.size();i++) {
    				 String storage = FileUtil.generateDir(true, Global.platform.weibo.name(), filename, null, null,i);
					 String cos = FileUtil.generateDir(false, Global.platform.weibo.name(), filename, null, "jpeg",i);
					 HttpUtil.downloadFileWithOkHttp(imageUrls.get(i), filename+"-index-"+i + ".jpeg", storage, header);
					 imageList.add(cos);
    			}
    		}
			graphicContentEntity.setOriginaladdress(weibourl);
			graphicContentEntity.setTitle(StringUtil.getFileName(text_raw, weiboId));
			graphicContentEntity.setMarkroute(markroute);
			graphicContentEntity.setContent(StringUtil.cleanText(text_raw));
			graphicContentEntity.setImages(imageList.toJSONString());
			graphicContentEntity.setAuthor(username);
			graphicContentEntity.setCreatetime(new Date());
			graphicContentDao.save(graphicContentEntity);
			sendNotify.sendNotifyData(filename, weibourl, "微博");
			processHistoryService.saveProcess(saveProcess.getId(), weibourl, "微博");
    		return;
    	}
    	logger.error("weibo url id 解析错误,请提交对应链接 到issues");
    	
    	
    }
    public static String extractWeiboId(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }
        
        Matcher matcher = WEIBO_ID_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    /**
     * 使用OkHttp和cookies发送微博详情请求
     * @param weiboId 微博ID
     * @return 微博详情JSON字符串，失败返回null
     */
    public String fetchWeiboDetail(String weiboId) {     
        try {
            // 构建请求URL
            HttpUrl.Builder urlBuilder = HttpUrl.parse(showDetail).newBuilder();
            urlBuilder.addQueryParameter("id", weiboId);
            urlBuilder.addQueryParameter("locale", "zh-CN");
            String url = urlBuilder.build().toString();
            // 构建请求
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Cookie", Global.cookie_manage.getWeibocookie())
                    .addHeader("User-Agent", DouUtil.ua)
                    .addHeader("Accept", "application/json, text/plain, */*")
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                    .addHeader("Referer", "https://weibo.com/")
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .get()
                    .build();
            
            // 执行请求
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    return responseBody;
                } else {
                    System.err.println("请求失败，状态码: " + response.code());
                    System.err.println("错误信息: " + response.message());
                    if (response.body() != null) {
                        System.err.println("响应内容: " + response.body().string());
                    }
                    return null;
                }
            }
            
        } catch (IOException e) {
            System.err.println("网络请求异常: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("请求微博详情时发生未知错误: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    
    /**
     * 微博请求回调接口
     */
    public interface WeiboCallback {
        /**
         * 请求成功回调
         * @param result 响应数据
         */
        void onSuccess(String result);
        
        /**
         * 请求失败回调
         * @param error 错误信息
         */
        void onFailure(String error);
    }
    
    /**
     * 关闭HTTP客户端资源
     */
    public static void shutdown() {
        if (client != null) {
            client.dispatcher().executorService().shutdown();
            client.connectionPool().evictAll();
            System.out.println("HTTP客户端资源已释放");
        }
    }
    
    /**
     * 从微博详情JSON中提取媒体资源信息
     * @param weiboDetailJson 微博详情的JSON对象
     * @return 媒体资源信息对象
     */
    public static MediaInfo extractMediaInfo(JSONObject weiboDetailJson) {
        logger.info("开始提取微博媒体资源信息");
        
        MediaInfo mediaInfo = new MediaInfo();
        
        try {
            // 提取图片URL列表
            List<String> imageUrls = extractHighestQualityImages(weiboDetailJson);
            mediaInfo.setImageUrls(imageUrls);
            logger.info("成功提取图片数量: " + imageUrls.size());
            
            // 提取视频URL
            String videoUrl = extractHighestQualityVideo(weiboDetailJson);
            if (videoUrl != null && !videoUrl.isEmpty()) {
                mediaInfo.setVideoUrl(videoUrl);
                logger.info("成功提取视频URL: " + videoUrl);
            }
            
            // 判断是否为视频微博
            boolean isVideo = isVideoWeibo(weiboDetailJson);
            mediaInfo.setVideo(isVideo);
            
        } catch (Exception e) {
            logger.error("提取媒体资源时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
        
        return mediaInfo;
    }
    
    /**
     * 提取最高清晰度的图片URL列表
     * @param weiboJson 微博JSON对象
     * @return 图片URL列表
     */
    private static List<String> extractHighestQualityImages(JSONObject weiboJson) {
        List<String> imageUrls = new ArrayList<>();
        try {
            JSONArray picIds = weiboJson.getJSONArray("pic_ids");
            if (picIds != null && picIds.size() > 0) {
                logger.info("发现图片ID数量: " + picIds.size());
                for (int i = 0; i < picIds.size(); i++) {
                    String picId = picIds.getString(i);
                    if (picId != null && !picId.trim().isEmpty()) {
                        // 构建最高清晰度图片URL
                        String imageUrl = buildHighestQualityImageUrl(picId);
                        imageUrls.add(imageUrl);
                        logger.info("构建图片URL: " + imageUrl);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("提取图片URL时发生错误: " + e.getMessage());
        }
        return imageUrls;
    }
    
    /**
     * 构建最高清晰度图片URL
     * @param picId 图片ID
     * @return 完整的图片URL
     */
    private static String buildHighestQualityImageUrl(String picId) {
        // 根据微博API规则构建URL: 域名 + 清晰度后缀 + 图片ID
        return WEIBO_IMG_DOMAIN + HIGHEST_QUALITY_SUFFIX + "/" + picId;
    }
    
    /**
     * 提取最高清晰度的视频URL
     * @param weiboJson 微博JSON对象
     * @return 最高清晰度视频URL，如果没有视频则返回null
     */
    private static String extractHighestQualityVideo(JSONObject weiboJson) {
        try {
            if (!isVideoWeibo(weiboJson)) {
                return null;
            }
            JSONObject pageInfo = weiboJson.getJSONObject("page_info");
            if (pageInfo == null) {
                return null;
            }
            JSONObject mediaInfo = pageInfo.getJSONObject("media_info");
            if (mediaInfo == null) {
                return null;
            }
           
            JSONArray playbackList = mediaInfo.getJSONArray("playback_list");
            if (playbackList == null || playbackList.size() == 0) {
                return null;
            }
            String highestQualityUrl = null;
            int maxBitrate = 0;
            
            for (int i = 0; i < playbackList.size(); i++) {
                JSONObject playbackItem = playbackList.getJSONObject(i);
                if (playbackItem != null) {
                    JSONObject playInfo = playbackItem.getJSONObject("play_info");
                    if (playInfo != null) {
                        int bitrate = playInfo.getIntValue("bitrate");
                        String url = playInfo.getString("url");
                        if (url != null && bitrate > maxBitrate) {
                            maxBitrate = bitrate;
                            highestQualityUrl = url;
                        }
                    }
                }
            }
            if (highestQualityUrl != null) {
                logger.info("找到最高码率视频: " + maxBitrate + "kbps");
            }
            
            return highestQualityUrl;
            
        } catch (Exception e) {
            logger.error("提取视频URL时发生错误: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 判断是否为视频微博
     * @param weiboJson 微博JSON对象
     * @return 如果是视频微博返回true，否则返回false
     */
    private static boolean isVideoWeibo(JSONObject weiboJson) {
        try {
            JSONObject pageInfo = weiboJson.getJSONObject("page_info");
            if (pageInfo != null) {
                // 检查type字段，值为11表示视频 (对应Python中的 $.page_info.type)
                Object type = pageInfo.get("type");
                Object object_type = pageInfo.get("object_type");
                if(object_type != null) {
                	String objectTypeStr = object_type.toString();
                	return "video".equals(objectTypeStr);
                }
                if (type != null) {
                    String typeStr = type.toString();
                    return "11".equals(typeStr) || "5".equals(typeStr) ;
                }  
            }
        } catch (Exception e) {
            logger.error("判断视频类型时发生错误: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * 媒体资源信息类
     */
    public static class MediaInfo {
        private List<String> imageUrls = new ArrayList<>();
        private String videoUrl;
        private boolean isVideo;
        
        public List<String> getImageUrls() {
            return imageUrls;
        }
        
        public void setImageUrls(List<String> imageUrls) {
            this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
        }
        
        public String getVideoUrl() {
            return videoUrl;
        }
        
        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }
        
        public boolean isVideo() {
            return isVideo;
        }
        
        public void setVideo(boolean video) {
            isVideo = video;
        }
        
        /**
         * 获取所有媒体URL（图片+视频）
         * @return 所有媒体URL列表
         */
        public List<String> getAllMediaUrls() {
            List<String> allUrls = new ArrayList<>(imageUrls);
            if (videoUrl != null && !videoUrl.isEmpty()) {
                allUrls.add(videoUrl);
            }
            return allUrls;
        }
        
        /**
         * 检查是否包含媒体资源
         * @return 如果包含图片或视频返回true
         */
        public boolean hasMedia() {
            return (!imageUrls.isEmpty()) || (videoUrl != null && !videoUrl.isEmpty());
        }
        
        @Override
        public String toString() {
            return "MediaInfo{" +
                    "图片数量=" + imageUrls.size() +
                    ", 视频URL='" + videoUrl + '\'' +
                    ", 是否为视频=" + isVideo +
                    '}';
        }
    }
    
    
    public static void main(String[] args) {
    	WeiBoExecutor executor = new WeiBoExecutor();
    	String result = executor.fetchWeiboDetail("O8DM0BLLm");
    	JSONObject weiboJson = JSONObject.parseObject(result);
        MediaInfo mediaInfo = extractMediaInfo(weiboJson);
        System.out.println("提取结果: " + mediaInfo);
        System.out.println("图片URLs: " + mediaInfo.getImageUrls());
        System.out.println("视频URL: " + mediaInfo.getVideoUrl());
//    	System.out.println(result);
	}
}