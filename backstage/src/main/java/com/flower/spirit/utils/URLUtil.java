package com.flower.spirit.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 链接工具类
 * @author flower
 *
 */
public class URLUtil {
	
	@SuppressWarnings("serial")
	public static Map<String, List<String>> domain = new HashMap<String, List<String>>(){{
		List<String> bili = new ArrayList<String>();
		bili.add("b23.tv");
		bili.add("bilibili.com");
		List<String> dy = new ArrayList<String>();
		dy.add("douyin.com");
		List<String> tiktok = new ArrayList<String>();
		tiktok.add("tiktok.com");
		
		List<String> youtube = new ArrayList<String>();
		youtube.add("youtu.be");
		youtube.add("youtube.com");
		
		List<String> instagram = new ArrayList<String>();
		instagram.add("instagram.com");
		
		List<String> twitter = new ArrayList<String>();
		twitter.add("twitter.com");
		twitter.add("https://x.com/");
	
		List<String> weibo = new ArrayList<String>();
		weibo.add("weibo.com");
		
		List<String> xiaohongshu = new ArrayList<String>();
		xiaohongshu.add("www.xiaohongshu.com");
		xiaohongshu.add("xhslink.com");
		
		List<String> qqmusic = new ArrayList<String>();
		qqmusic.add("y.qq.com");
		
		List<String> netease = new ArrayList<String>();
		netease.add("163.cn");
		netease.add("music.163.com");
		
		put("tiktok", tiktok);
		put("哔哩", bili);
		put("抖音", dy);
		put("YouTube", youtube);
		put("instagram", instagram);
		put("twitter", twitter);
		put("微博", weibo);
		put("小红书", xiaohongshu);
		put("QQ音乐", qqmusic);
		put("网易云音乐", netease);
	}};;

	/**
	 * @param url
	 * @return
	 */
	public static String urlAnalysis(String url) {
		for (Entry<String, List<String>> entry : domain.entrySet()) {
		    List<String> list =entry.getValue();
		    for(String dom:list) {
		    	if(url.indexOf(dom) >-1) {
		    		return entry.getKey();
		    	}
		    }
		}
		return "未知";
	}
	public static void main(String[] args) {
		String urlAnalysis = URLUtil.urlAnalysis("https://b23.tv/kq7ciww");
		System.out.println(urlAnalysis);
	}
}
