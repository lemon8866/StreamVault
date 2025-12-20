package com.flower.spirit.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.config.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YtDlpUtil {

	private static final Logger logger = LoggerFactory.getLogger(YtDlpUtil.class);

	public static String exec(String url, String outpath, String p,Boolean createnfo) throws IOException, InterruptedException {
		List<String> command = new ArrayList<>();
		command.add("yt-dlp");
		command.add("--print-json");
		String apppath = Global.apppath;
		File cookieDir = new File(apppath + "/cookies");
		if (null!=p && p.equals("youtube")) {
			File youtubeFile = new File(cookieDir, "youtube.txt");
			if (youtubeFile.exists()) {
				command.add("--cookies");
				command.add(youtubeFile.getAbsolutePath());
			}
		}

		if (null!=p && p.equals("twitter")) {
			File twitterFile = new File(cookieDir, "twitter.txt");
			if (twitterFile.exists()) {
				command.add("--cookies");
				command.add(twitterFile.getAbsolutePath());
			}
		}
		
		if(null!=p && !p.equals("twitter") && !p.equals("youtube")) {
			File all = new File(cookieDir, p+".txt");
			if (all.exists()) {
				command.add("--cookies");
				command.add(all.getAbsolutePath());
			}
		}

		if (Global.proxyinfo != null) {
			command.add("--proxy");
			command.add(Global.proxyinfo);
		}
		command.add(url);
		command.add("-o");
		if (Global.getGeneratenfo && createnfo) {
			command.add(outpath + File.separator + "%(title)s" + File.separator + "%(id)s.%(ext)s");
		} else {
			command.add(outpath + File.separator + "%(title)s" + File.separator + "%(title)s.%(ext)s");
		}

		command.add("--write-thumbnail");
		command.add("--convert-thumbnails");
		command.add("webp");
		// command.add("%(title)s.%(ext)s");
//		command.add("--restrict-filenames");
		command.add("--no-restrict-filenames");
		command.add("--windows-filenames");
		
		command.add("--replace-in-metadata");
		command.add("title");
		command.add("#");
		command.add("_");
		
		command.add("--replace-in-metadata");
		command.add("title");
		command.add("\\?");
		command.add("_");
		
		command.add("--replace-in-metadata");
		command.add("title");
		command.add("\\|");
		command.add("_");

		if (null != Global.useragent && !"".equals(Global.useragent)) {
			command.add("--user-agent");
			command.add(Global.useragent);
		}
//		if (Global.ytdlpargs != null && !Global.ytdlpargs.isEmpty()) {
//		    String[] args = Global.ytdlpargs.split(" ");
//		    command.addAll(Arrays.asList(args));
//		}
		ProcessBuilder processBuilder = new ProcessBuilder(command);
	    Process process = processBuilder.start();
	    

	    Thread stderrThread = new Thread(() -> {
	        try (BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
	            String errLine;
	            while ((errLine = errReader.readLine()) != null) {
	                logger.warn("yt-dlp stderr: " + errLine);
	            }
	        } catch (IOException e) {
	            logger.error("yt-dlp 错误输出失败", e);
	        }
	    });
	    stderrThread.start();
	    
	    
	    StringBuilder stringBuilder = new StringBuilder();
	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            stringBuilder.append(line).append("\n");
	        }
	    } finally {
	        process.waitFor();
	        stderrThread.join();
	        process.destroy();
	    }

	    String completeString = stringBuilder.toString();
	    if (process.exitValue() != 0) {
	        logger.error(completeString);
	    } else {
	        logger.info("yt-dlp executed with exit code: " + process.exitValue());
	    }
	    return completeString;
	}

	public static boolean isVideoStream(JSONObject format) {
		String vcodec = format.getString("vcodec");
		return vcodec != null && !"none".equals(vcodec);
	}

	public static boolean isAudioStream(JSONObject format) {
		String vcodec = format.getString("vcodec");
		String audioExt = format.getString("audio_ext");
		return "none".equals(vcodec) &&
				audioExt != null &&
				!"none".equals(audioExt);
	}

	public static String exec(String url) throws IOException, InterruptedException {
		List<String> command = new ArrayList<>();
		command.add("yt-dlp");
		command.add("--print-json");
		command.add("--skip-download");
		command.add(url);
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Process process = processBuilder.start();
		InputStream inputStream = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			;
		}
		int exitCode = process.waitFor();
		System.out.println("Command executed with exit code: " + exitCode);
		String completeString = stringBuilder.toString();
		return completeString;
	}

	public static String getPlatform(String url) {
	    if (url == null || url.trim().isEmpty()) {
	        logger.warn("URL为空，无法获取平台信息");
	        return null;
	    }

	    Process process = null;
	    try {
	        List<String> command = new ArrayList<>();
	        command.add("yt-dlp");
	        command.add("--print");
	        command.add("%(extractor)s");    
	        command.add("--no-download");
	        command.add("--skip-download");
	        command.add("--ignore-config"); 
	        if (Global.proxyinfo != null) {
	            command.add("--proxy");
	            command.add(Global.proxyinfo);
	        }
	        if (Global.useragent != null && !Global.useragent.isEmpty()) {
	            command.add("--user-agent");
	            command.add(Global.useragent);
	        }
	        command.add(url);
	        ProcessBuilder processBuilder = new ProcessBuilder(command);
	        processBuilder.redirectErrorStream(false);
	        process = processBuilder.start();
	        String stdout;
	        try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
	            stdout = reader.readLine();
	        }
	        String stderr;
	        try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
	            StringBuilder sb = new StringBuilder();
	            String line;
	            while ((line = reader.readLine()) != null) {
	                sb.append(line).append("\n");
	            }
	            stderr = sb.toString();
	        }

	        int exitCode = process.waitFor();

	        // 情况1: stdout 有 extractor（成功）
	        if (stdout != null && !stdout.trim().isEmpty()) {
	            String platform = stdout.trim();
	            logger.debug("从 stdout 获取平台: {}", platform);
	            return platform;
	        }

	        if (stderr != null) {
	            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\[(\\w+)\\]");
	            java.util.regex.Matcher matcher = pattern.matcher(stderr);
	            if (matcher.find()) {
	                String platform = matcher.group(1);
	                logger.debug("从 stderr 提取平台: {}", platform);
	                return platform;
	            }
	        }

	        logger.warn("无法识别平台，exitCode={}, stdout={}, stderr={}", exitCode, stdout, stderr);
	        return null;

	    } catch (Exception e) {
	        logger.error("获取平台信息失败: {}", e.getMessage(), e);
	        return null;
	    } finally {
	        if (process != null) {
	            process.destroyForcibly();
	        }
	    }
	}
}
