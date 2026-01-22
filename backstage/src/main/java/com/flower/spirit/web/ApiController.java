package com.flower.spirit.web;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.InputStream;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.Global;
import com.flower.spirit.entity.VideoDataEntity;
import com.flower.spirit.service.AnalysisService;
import com.flower.spirit.service.VideoDataService;
import com.flower.spirit.service.ConfigService;
import com.flower.spirit.utils.DouUtil;
import com.flower.spirit.utils.YtDlpUtil;

/**
 * api 调用控制器 此处控制器不拦截  仅通过token 校验
 * @author flower
 *
 */
@RestController
@RequestMapping("/api")
public class ApiController {
	
	private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
	
	@Autowired
	private AnalysisService analysisService;
	
//	private ExecutorService exec = Executors.newFixedThreadPool(1);
	
	@Autowired
	private VideoDataService videoDataService;
	
	@Autowired
	private ConfigService configService;
	
	
	/**
	 * 接受 视频平台的分享链接
	 * @param token
	 * @param video
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/processingVideos")
	@CrossOrigin
	public AjaxEntity processingVideos(String token,String video) {
//		 analysisService.processingVideos(token,video);
		try {
			analysisService.processingVideos(token,video);
		} catch (Exception e) {
			System.err.println("线程中异常 先打印 不一定有用 标记");
		}
		return new AjaxEntity(Global.ajax_success, "已提交,等待系统处理", "");
	
	}

	
	/**
	 * app 或者小程序 分页获取视频列表功能 接口
	 * @param req
	 * @param res
	 * @return
	 */
	@RequestMapping("/findVideos")
	public AjaxEntity findVideos(HttpServletRequest req,VideoDataEntity res) {
		String token = req.getParameter("token");
		if (!(Objects.equals(token, Global.apptoken) || Objects.equals(token, Global.readonlytoken))) {
		    return new AjaxEntity(Global.ajax_uri_error, "app token 错误", null);
		}
		return videoDataService.findPage(res);
	}
	
	
	@PostMapping("/cookieCloud/update")
	public ResponseEntity<?> cookieCloud(HttpServletRequest request) {
	    try {
	        String contentEncoding = request.getHeader("Content-Encoding");
	        String jsonBody;
	        if ("gzip".equalsIgnoreCase(contentEncoding)) {
	            try (GZIPInputStream gis = new GZIPInputStream(request.getInputStream());
	                 InputStreamReader isr = new InputStreamReader(gis);
	                 BufferedReader reader = new BufferedReader(isr)) {
	                jsonBody = reader.lines().collect(Collectors.joining("\n"));
	            }
	        } else {
	            try (BufferedReader reader = request.getReader()) {
	                jsonBody = reader.lines().collect(Collectors.joining("\n"));
	            }
	        }
	        ObjectMapper objectMapper = new ObjectMapper();
	        Map<String, String> payload = objectMapper.readValue(jsonBody, new TypeReference<>() {});
	        String uuid = payload.get("uuid");
	        String encrypted = payload.get("encrypted");
	        String cryptoType = payload.getOrDefault("crypto_type", "legacy");

	        if (uuid == null || uuid.trim().isEmpty() ||
	            encrypted == null || encrypted.trim().isEmpty()) {
	            return ResponseEntity.badRequest().body("Missing required fields: uuid or encrypted");
	        }
	        String source = request.getHeader("application-source");
	        configService.cookieCloud(uuid, encrypted, cryptoType,source);
	        return ResponseEntity.ok(Map.of("action", "done"));

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(500).body("Internal Server Error");
	    }
	}
	
