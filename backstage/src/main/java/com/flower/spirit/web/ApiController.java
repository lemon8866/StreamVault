package com.flower.spirit.web;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.InputStream;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.Global;
import com.flower.spirit.entity.VideoDataEntity;
import com.flower.spirit.service.AnalysisService;
import com.flower.spirit.service.VideoDataService;
import com.flower.spirit.service.ConfigService;

/**
 * api 调用控制器 此处控制器不拦截  仅通过token 校验
 * @author flower
 *
 */
@RestController
@RequestMapping("/api")
public class ApiController {
	
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
}
