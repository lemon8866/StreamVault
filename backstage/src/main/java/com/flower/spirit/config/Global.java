package com.flower.spirit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.flower.spirit.entity.CookiesConfigEntity;
import com.flower.spirit.entity.NotifyConfigEntity;

/**
 * 
 * <p>
 * Title: Global
 * </p>
 * 
 * <p>
 * Description: 全局变量
 * </p>
 * 
 * @author QingFeng
 * 
 * @date 2020年8月14日
 * 
 */
@Component
public class Global {

	public static String user_session_key = "user_login_session";

	public static String ajax_success = "000001";

	public static String ajax_uri_error = "999998";

	public static String ajax_login_err = "999997";

	public static String ajax_login_success_message = "登录成功";

	public static String ajax_login_err_message = "您的账号或密码输入错误,请重新输入";

	public static String ajax_uri_error_message = "您的参数不完整,请检查提交参数";

	public static String ajax_add_user_err = "999996";

	public static String ajax_add_user_err_message = "添加用户失败,用户已存在";

	public static String ajax_add_user_success_message = "用户添加成功";

	public static String ajax_option_success = "操作成功";

	public static String ajax_nav_no_rule = "333333";

	public static String ajax_nav_no_rule_message = "未对外开放";

	public static String downtype = "a2";

	public static String a2_link = "http://localhost:6800/jsonrpc";

	public static String a2_token = "123456";

	public static String down_path = "/app/resources";

	public static String apptoken = "123456";

	public static String bilicookies = "";

	public static boolean bilimember = false;

	public static String bilibitstream = "64";
	
	public static boolean biliodddmm = true;
	
	public static boolean bilicollectdmm = false;
	
	public static String bili_refresh_token = null;

	public static boolean openprocesshistory = false;

	public static String tiktokCookie = "";

	// public static String analysiSserver="https://spirit.lifeer.xyz";

	public static String wallpaperid = "431960";

	public static CookiesConfigEntity cookie_manage;
	
	public static String tasknexttime ="";

	public static enum platform {
		bilibili,
		douyin,
		tiktok,
		youtube,
		instagram,
		twitter,
		kuaishou,
		weibo,
		rednote;
	};

	// 文件映射和保存路径
	public static String savefile;
	public static String uploadRealPath;
	public static String apppath;

	public static boolean getGeneratenfo =false;
	
	public static boolean danmudown =false;
	
	public static String proxyinfo;
	
	public static String useragent = null;
	
	public static String readonlytoken = null;
	
	public static NotifyConfigEntity notify;
	
	public static enum configInfo{
		getSerchPersion("user-agent","Mozilla/5.0 (Linux; Android 14; Xiaomi 23127PN0CC Build/UKQ1.230917.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/123.0.6312.60 Mobile Safari/537.36"),
		BiliDroid("User-Agent","Mozilla/5.0 BiliDroid/8.42.0 (bbcallen@gmail.com)");
		private final String key, value; configInfo(String key, String value) { this.key = key; this.value = value; } public String getKey() { return key; } public String getValue() { return value; }

	}
	
	public static String ytdlpmode = "0";
	
//	public static String ytdlpargs =null;

	public static String nfonetaddr = "";

	public static String encoder = "libx264";
	
	public static int RangeNumber = 1;
	
	public static String frontend = "blank";
	
	public static String hiddenplatforms = "";  // 在视频首页隐藏的平台（逗号分隔）

	@Value("${file.save}")
	public void setSavefile(String value) {
		Global.savefile = value;
	}

	@Value("${file.save.path}")
	public void setUploadRealPath(String value) {
		Global.uploadRealPath = value;
	}
	
	
	@Value("${file.app.path}")
	public void setAppPath(String value) {
		Global.apppath = value;
	}
}
