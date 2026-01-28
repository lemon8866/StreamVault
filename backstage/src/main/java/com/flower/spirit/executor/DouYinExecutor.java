package com.flower.spirit.executor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.GraphicContentDao;
import com.flower.spirit.entity.GraphicContentEntity;
import com.flower.spirit.entity.ProcessHistoryEntity;
import com.flower.spirit.service.ProcessHistoryService;
import com.flower.spirit.utils.CommandUtil;
import com.flower.spirit.utils.DouUtil;
import com.flower.spirit.utils.FileUtil;
import com.flower.spirit.utils.HttpUtil;
import com.flower.spirit.utils.StringUtil;
import com.flower.spirit.utils.sendNotify;

/**
 * 这里是新版的写法 日后为了扩展性  新功能 将使用单独的executor  然后最后由入口发起选择调度
 * 但是这里只是负责解析  返回标准数据  传回调度 由调度再负责保存等实际业务逻辑
 * 旧版解析器 看时间 也可能会迁移 也许能用就算了
 */
@Service
public class DouYinExecutor {
	
	@Autowired
	private GraphicContentDao graphicContentDao;
	
	@Autowired
	private ProcessHistoryService processHistoryService;
	
    private static GraphicContentDao staticGraphicContentDao;
    
    private static ProcessHistoryService staticprocessHistoryService;

