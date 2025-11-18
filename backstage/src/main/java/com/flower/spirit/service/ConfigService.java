package com.flower.spirit.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.AppConfig;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.ConfigDao;
import com.flower.spirit.entity.ConfigEntity;
import com.flower.spirit.entity.VideoDataEntity;
import com.flower.spirit.utils.BeanUtil;
import com.flower.spirit.utils.YtDlpUtil;

@Service
public class ConfigService {
	
	@Autowired
	private ConfigDao  configDao;
	
	private Logger logger = LoggerFactory.getLogger(ConfigService.class);

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
		if(null!=configEntity.getYtdlpargs() && !"".equals(configEntity.getYtdlpargs())) {
			Global.ytdlpargs = configEntity.getYtdlpargs();
		}
		if(null!=configEntity.getNfonetaddr() && !"".equals(configEntity.getNfonetaddr())) {
			Global.nfonetaddr = configEntity.getNfonetaddr();
		}
		if(null!=configEntity.getFrontend() && !"".equals(configEntity.getFrontend())) {
			Global.frontend = configEntity.getFrontend();
		}
		if(null!=configEntity.getRangenum() && !"".equals(configEntity.getRangenum())) {
			Global.RangeNumber = Integer.valueOf(configEntity.getRangenum());
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

}
