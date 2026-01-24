package com.flower.spirit.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.config.Global;

/**
 * 小红书视频解析工具类
 * 用于本地下载功能
 */
public class XiaohongshuParser {

    private static final Logger logger = LoggerFactory.getLogger(XiaohongshuParser.class);

    /**
     * 视频信息类
     */
    public static class VideoInfo {
        private String videoUrl;
        private String coverUrl;
        private String title;
        private String author;
        private String noteId;
        private String type; // normal: 图文, video: 视频
        private Long duration;
        private List<String> imageUrls;

        public String getVideoUrl() { return videoUrl; }
        public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
        public String getCoverUrl() { return coverUrl; }
        public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getNoteId() { return noteId; }
        public void setNoteId(String noteId) { this.noteId = noteId; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Long getDuration() { return duration; }
        public void setDuration(Long duration) { this.duration = duration; }
        public List<String> getImageUrls() { return imageUrls; }
        public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    }

    /**
     * 解析小红书视频
     * @param url 小红书链接
     * @return 视频信息
     */
    public static VideoInfo parseVideo(String url) {
        try {
            String cookie = null;
            if (Global.cookie_manage != null) {
                cookie = Global.cookie_manage.getRednotecookie();
            }
            
            String page = HttpUtil.getPage(url, cookie, "https://www.xiaohongshu.com/");
            if (page == null || page.isEmpty()) {
                logger.error("无法获取小红书页面内容");
                return null;
            }
            
            String jsonFromHtml = extractJsonString(page);
            if (jsonFromHtml == null) {
                logger.error("无法从页面中提取JSON数据");
                return null;
            }
            
            JSONObject json = JSONObject.parseObject(jsonFromHtml);
            if (json == null) {
                logger.error("JSON解析失败");
                return null;
            }
            
            JSONObject noteObj = json.getJSONObject("note");
            if (noteObj == null) {
                logger.error("未找到note对象");
                return null;
            }
            
            JSONObject noteDetailMap = noteObj.getJSONObject("noteDetailMap");
            if (noteDetailMap == null || noteDetailMap.isEmpty()) {
                logger.error("未找到noteDetailMap对象或为空");
                return null;
            }
            
            String keyid = noteDetailMap.keySet().iterator().next();
            JSONObject noteDetail = noteDetailMap.getJSONObject(keyid);
            if (noteDetail == null) {
                logger.error("未找到noteDetail对象");
                return null;
            }
            
            JSONObject note = noteDetail.getJSONObject("note");
            if (note == null) {
                logger.error("未找到note对象");
                return null;
            }
            
            VideoInfo info = new VideoInfo();
            info.setNoteId(keyid);
            info.setTitle(note.getString("title"));
            info.setType(note.getString("type"));
            
            JSONObject user = note.getJSONObject("user");
            if (user != null) {
                info.setAuthor(user.getString("nickname"));
            }
            
            // 获取图片列表
            JSONArray imageList = note.getJSONArray("imageList");
            List<String> images = getImages(imageList);
            info.setImageUrls(images);
            
            // 设置封面图（使用第一张图片）
            if (!images.isEmpty()) {
                info.setCoverUrl(images.get(0));
            }
            
            // 如果是视频类型，获取视频URL
            if ("video".equals(info.getType())) {
                JSONObject videoData = note.getJSONObject("video");
                if (videoData != null) {
                    JSONObject media = videoData.getJSONObject("media");
                    if (media != null) {
                        JSONObject stream = media.getJSONObject("stream");
                        if (stream != null) {
                            JSONArray h264Data = stream.getJSONArray("h264");
                            if (h264Data != null && h264Data.size() > 0) {
                                // 获取第一个（通常是最高质量）的视频流
                                JSONObject firstStream = h264Data.getJSONObject(0);
                                String masterUrl = firstStream.getString("masterUrl");
                                info.setVideoUrl(masterUrl);
                                
                                Long duration = firstStream.getLong("duration");
                                if (duration != null) {
                                    info.setDuration(duration / 1000); // 转换为秒
                                }
                            }
                        }
                    }
                }
            }
            
            return info;
            
        } catch (Exception e) {
            logger.error("解析小红书视频失败", e);
            return null;
        }
    }

    /**
     * 从HTML内容中提取__INITIAL_STATE__的JSON字符串
     * @param htmlContent HTML内容
     * @return JSON字符串
     */
    private static String extractJsonString(String htmlContent) {
        Pattern pattern = Pattern.compile("window\\.__INITIAL_STATE__\\s*=\\s*(.+?)(?=</script>)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(htmlContent);
        
        if (matcher.find()) {
            String jsonString = matcher.group(1).trim();
            
            // 移除末尾的分号（如果存在）
            if (jsonString.endsWith(";")) {
                jsonString = jsonString.substring(0, jsonString.length() - 1);
            }
            
            return jsonString;
        }
        
        return null;
    }

    /**
     * 从图片数组中获取图片URL列表
     * @param arr 图片JSON数组
     * @return 图片URL列表
     */
    private static List<String> getImages(JSONArray arr) {
        List<String> res = new ArrayList<>();
        if (arr != null) {
            for (int i = 0; i < arr.size(); i++) {
                String url = arr.getJSONObject(i).getString("urlDefault");
                if (url != null && !url.isEmpty()) {
                    res.add(url);
                }
            }
        }
        return res;
    }
}
