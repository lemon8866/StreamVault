package com.flower.spirit.config;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.flower.spirit.entity.BiliConfigEntity;
import com.flower.spirit.entity.ConfigEntity;
import com.flower.spirit.entity.TikTokConfigEntity;
import com.flower.spirit.entity.CookiesConfigEntity;
import com.flower.spirit.service.BiliConfigService;
import com.flower.spirit.service.ConfigService;
import com.flower.spirit.service.CookiesConfigService;
import com.flower.spirit.service.DownloaderService;
import com.flower.spirit.service.FfmpegQueueService;
import com.flower.spirit.service.NotifyConfigService;
import com.flower.spirit.service.TikTokConfigService;

/**
 * @author flower
 *程序初始化入口 加载常量信息到应用缓存
 */
@Configuration
public class AppConfig {
	
	private Logger logger = LoggerFactory.getLogger(AppConfig.class);
	
	@Autowired
	private DownloaderService downloaderService;
	
	@Autowired
	private ConfigService configService;
	

	@Autowired
	private BiliConfigService biliConfigService;
	
	@Autowired
	private TikTokConfigService  tikTokConfigService;
	
	@Autowired
	private FfmpegQueueService ffmpegQueueService;
	
	@Autowired
	private CookiesConfigService cookiesConfigService;
	
	@Autowired 
	private NotifyConfigService notifyConfigService;
	
	
	@PostConstruct
	public void init() {
		downloaderService.renovate();
		ConfigEntity data = configService.getData();
		Global.apptoken =data.getApptoken();
		if(null != data.getOpenprocesshistory() && data.getOpenprocesshistory().equals("1")) {
			Global.openprocesshistory =true;
		}
		if(null!=data.getUseragent() && !"".equals(data.getUseragent())) {
			Global.useragent = data.getUseragent();
		}
		BiliConfigEntity bili = biliConfigService.getData();
		Global.bilicookies =bili.getBilicookies();
		if(null != bili.getBigmember() && bili.getBigmember().equals("是")) {
			Global.bilimember= true;
		}
		if(null != bili.getBitstream() && !"".equals(bili.getBitstream())) {
			Global.bilibitstream= bili.getBitstream();
		}
		if(null != bili.getRefreshtoken() && !"".equals(bili.getRefreshtoken())) {
			Global.bili_refresh_token= bili.getRefreshtoken();
		}
		TikTokConfigEntity tiktok = tikTokConfigService.getData();
		if(null !=tiktok.getCookies() && !"".equals(tiktok.getCookies())) {
			Global.tiktokCookie =tiktok.getCookies();
		}
		//新增cookies 配置信息
		CookiesConfigEntity cookies = cookiesConfigService.getData();
		if(null !=cookies) {
			Global.cookie_manage =cookies;
		}
		if(data.getGeneratenfo()!= null && data.getGeneratenfo().equals("1")) {
			Global.getGeneratenfo =  true;
		}
		if(null!=data.getReadonlytoken() && !"".equals(data.getReadonlytoken())) {
			Global.readonlytoken = data.getReadonlytoken();
		}
		if(null!=data.getYtdlpmode() && !"".equals(data.getYtdlpmode())) {
			Global.ytdlpmode = data.getYtdlpmode();
		}
		if(null!=data.getYtdlpargs() && !"".equals(data.getYtdlpargs())) {
			Global.ytdlpargs = data.getYtdlpargs();
		}
		if(null!=data.getNfonetaddr() && !"".equals(data.getNfonetaddr())) {
			Global.nfonetaddr = data.getNfonetaddr();
		}
		if(null!=data.getFrontend() && !"".equals(data.getFrontend())) {
			Global.frontend = data.getFrontend();
		}
		if(null!=data.getRangenum() && !"".equals(data.getRangenum())) {
			Global.RangeNumber = Integer.valueOf(data.getRangenum());
		}
		if (data.getAgenttype() != null && !data.getAgenttype().trim().isEmpty() &&
			    data.getAgentaddress() != null && !data.getAgentaddress().trim().isEmpty() &&
			    data.getAgentport() != null && !data.getAgentport().trim().isEmpty()) {
			Global.proxyinfo = buildProxyArgument(data);
			logger.info("已启动yt-dlp网络代理,代理地址:"+Global.proxyinfo);
			   
		}
		if(data.getDanmudown()!= null && data.getDanmudown().equals("1")) {
			Global.danmudown =  true;
		}
		//清空 ffmpeg 队列
		ffmpegQueueService.clearTask();
		logger.info("ffmpeg队列已清空");
		
		//全局通知
		Global.notify = notifyConfigService.getData(null);
	}
	
    public static String buildProxyArgument(ConfigEntity data) {
        if (data == null || 
            isBlank(data.getAgenttype()) || 
            isBlank(data.getAgentaddress()) || 
            isBlank(data.getAgentport())) {
            return null; // 直接连接
        }

        StringBuilder proxyUrl = new StringBuilder();
        proxyUrl.append(data.getAgenttype()).append("://");

        if (!isBlank(data.getAgentaccpass())) {
            proxyUrl.append(data.getAgentaccpass()).append("@");
        }

        proxyUrl.append(data.getAgentaddress()).append(":").append(data.getAgentport()).append("/");

        return proxyUrl.toString();
    }

    private static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    
    public static void main(String[] args) {
		ConfigEntity configEntity = new ConfigEntity();
		configEntity.setAgenttype("http");
		configEntity.setAgentaddress("127.0.0.1");
		configEntity.setAgentport("3333");
		configEntity.setAgentaccpass("xxx:xxx");
		String proxyArgument = buildProxyArgument(configEntity);
		System.out.println(proxyArgument);
	}
}
