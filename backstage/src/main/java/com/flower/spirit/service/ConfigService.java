package com.flower.spirit.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.AppConfig;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.ConfigDao;
import com.flower.spirit.entity.ConfigEntity;
import com.flower.spirit.entity.CookiesConfigEntity;
import com.flower.spirit.entity.TikTokConfigEntity;
import com.flower.spirit.entity.VideoDataEntity;
import com.flower.spirit.utils.AESUtils;
import com.flower.spirit.utils.BeanUtil;
import com.flower.spirit.utils.MD5Util;
import com.flower.spirit.utils.StringUtil;
import com.flower.spirit.utils.YtDlpUtil;
import com.flower.spirit.utils.sendNotify;


@Service
public class ConfigService {
	
	@Autowired
	private ConfigDao  configDao;
	
	private Logger logger = LoggerFactory.getLogger(ConfigService.class);
	
	@Autowired
	private CookiesConfigService cookiesConfigService;
	
	@Autowired
	private TikTokConfigService tikTokConfigService;
	
	private static String cookiecloud ="";

	public ConfigEntity getData() {
		List<ConfigEntity> list =  configDao.findAll();
		return list.get(0);
	}

	public AjaxEntity saveConfig(ConfigEntity configEntity) {
		
		List<ConfigEntity> list =  configDao.findAll();
		ConfigEntity configData = list.get(0);
		BeanUtil.copyPropertiesIgnoreCase(configEntity,configData);
		configDao.save(configData);
		Global.apptoken =configEntity.getApptoken();
		if(configEntity.getGeneratenfo()!= null && configEntity.getGeneratenfo().equals("1")) {
			Global.getGeneratenfo =  true;
		}
		if(configEntity.getDanmudown()!= null && configEntity.getDanmudown().equals("1")) {
			Global.danmudown =  true;
		}
		if (configEntity.getAgenttype() != null && !configEntity.getAgenttype().trim().isEmpty() &&
				configEntity.getAgentaddress() != null && !configEntity.getAgentaddress().trim().isEmpty() &&
						configEntity.getAgentport() != null && !configEntity.getAgentport().trim().isEmpty()) {
			Global.proxyinfo = AppConfig.buildProxyArgument(configEntity);
			logger.info("已启动yt-dlp网络代理,代理地址:"+Global.proxyinfo); 
		}else {
			Global.proxyinfo=null;
		}
		if(null!=configEntity.getUseragent() && !"".equals(configEntity.getUseragent())) {
			Global.useragent = configEntity.getUseragent();
		}else {
			Global.useragent = null;
		}
		if(null!=configEntity.getReadonlytoken() && !"".equals(configEntity.getReadonlytoken())) {
			Global.readonlytoken = configEntity.getReadonlytoken();
		}else {
			Global.readonlytoken = null;
		}
		if(null!=configEntity.getYtdlpmode() && !"".equals(configEntity.getYtdlpmode())) {
			Global.ytdlpmode = configEntity.getYtdlpmode();
		}
//		if(null!=configEntity.getYtdlpargs() && !"".equals(configEntity.getYtdlpargs())) {
//			Global.ytdlpargs = configEntity.getYtdlpargs();
//		}
		if(null!=configEntity.getNfonetaddr() && !"".equals(configEntity.getNfonetaddr())) {
			Global.nfonetaddr = configEntity.getNfonetaddr();
		}
		if(null!=configEntity.getFrontend() && !"".equals(configEntity.getFrontend())) {
			Global.frontend = configEntity.getFrontend();
		}
		if(null!=configEntity.getRangenum() && !"".equals(configEntity.getRangenum())) {
			Global.RangeNumber = Integer.valueOf(configEntity.getRangenum());
		}
		// 保存隐藏平台配置
		if(configEntity.getHiddenplatforms() != null) {
			Global.hiddenplatforms = configEntity.getHiddenplatforms();
		} else {
			Global.hiddenplatforms = "";
		}
		return new AjaxEntity(Global.ajax_option_success, "操作成功", configEntity);
	}

