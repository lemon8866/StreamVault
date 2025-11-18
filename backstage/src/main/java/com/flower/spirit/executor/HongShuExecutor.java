package com.flower.spirit.executor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.GraphicContentDao;
import com.flower.spirit.dao.VideoDataDao;
import com.flower.spirit.entity.GraphicContentEntity;
import com.flower.spirit.entity.ProcessHistoryEntity;
import com.flower.spirit.entity.VideoDataEntity;
import com.flower.spirit.service.ProcessHistoryService;
import com.flower.spirit.utils.DateUtils;
import com.flower.spirit.utils.DouUtil;
import com.flower.spirit.utils.EmbyMetadataGenerator;
import com.flower.spirit.utils.FileUtil;
import com.flower.spirit.utils.HttpUtil;
import com.flower.spirit.utils.StringUtil;
import com.flower.spirit.utils.sendNotify;

@Service
public class HongShuExecutor {
	
	private static Logger logger = LoggerFactory.getLogger(HongShuExecutor.class);
	
	@Autowired
	private VideoDataDao videoDataDao;
	
	@Autowired
	private ProcessHistoryService processHistoryService;
	
	@Autowired
	private GraphicContentDao graphicContentDao;

	public void dataExecutor(String detectedPlatform,String url) throws IOException, InterruptedException {
		ProcessHistoryEntity saveProcess = processHistoryService.saveProcess(null, url, detectedPlatform);
		//返回为空则尝试图文
		HashMap<String, String> header = new HashMap<String, String>();
		header.put("Referer", "https://www.xiaohongshu.com/");
		header.put("User-Agent",  Global.useragent != null?Global.useragent:"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
		if(null!=Global.cookie_manage.getRednotecookie() && !"".equals(Global.cookie_manage.getRednotecookie())) {
			header.put("cookie", Global.cookie_manage.getRednotecookie());
		}
		String page = HttpUtil.getPage(url, Global.cookie_manage.getRednotecookie(),"https://www.xiaohongshu.com/");
		String jsonFromHtml = extractJsonString(page);
		JSONObject json = JSONObject.parseObject(jsonFromHtml);
		JSONObject noteDetailMap = json.getJSONObject("note").getJSONObject("noteDetailMap");
		String keyid = noteDetailMap.keySet().iterator().next();
		JSONObject note = noteDetailMap.getJSONObject(keyid).getJSONObject("note");
		String title = note.getString("title");
		String time = note.getString("time");
		String type = note.getString("type");  //type   normal  video   video  要判断images 如果多个 认为是图文
		//如果视频时长超过1分钟 算视频
		String desc = note.getString("desc");
		String nickname = note.getJSONObject("user").getString("nickname");
		JSONArray imageList = note.getJSONArray("imageList");
		List<String> images = getImages(imageList);
		String filename = StringUtil.getFileName(title, keyid);
		/*markroute  图文用**/
		String markroute = FileUtil.generateDir(true, Global.platform.rednote.name(), filename, null, null,0); 
		if(type.equals("normal")) {
			//判重复
			Optional<GraphicContentEntity> byVideoidAndPlatform = graphicContentDao.findByVideoidAndPlatform(keyid,  Global.platform.rednote.name());
			if(byVideoidAndPlatform.isPresent()) {
				logger.info(url+"地址已存在,不处理");
				return;
			}
			downImages(images,null, filename, header, keyid, url, title, markroute, desc, nickname, saveProcess);
			return;
		}
		if(type.equals("video")) {
			//混合 判断images 是否超过1个  判断视频时长是否超过60s 超过60s 算视频 
			JSONObject videoData = note.getJSONObject("video");
			JSONArray h264Data = videoData.getJSONObject("media").getJSONObject("stream").getJSONArray("h264");
			List<String> videos = getVideos(h264Data);
			if(images.size()>1) {
				Optional<GraphicContentEntity> byVideoidAndPlatform = graphicContentDao.findByVideoidAndPlatform(keyid,  Global.platform.rednote.name());
				if(byVideoidAndPlatform.isPresent()) {
					logger.info(url+"地址已存在,不处理");
					return;
				}
				downImages(images, videos,filename, header, keyid, url, title, markroute, desc, nickname, saveProcess);
				return;
			}
			if(h264Data.size()>1) {
				//数据超 进图文
				downImages(images, videos,filename, header, keyid, url, title, markroute, desc, nickname, saveProcess);
				return;
			}
			if(h264Data.size() == 1) {
				List<VideoDataEntity> byVideoidAndVideoplatform = videoDataDao.findByVideoidAndVideoplatform(keyid, "小红书");
				if(byVideoidAndVideoplatform.size()>0) {
					logger.info(url+"地址已存在,不处理");
					return;
				}
				Long duration = h264Data.getJSONObject(0).getLong("duration")/1000;
				String masterUrl = h264Data.getJSONObject(0).getString("masterUrl");
				if(duration>6) {
					//单视频且时长超6 进视频
					String 	videofile = FileUtil.generateDir(true, Global.platform.rednote.name(), true, filename, null, null);
					String videounrealaddr = FileUtil.generateDir(false, Global.platform.rednote.name(), true, filename,
							null, "mp4");
					String coverunaddr = FileUtil.generateDir(false, Global.platform.rednote.name(), true, filename, null,
							"jpg");
					String coverfile = filename + ".jpg";
					String coverdir = FileUtil.generateDir(true, Global.platform.rednote.name(), true, filename, null,
							null);
					HttpUtil.downloadFileWithOkHttp(masterUrl, filename + ".mp4", videofile, header);
					HttpUtil.downloadFileWithOkHttp(images.get(0), coverfile, coverdir, header);
					if (Global.getGeneratenfo) {
						String upload_date = DateUtils.formatDateTime(new Date(Long.parseLong(time)*1000));
						EmbyMetadataGenerator.createKuaiNfo(nickname, nickname, upload_date, keyid, title, title, coverfile,
								videofile);
					}
					videofile = videofile+filename + ".mp4";
					VideoDataEntity videoDataEntity = new VideoDataEntity(keyid, title, title, "小红书", coverunaddr,
							videofile,
							videounrealaddr, url);
					videoDataEntity.setVideoauthor(nickname);
					videoDataDao.save(videoDataEntity);
					processHistoryService.saveProcess(saveProcess.getId(), url,  "小红书");
					sendNotify.sendNotifyData(title+"(视频)", url,  "小红书");
					logger.info("下载流程结束");
					return;
				}else {
					Optional<GraphicContentEntity> byVideoidAndPlatform = graphicContentDao.findByVideoidAndPlatform(keyid,  Global.platform.rednote.name());
					if(byVideoidAndPlatform.isPresent()) {
						logger.info(url+"地址已存在,不处理");
						return;
					}
					//进图文
					downImages(images, videos,filename, header, keyid, url, title, markroute, desc, nickname, saveProcess);
					return;
				}
			}
		}
	}
	
	
	
	public void downImages(List<String> images,List<String> videos,String filename,Map<String, String> header,String keyid,String url,String title,String markroute,String desc,String nickname,ProcessHistoryEntity saveProcess) throws IOException {
		JSONArray imageurl=  new JSONArray();
		for(int i =0;i<images.size();i++) {
			 String storage = FileUtil.generateDir(true, Global.platform.rednote.name(), filename, null, null,i);
			 String cos = FileUtil.generateDir(false, Global.platform.rednote.name(), filename, null, "jpeg",i);
			 HttpUtil.downloadFileWithOkHttp(images.get(i), filename+"-index-"+i + ".jpeg", storage, header);
			 imageurl.add(cos);
		}
		if(videos!=null) {
			for(int v =0;v<videos.size();v++) {
    			String storage = FileUtil.generateDir(true, Global.platform.rednote.name(), filename, null, null,v);
				String cos = FileUtil.generateDir(false, Global.platform.rednote.name(), filename, null, "mp4",v);
				HttpUtil.downloadFileWithOkHttp(videos.get(v), filename+"-index-"+v+".mp4", storage, header);
				imageurl.add(cos);
			}
		}
		GraphicContentEntity graphicContentEntity = new GraphicContentEntity();
		graphicContentEntity.setVideoid(keyid);
		graphicContentEntity.setPlatform(Global.platform.rednote.name());
		graphicContentEntity.setOriginaladdress(url);
		graphicContentEntity.setTitle(title);
		graphicContentEntity.setMarkroute(markroute);
		graphicContentEntity.setContent(desc);
		graphicContentEntity.setImages(imageurl.toJSONString());
		graphicContentEntity.setAuthor(nickname);
		graphicContentEntity.setCreatetime(new Date());
		graphicContentDao.save(graphicContentEntity);
		sendNotify.sendNotifyData(filename+"(图文)", url, "小红书");
		processHistoryService.saveProcess(saveProcess.getId(), url, "小红书");
	}
	
	public void downVideos(List<String> videos,String filename,Map<String, String> header,String keyid,String url,String title,String markroute,String desc,String nickname,ProcessHistoryEntity saveProcess) throws IOException {
		
	}
	
	
	public List<String> getVideos(JSONArray video){
		List<String> res = new ArrayList<String>();
		if(video!= null) {
			for(int v =0;v<video.size();v++) {
				String masterUrl = video.getJSONObject(v).getString("masterUrl");
				res.add(masterUrl);
			}
		}
		return res;
	}

    
	public List<String> getImages(JSONArray arr){
		List<String> res = new ArrayList<String>();
		for(int i =0;i<arr.size();i++) {
			String string = arr.getJSONObject(i).getString("urlDefault");
			res.add(string);
		}
		return res;
	}

    
    /**
     * 从HTML内容中提取__INITIAL_STATE__的JSON字符串
     * @param htmlContent HTML内容
     * @return JSON字符串
     */
    private static String extractJsonString(String htmlContent) {
        // 使用正则表达式匹配window.__INITIAL_STATE__ = {...}
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
     * 获取指定路径的JSON值
     * @param jsonObject 根JSON对象
     * @param path 路径，用点分隔，例如 "global.appSettings.notificationInterval"
     * @return 对应路径的值，如果路径不存在返回null
     */
    public static Object getValueByPath(JSONObject jsonObject, String path) {
        if (jsonObject == null || path == null || path.isEmpty()) {
            return null;
        }
        
        String[] keys = path.split("\\.");
        Object current = jsonObject;
        
        for (String key : keys) {
            if (current instanceof JSONObject) {
                current = ((JSONObject) current).get(key);
            } else {
                return null;
            }
        }
        
        return current;
    }

}