	/**
	 * 解析视频用于本地下载
	 * @param token
	 * @param video
	 * @return
	 */
	@RequestMapping("/parseVideoForLocal")
	@CrossOrigin
	public AjaxEntity parseVideoForLocal(String token, String video) {
		try {
			// 1. 验证 token
			if (!(Objects.equals(token, Global.apptoken) || Objects.equals(token, Global.readonlytoken))) {
				return new AjaxEntity(Global.ajax_uri_error, "token 错误", null);
			}
			
			// 2. 获取平台信息和真实URL
			String platform = analysisService.getPlatform(video);
			String url = analysisService.getUrl(video);
			
			if (platform == null || url == null || url.isEmpty()) {
				return new AjaxEntity(Global.ajax_uri_error, "无法识别视频链接", null);
			}
			
			logger.info("解析视频 - 平台: {}, URL: {}", platform, url);
			
			Map<String, Object> result = new HashMap<>();
			
			// 3. 如果是抖音平台，使用 DouUtil
			if (platform.equals("抖音")) {
				Map<String, String> douData = DouUtil.downVideo(url);
				if (douData == null) {
					return new AjaxEntity(Global.ajax_uri_error, "解析失败", null);
				}
				
				result.put("platform", "抖音");
				result.put("videoUrl", douData.get("videoplay"));
				result.put("coverUrl", douData.get("cover"));
				result.put("title", douData.get("desc"));
				result.put("author", douData.get("nickname"));
				result.put("isDash", false);
				result.put("needReferer", true);
				result.put("referer", "https://www.douyin.com/");
				
			} else {
				// 4. 其他平台使用 yt-dlp
				String jsonStr = YtDlpUtil.execForJson(url, platform);
				
				// 添加JSON解析的错误处理
				JSONObject jsonObject;
				try {
					jsonObject = JSONObject.parseObject(jsonStr.trim());
					if (jsonObject == null) {
						return new AjaxEntity(Global.ajax_uri_error, "解析失败: JSON数据为空", null);
					}
				} catch (Exception e) {
					logger.error("JSON解析失败，原始数据: {}", jsonStr);
					return new AjaxEntity(Global.ajax_uri_error, "解析失败: JSON解析错误 - " + e.getMessage(), null);
				}
				
				result.put("platform", platform);
				result.put("title", jsonObject.getString("title"));
				result.put("author", jsonObject.getString("uploader"));
				result.put("coverUrl", jsonObject.getString("thumbnail"));
				result.put("duration", jsonObject.getInteger("duration"));
				
				// 检查是否是 playlist
				String entryType = jsonObject.getString("_type");
				if ("playlist".equals(entryType)) {
					return new AjaxEntity(Global.ajax_uri_error, "检测到播放列表，本地下载功能暂不支持播放列表。请使用服务器下载功能。", null);
				}
				
				// 获取视频URL - 改进逻辑以支持更多平台
				JSONArray formats = jsonObject.getJSONArray("formats");
				boolean isDash = false;
				String videoUrl = null;
				
				// 优先查找同时包含音视频的格式
				if (formats != null && formats.size() > 0) {
					logger.info("找到 {} 个格式", formats.size());
					
					// 第一遍：查找最佳的合并格式（同时包含音视频）
					JSONObject bestMergedFormat = null;
					int bestHeight = 0;
					
					for (int i = 0; i < formats.size(); i++) {
						JSONObject format = formats.getJSONObject(i);
						String vcodec = format.getString("vcodec");
						String acodec = format.getString("acodec");
						String formatUrl = format.getString("url");
						
						// 同时包含视频和音频
						if (vcodec != null && !vcodec.equals("none") && 
							acodec != null && !acodec.equals("none") &&
							formatUrl != null && !formatUrl.isEmpty()) {
							
							Integer height = format.getInteger("height");
							if (height != null && height > bestHeight) {
								bestHeight = height;
								bestMergedFormat = format;
							}
						}
					}
					
					// 如果找到合并格式，使用它
					if (bestMergedFormat != null) {
						videoUrl = bestMergedFormat.getString("url");
						logger.info("使用合并格式: 分辨率 {}p", bestHeight);
					}
					
					// 第二遍：如果没有合并格式，检查是否是DASH，并选择最佳视频流
					if (videoUrl == null) {
						boolean hasVideoOnly = false;
						boolean hasAudioOnly = false;
						JSONObject bestVideoFormat = null;
						int bestVideoHeight = 0;
						
						for (int i = 0; i < formats.size(); i++) {
							JSONObject format = formats.getJSONObject(i);
							String vcodec = format.getString("vcodec");
							String acodec = format.getString("acodec");
							String formatUrl = format.getString("url");
							
							if (formatUrl == null || formatUrl.isEmpty()) continue;
							
							// 只有视频
							if (vcodec != null && !vcodec.equals("none") && 
								(acodec == null || acodec.equals("none"))) {
								hasVideoOnly = true;
								Integer height = format.getInteger("height");
								if (height != null && height > bestVideoHeight) {
									bestVideoHeight = height;
									bestVideoFormat = format;
								}
							}
							
							// 只有音频
							if (acodec != null && !acodec.equals("none") && 
								(vcodec == null || vcodec.equals("none"))) {
								hasAudioOnly = true;
							}
						}
						
						isDash = hasVideoOnly && hasAudioOnly;
						
						// 使用最佳视频流（即使没有音频）
						if (bestVideoFormat != null) {
							videoUrl = bestVideoFormat.getString("url");
							logger.info("使用视频流: 分辨率 {}p, DASH模式: {}", bestVideoHeight, isDash);
						}
					}
				}
				
				// 如果formats中没有找到，尝试使用顶层的url字段
				if (videoUrl == null) {
					videoUrl = jsonObject.getString("url");
					if (videoUrl != null) {
						logger.info("使用顶层 URL 字段");
					}
				}
				
				// 如果还是没有找到视频URL
				if (videoUrl == null || videoUrl.isEmpty()) {
					logger.error("无法从JSON中提取视频URL，JSON keys: {}", jsonObject.keySet());
					return new AjaxEntity(Global.ajax_uri_error, "解析失败: 无法获取视频下载地址", null);
				}
				
				result.put("videoUrl", videoUrl);
				result.put("isDash", isDash);
				result.put("needReferer", false);
			}
			
			return new AjaxEntity(Global.ajax_success, "解析成功", result);
			
		} catch (Exception e) {
			logger.error("解析视频失败", e);
			e.printStackTrace();
			return new AjaxEntity(Global.ajax_uri_error, "解析失败: " + e.getMessage(), null);
		}
	}
}