	public AjaxEntity ytextractor(VideoDataEntity enity) {
		String detectedPlatform = YtDlpUtil.getPlatform(enity.getOriginaladdress());
		if(detectedPlatform!= null) {
			return new AjaxEntity(Global.ajax_success, "请求成功", detectedPlatform);
		}
		return new AjaxEntity(Global.ajax_uri_error, "请求失败,可能网络不支持或者yt-dlp不支持", null);
	}

	public void cookieCloud(String uuid,String encrypted, String crypto,String source) throws Exception {
		String tilte ="cookieCloud 推送状态";
		String content = "";
		CookiesConfigEntity data =null;
		if(cookiecloud.equals("")) {
			data = cookiesConfigService.getData();
			cookiecloud = data.getCookiecloud();
			if(data.getCookiecloud()==null || data.getCookiecloud().equals("")) {
				return;
			}
		}
		String mm =  MD5Util.MD5(cookiecloud+uuid+"streamvalute");

		if(!source.equalsIgnoreCase(mm)) {
			return;
		}
		if(data== null) {
			data = cookiesConfigService.getData();
		}
		String decrypt = AESUtils.decrypt(uuid,encrypted, data.getCookiecloud());
		if(decrypt ==null ||  decrypt.equals("")) {
			return;
		}
		JSONObject object = JSONObject.parseObject(decrypt);
		JSONObject cookie_data = object.getJSONObject("cookie_data");
		JSONArray weibo = cookie_data.getJSONArray("weibo.com");
		JSONArray douyin = cookie_data.getJSONArray("douyin.com");
		JSONArray xiaohongshu = cookie_data.getJSONArray("xiaohongshu.com");
		JSONArray kuaishou = cookie_data.getJSONArray("kuaishou.com");
		String weibo_cookie = StringUtil.cookiesToString(weibo).trim();
		String douyin_cookie = StringUtil.cookiesToString(douyin).trim();
		String xiaohongshu_cookie = StringUtil.cookiesToString(xiaohongshu).trim();
		String kuaishou_cookie = StringUtil.cookiesToString(kuaishou).trim();
		if(weibo_cookie!=null && !weibo_cookie.equals("") && weibo_cookie.length()>10) {
			data.setWeibocookie(weibo_cookie);
			logger.info("收到cookieCloud weibo cookie更新");
			content =content+"weibo:已被更新";
		}
		if(xiaohongshu_cookie!=null && !xiaohongshu_cookie.equals("") && xiaohongshu_cookie.length()>10) {
			data.setRednotecookie(xiaohongshu_cookie);
			logger.info("收到cookieCloud xiaohongshu cookie更新");
			content =content+"xiaohongshu:已被更新";
		}
		if(kuaishou_cookie!=null && !kuaishou_cookie.equals("") && kuaishou_cookie.length()>10) {
			data.setKuaishouCookie(kuaishou_cookie);
			logger.info("收到cookieCloud kuaishou cookie更新");
			content =content+"kuaishou:已被更新";
		}
		cookiesConfigService.updateCookie(data);
		if(douyin_cookie!=null && !douyin_cookie.equals("") && douyin_cookie.length()>10 && douyin_cookie.contains("odin_tt") && douyin_cookie.contains("sessionid_ss") && douyin_cookie.contains("ttwid") && douyin_cookie.contains("passport_csrf_token")) {
			TikTokConfigEntity dyconfig = tikTokConfigService.getData();
			dyconfig.setCookies(douyin_cookie);
			tikTokConfigService.updateTikTokConfig(dyconfig);
			logger.info("收到cookieCloud douyin cookie更新");
			content =content+"douyin:已被更新";
		}
		content = content == null || content.isEmpty() ? "无更新成功任务" : content;
		sendNotify.sendMessage(tilte, content);
		
	}

}
