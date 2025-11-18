package com.flower.spirit.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.UserDao;
import com.flower.spirit.entity.UserEntity;
import com.flower.spirit.utils.CommandUtil;
import com.flower.spirit.utils.HttpUtil;
import com.flower.spirit.utils.MD5Util;
import com.flower.spirit.utils.SecurityUtil;
import com.flower.spirit.utils.StringUtil;
import com.flower.spirit.utils.sendNotify;

@Service
public class SystemService {

	private Logger logger = LoggerFactory.getLogger(SystemService.class);

	@Autowired
	private UserDao userDao;

	@Value("${file.save.path}")
	private String fileSavePath;

	@Value("${file.save.staticAccessPath}")
	private String staticAccessPath;

	@Value("${server.version}")
	private String serverversion;

	/**
	 * 
	 * <p>
	 * Title: loginUser
	 * </p>
	 * 
	 * <p>
	 * Description:登录用户
	 * </p>
	 * 
	 * @param userEntity
	 * @param request
	 * @return
	 * 
	 */
	public AjaxEntity loginUser(UserEntity userEntity, HttpServletRequest request) {
		String ip = StringUtil.getRemoteAddr(request);

		// 检查IP是否被锁定
		if (!SecurityUtil.isLoginAllowed(ip)) {
			logger.warn("IP {} 因多次登录失败被临时锁定", ip);
			return new AjaxEntity(Global.ajax_login_err, "登录尝试次数过多，请稍后再试", null);
		}

		// 基本参数验证
		if (!StringUtil.isString(userEntity.getPassword()) || !StringUtil.isString(userEntity.getUsername())) {
			return new AjaxEntity(Global.ajax_uri_error, Global.ajax_uri_error_message, null);
		}

		// XSS防护和用户名格式验证
		String username = SecurityUtil.escapeXSS(userEntity.getUsername());
		if (!SecurityUtil.isValidUsername(username)) {
			logger.warn("用户名格式无效: {}", username);
			return new AjaxEntity(Global.ajax_login_err, "用户名格式无效", null);
		}

		// SQL注入检查
		if (SecurityUtil.containsSQLInjection(username)) {
			logger.warn("检测到SQL注入尝试: {} from IP: {}", username, ip);
			SecurityUtil.recordLoginFailure(ip);
			return new AjaxEntity(Global.ajax_login_err, "账号密码错误", null);
		}

		logger.info("用户 {} 在 {} 尝试登录", username, ip);

		UserEntity findByUsername = userDao.findByUsername(username);
		if (findByUsername == null) {
			logger.info("用户 {} 在 {} 登录失败: 用户不存在", username, ip);
			SecurityUtil.recordLoginFailure(ip);
			return new AjaxEntity(Global.ajax_login_err, Global.ajax_login_err_message, null);
		}

		if (MD5Util.MD5(userEntity.getPassword()).equals(findByUsername.getPassword())) {

		    HttpSession oldSession = request.getSession(false);
		    if (oldSession != null) {
		        oldSession.invalidate();
		    }
		    HttpSession newSession = request.getSession(true);
			
			Date date = new Date();
			findByUsername.setLasttime(Long.toString(date.getTime()));
			userDao.save(findByUsername);
			logger.info("用户 {} 在 {} 登录成功", username, ip);
//			request.getSession().setAttribute(Global.user_session_key, findByUsername);
			newSession.setAttribute(Global.user_session_key, findByUsername);
			// 登录成功，重置失败计数
			SecurityUtil.resetLoginAttempts(ip);
			sendNotify.sendMessage("StreamVault通知", "帐号:"+username+"登录成功\n"+"登录IP:"+ip);
			return new AjaxEntity(Global.ajax_success, Global.ajax_login_success_message, null);
		}

		logger.info("用户 {} 在 {} 登录失败: 密码错误", username, ip);
		SecurityUtil.recordLoginFailure(ip);
		return new AjaxEntity(Global.ajax_login_err, Global.ajax_login_err_message, null);
	}


	public AjaxEntity checkVersion() {
		String version = "https://mirror.ghproxy.com/https://raw.githubusercontent.com/lemon8866/spirit/main/version";
		String version_data = HttpUtil.getSerchPersion(version, "UTF-8");
		String[] lines = version_data.split("\n");
		for (String str : lines) {
			String[] split = str.split("=");
			if (split[0].equals("server")) {
				int compareVersions = compareVersion(serverversion, split[1]);
				// int compareVersions = compareVersion("0.0.6", split[1]);
				if (compareVersions == -1) {
					// 版本过低 获取远端 版本说明
				}
				if (compareVersions == 0) {
					// 版本相同 获取远端 版本说明
				}
			}

		}
		return null;
	}

	/**
	 * 版本号比较
	 *
	 * @param v1
	 * @param v2
	 * @return 0代表相等，1代表左边大，-1代表右边大
	 *         Utils.compareVersion("1.0.358_20180820090554","1.0.358_20180820090553")=1
	 */
	public static int compareVersion(String v1, String v2) {
		if (v1.equals(v2)) {
			return 0;
		}
		String[] version1Array = v1.split("[._]");
		String[] version2Array = v2.split("[._]");
		int index = 0;
		int minLen = Math.min(version1Array.length, version2Array.length);
		long diff = 0;

		while (index < minLen
				&& (diff = Long.parseLong(version1Array[index])
						- Long.parseLong(version2Array[index])) == 0) {
			index++;
		}
		if (diff == 0) {
			for (int i = index; i < version1Array.length; i++) {
				if (Long.parseLong(version1Array[i]) > 0) {
					return 1;
				}
			}

			for (int i = index; i < version2Array.length; i++) {
				if (Long.parseLong(version2Array[i]) > 0) {
					return -1;
				}
			}
			return 0;
		} else {
			return diff > 0 ? 1 : -1;
		}
	}


	/**
	 * 这里是一个  检查并更新应用版本  这里目前只检查yt-dlp
	 * 后续考虑添加 f2 Python库 和本应用
	 * 
	 * @return
	 */
	public AjaxEntity checkAndUpdate(String proxyup) {
		String message ="StreamVault 版本暂时无法通过在线更新<br />";

		List<String> cmdList = new ArrayList<>();
		cmdList.add("yt-dlp");
		cmdList.add("-U");
		if(Global.proxyinfo != null && (null !=proxyup && proxyup.equals("1"))){
			cmdList.add("--proxy");
			cmdList.add(Global.proxyinfo);
		}
		String commandos = CommandUtil.runCommandList(cmdList);
		String updateStatus = StringUtil.getUpdateStatus(commandos);
		message= message+"yt-dlp版本:"+updateStatus;
		return new AjaxEntity(Global.ajax_success, message, null);
	}
	public static void main(String[] args) {

	}
}