    @PostConstruct
    public void init() {
        staticGraphicContentDao = graphicContentDao;
        staticprocessHistoryService = processHistoryService;
    }
	
	
	/**
	 * 图文  图文暂时没有提交记录
	 * @param postid
	 * @throws IOException 
	 */
	public static void ImageTextExecutor(String originaladdress,String post) throws IOException {
		ProcessHistoryEntity saveProcess = staticprocessHistoryService.saveProcess(null, originaladdress, "抖音");
		String taskout = Global.apppath + "lot" +System.getProperty("file.separator") + "imageText_"+post + ".json";
		GraphicContentEntity graphicContentEntity = new GraphicContentEntity();
		graphicContentEntity.setVideoid(post);
		graphicContentEntity.setPlatform(Global.platform.douyin.name());
		
		Optional<GraphicContentEntity> byVideoidAndPlatform = staticGraphicContentDao.findByVideoidAndPlatform(post,Global.platform.douyin.name());
		if(byVideoidAndPlatform.isPresent()) {
			return;
		}
		String f2cmd = CommandUtil.f2cmd(Global.tiktokCookie, post, "fetch_post_data", null, null, null, taskout);
		if (null != f2cmd && f2cmd.contains("stream-vault-ok")) {
			String json = FileUtil.readJson(taskout);
			JSONObject object = JSONObject.parseObject(json);
			//判断
			JSONObject aweme_detail = object.getJSONObject("aweme_detail");
			String desc = aweme_detail.getString("desc");
			String nickname = aweme_detail.getJSONObject("author").getString("nickname");
			JSONArray images = aweme_detail.getJSONArray("images");
			JSONArray imageList=  new JSONArray();
			HashMap<String, String> header = new HashMap<String, String>();
			header.put("Referer", "https://www.douyin.com/");
			header.put("User-Agent", DouUtil.ua);
			header.put("cookie", Global.tiktokCookie);
			String filename = StringUtil.getFileName(desc, post);
			String markroute = FileUtil.generateDir(true, Global.platform.douyin.name(), filename, null, null,0);
			for(int i = 0;i<images.size();i++) {
				JSONObject video = images.getJSONObject(i).getJSONObject("video");
				if(null !=video) {
					//多
					String videoplay ="";
					JSONArray jsonArray = video.getJSONObject("play_addr").getJSONArray("url_list");
					 if(jsonArray.size() >=2) {
						 videoplay = jsonArray.getString(jsonArray.size()-1);
					 }else {
						 videoplay = jsonArray.getString(0);
					 }
					String storage = FileUtil.generateDir(true, Global.platform.douyin.name(), filename, null, null,i);
					String cos = FileUtil.generateDir(false, Global.platform.douyin.name(), filename, null, "mp4",i);
					imageList.add(cos);
					HttpUtil.downloadFileWithOkHttp(videoplay, filename+"-index-"+i + ".mp4", storage, header);
				}else {
					//普通
					String picaddr ="";
					JSONArray piclist = images.getJSONObject(i).getJSONArray("url_list");
					 if(piclist.size() >=2) {
						 picaddr = piclist.getString(piclist.size()-1);
					 }else {
						 picaddr = piclist.getString(0);
					 }
					 String storage = FileUtil.generateDir(true, Global.platform.douyin.name(), filename, null, null,i);
					 String cos = FileUtil.generateDir(false, Global.platform.douyin.name(), filename, null, "jpeg",i);
					 HttpUtil.downloadFileWithOkHttp(picaddr, filename+"-index-"+i + ".jpeg", storage, header);
					 imageList.add(cos);
				}
			}
	
			graphicContentEntity.setOriginaladdress(originaladdress);
			graphicContentEntity.setTitle(StringUtil.getFileName(desc, post));
			graphicContentEntity.setMarkroute(markroute);
			graphicContentEntity.setContent(StringUtil.cleanText(desc));
			graphicContentEntity.setImages(imageList.toJSONString());
			graphicContentEntity.setAuthor(nickname);
			graphicContentEntity.setCreatetime(new Date());
			staticGraphicContentDao.save(graphicContentEntity);
			Files.deleteIfExists(Paths.get(taskout));
			sendNotify.sendNotifyData(filename, originaladdress, "抖音");
			staticprocessHistoryService.saveProcess(saveProcess.getId(), originaladdress, "抖音");
		}

		
		
		
	}
	
	
	public static void ImageTextExecutor(String post,String type,String patch) throws IOException {
		String taskout = Global.apppath + "lot" +System.getProperty("file.separator") + "imageText_"+post + ".json";
		GraphicContentEntity graphicContentEntity = new GraphicContentEntity();
		graphicContentEntity.setVideoid(post);
		graphicContentEntity.setPlatform(Global.platform.douyin.name());
		
		Optional<GraphicContentEntity> byVideoidAndPlatform = staticGraphicContentDao.findByVideoidAndPlatform(post,Global.platform.douyin.name());
		if(byVideoidAndPlatform.isPresent()) {
			return;
		}
		String f2cmd = CommandUtil.f2cmd(Global.tiktokCookie, post, "fetch_post_data", null, null, null, taskout);
		if (null != f2cmd && f2cmd.contains("stream-vault-ok")) {
			String json = FileUtil.readJson(taskout);
			JSONObject object = JSONObject.parseObject(json);
			//判断
			JSONObject aweme_detail = object.getJSONObject("aweme_detail");
			String desc = aweme_detail.getString("desc");
			String nickname = aweme_detail.getJSONObject("author").getString("nickname");
			JSONArray images = aweme_detail.getJSONArray("images");
			JSONArray imageList=  new JSONArray();
			HashMap<String, String> header = new HashMap<String, String>();
			header.put("Referer", "https://www.douyin.com/");
			header.put("User-Agent", DouUtil.ua);
			header.put("cookie", Global.tiktokCookie);
			String filename = StringUtil.getFileName(desc, post);
			String markroute = FileUtil.generateDir(true, Global.platform.douyin.name(), filename, null, null,0);
			for(int i = 0;i<images.size();i++) {
				JSONObject video = images.getJSONObject(i).getJSONObject("video");
				if(null !=video) {
					//多
					String videoplay ="";
					JSONArray jsonArray = video.getJSONObject("play_addr").getJSONArray("url_list");
					 if(jsonArray.size() >=2) {
						 videoplay = jsonArray.getString(jsonArray.size()-1);
					 }else {
						 videoplay = jsonArray.getString(0);
					 }
					String storage = FileUtil.generateDir(true, Global.platform.douyin.name(), filename, null, null,i);
					String cos = FileUtil.generateDir(false, Global.platform.douyin.name(), filename, null, "mp4",i);
					imageList.add(cos);
					HttpUtil.downloadFileWithOkHttp(videoplay, filename+"-index-"+i + ".mp4", storage, header);
				}else {
					//普通
					String picaddr ="";
					JSONArray piclist = images.getJSONObject(i).getJSONArray("url_list");
					 if(piclist.size() >=2) {
						 picaddr = piclist.getString(piclist.size()-1);
					 }else {
						 picaddr = piclist.getString(0);
					 }
					 String storage = FileUtil.generateDir(true, Global.platform.douyin.name(), filename, null, null,i);
					 String cos = FileUtil.generateDir(false, Global.platform.douyin.name(), filename, null, "jpeg",i);
					 HttpUtil.downloadFileWithOkHttp(picaddr, filename+"-index-"+i + ".jpeg", storage, header);
					 imageList.add(cos);
				}
			}
	
			graphicContentEntity.setOriginaladdress(type);
			graphicContentEntity.setTitle(StringUtil.getFileName(desc, post));
			graphicContentEntity.setMarkroute(markroute);
			graphicContentEntity.setContent(StringUtil.cleanText(desc));
			graphicContentEntity.setImages(imageList.toJSONString());
			graphicContentEntity.setAuthor(nickname);
			graphicContentEntity.setCreatetime(new Date());
			staticGraphicContentDao.save(graphicContentEntity);
			Files.deleteIfExists(Paths.get(taskout));
		}

		
		
		
	}

}
