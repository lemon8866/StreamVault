package com.flower.spirit.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * <p>
 * Title: StringUtil
 * </p>
 * 
 * <p>
 * Description:字符串工具类
 * </p>
 * 
 * @author QingFeng
 * 
 * @date 2020年8月14日
 * 
 */
public class StringUtil {

	/**
	 * 
	 * <p>
	 * Title: isString
	 * </p>
	 * 
	 * <p>
	 * Description:判断是否是有效的字符串
	 * </p>
	 * 
	 * @param str
	 * @return
	 * 
	 */
	public static boolean isString(String str) {
		if (null == str || str.trim().equals("")) {
			return false;
		}
		return true;
	}

	/**
	 * 处理特殊字符串并返回安全的文件名
	 * 
	 * @param obj 原始文件名
	 * @param aid 备用ID
	 * @return 处理后的文件名
	 */
	public static String getFileName(String obj, String aid) {
		try {
			if (obj == null || obj.trim().isEmpty()) {
				return aid;
			}

			// 限制长度
			if (obj.length() > 64) {
				obj = obj.substring(0, 64);
			}

			// 替换所有非中文、数字、字母为点号
			String result = obj.replaceAll("[^A-Za-z0-9\\u4e00-\\u9fa5]+", ".");

			// 合并多个点号为一个
			result = result.replaceAll("\\.+", ".");
			


			// 去除首尾点号
			result = result.replaceAll("^\\.|\\.$", "");

			// 去除孤立的点号（即两个点号之间无字符的情况）
			result = result.replaceAll("(?<=\\.)\\.(?=\\.)", "");

			// 再次去除首尾点号，防止中间清理后新产生的
			result = result.replaceAll("^\\.|\\.$", "");

			result = result.replace(".", "");
			
			// 如果结果为空，使用备用ID
			if (result.isEmpty()) {
				return aid;
			}

			// 文件名不能以数字开头
			if (result.matches("^[0-9].*")) {
				result = "ep" + result;
			}

			return result;
		} catch (Exception e) {
			return aid;
		}
	}

	
    public static String simplifyTitle(String originalTitle) {
        if (originalTitle == null || originalTitle.trim().isEmpty()) {
            return "未知标题";
        }
        String cleaned = originalTitle.replaceAll("#[^_#\\s]+", "");
        cleaned = cleaned.replaceAll("_+", "_");
        cleaned = cleaned.replaceAll("^_+|_+$", "");
        String[] parts = cleaned.split("_");
        StringBuilder simplified = new StringBuilder();
        int count = 0;
        for (String part : parts) {
            part = part.trim();
            if (!part.isEmpty()) {
                simplified.append(part);
                count++;
                if (count >= 3) break; // 最多保留前3个有效词
            }
        }
        return simplified.length() > 0 ? simplified.toString() : "未知标题";
    }


	public static String getRemoteAddr(HttpServletRequest request) {
		String remoteAddr = request.getHeader("X-Real-IP");
		if (isString(remoteAddr)) {
			remoteAddr = request.getHeader("X-Forwarded-For");
		} else if (isString(remoteAddr)) {
			remoteAddr = request.getHeader("Proxy-Client-IP");
		} else if (isString(remoteAddr)) {
			remoteAddr = request.getHeader("WL-Proxy-Client-IP");
		}
		return remoteAddr != null ? remoteAddr : request.getRemoteAddr();
	}
    public static boolean isNotBlank(final CharSequence cs) {
        return !StringUtils.isBlank(cs);
    }
    
    public static String getUpdateStatus(String text) {
        Pattern currentVersionPattern = Pattern.compile("Current version: ([^ ]+)"); 
        Pattern latestVersionPattern = Pattern.compile("Latest version: ([^ ]+)"); 
        Pattern upToDatePattern = Pattern.compile("yt-dlp is up to date \\(([^\\)]+)\\)"); 
        if (text.contains("yt-dlp is up to date")) {
            Matcher upToDateMatcher = upToDatePattern.matcher(text);
            if (upToDateMatcher.find()) {
                String latestVersion = upToDateMatcher.group(1);
                return "当前已是最新版本 版本号:" + latestVersion;
            }
        }
        Matcher currentVersionMatcher = currentVersionPattern.matcher(text);
        Matcher latestVersionMatcher = latestVersionPattern.matcher(text);
        if (currentVersionMatcher.find() && latestVersionMatcher.find()) {
            String currentVersion = currentVersionMatcher.group(1);
            String latestVersion = latestVersionMatcher.group(1);
            if (currentVersion.equals(latestVersion)) {
                return "当前已是最新版本 版本号:" + latestVersion;
            } else {
                return "已经成功更新yt-dlp 由 " + currentVersion + " 更新至 " + latestVersion;
            }
        }
        return "无法获取版本信息";
    }
    
	public static String cookiesToString(JSONArray array) {
		if (array == null || array.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.size(); i++) {
			JSONObject cookie = array.getJSONObject(i);
			String name = cookie.getString("name");
			String value = cookie.getString("value");

			// 跳过 name 或 value 为 null 或空的情况
			if (name == null || value == null || name.trim().isEmpty() || value.trim().isEmpty()) {
				continue;
			}

			if (sb.length() > 0) {
				sb.append("; ");
			}
			sb.append(name).append("=").append(value);
		}
		return sb.toString();
	}

}
