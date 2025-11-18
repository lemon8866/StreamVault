package com.flower.spirit.web.admin;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.common.RequestEntity;
import com.flower.spirit.config.Global;
import com.flower.spirit.entity.BiliConfigEntity;
import com.flower.spirit.entity.CollectDataDetailEntity;
import com.flower.spirit.entity.CollectDataEntity;
import com.flower.spirit.entity.ConfigEntity;
import com.flower.spirit.entity.CookiesConfigEntity;
import com.flower.spirit.entity.CookiesRequestEntity;
import com.flower.spirit.entity.DownloaderEntity;
import com.flower.spirit.entity.GraphicContentEntity;
import com.flower.spirit.entity.ProcessHistoryEntity;
import com.flower.spirit.entity.TikTokConfigEntity;
import com.flower.spirit.entity.UserEntity;
import com.flower.spirit.entity.VideoDataEntity;
import com.flower.spirit.entity.VideoMixEntity;
import com.flower.spirit.service.BiliConfigService;
import com.flower.spirit.service.CollectDataDetailService;
import com.flower.spirit.service.CollectDataService;
import com.flower.spirit.service.AnalysisService;
import com.flower.spirit.service.ConfigService;
import com.flower.spirit.service.CookiesConfigService;
import com.flower.spirit.service.DouYinService;
import com.flower.spirit.service.DownloaderService;
import com.flower.spirit.service.GraphicContentService;
import com.flower.spirit.service.ProcessHistoryService;
import com.flower.spirit.service.SystemService;
import com.flower.spirit.service.TikTokConfigService;
import com.flower.spirit.service.UserService;
import com.flower.spirit.service.VideoDataService;
import com.flower.spirit.service.VideoMixService;


/**
 * 
 * 后台api 控制器
 * @author flower
 *
 */
@RestController
@RequestMapping("/admin/api")
public class AdminController {
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private UserService userService;
	
	
	@Autowired
	private DownloaderService downloaderService;
	
	
	@Autowired
	private ConfigService configService;

	
	@Autowired
	private VideoDataService videoDataService;
	
	@Autowired
	private BiliConfigService biliConfigService;
	
	@Autowired
	private ProcessHistoryService processHistoryService;
	
	@Autowired
	private CollectDataService collectDataService;
	
	
	@Autowired
	private TikTokConfigService  tikTokConfigService;
	
	
	@Autowired
	private CollectDataDetailService collectDataDetailService;
	
	@Autowired
	private CookiesConfigService cookiesConfigService;
	
	@Autowired
	private DouYinService douYinService;
	
	@Autowired
	private VideoMixService videoMixService;
	
	@Autowired
	private GraphicContentService graphicContentService;
	
	@Autowired
	private AnalysisService analysisService;
	
	/**  
	
	 * <p>Title: login</p>  
	
	 * <p>Description:用户管理员登录 </p>  
	
	 * @param userEntity
	 * @return  
	
	 */  
	@PostMapping(value = "/login")
	public AjaxEntity login(UserEntity userEntity,HttpServletRequest request) {
		return systemService.loginUser(userEntity,request);
	}
/**  
	
	 * <p>Title: findUserList</p>  
	
	 * <p>Description:分页获取管理员的列表 </p>  
	
	 * @param res
	 * @param request
	 * @return  
	
	 */  
	@PostMapping(value = "/findUserList")
	public AjaxEntity findUserList(RequestEntity res,HttpServletRequest request) {
		return userService.findUserList(res);
	}
	
	/**  
	
	 * <p>Title: addUser</p>  
	
	 * <p>Description: 添加用户</p>  
	
	 * @param userEntity
	 * @param request
	 * @return  
	
	 */  
	@PostMapping(value = "/addUser")
	public AjaxEntity addUser(UserEntity userEntity,HttpServletRequest request) {
		return userService.addUser(userEntity);
	}
	
	/**
	 * 删除管理用户
	 * @param userEntity
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/delUser")
	public AjaxEntity delUser(UserEntity userEntity,HttpServletRequest request) {
		return userService.delUser(userEntity);
	}
	
	/**
	 * 分页获取下载器列表
	 * @param res
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/finddownLoaderList")
	public AjaxEntity finddownLoaderList(RequestEntity res,HttpServletRequest request) {
		return downloaderService.finddownLoaderList(res);
	}
	
	/**
	 * 删除下载器
	 * @param downloaderEntity
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/deleteDownLoader")
	public AjaxEntity delDownLoader(DownloaderEntity downloaderEntity,HttpServletRequest request) {
		return downloaderService.delDownLoader(downloaderEntity);
	}
	
	/**
	 * 新增或修改下载器
	 * @param downloaderEntity
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/addDownLoader")
	public AjaxEntity addDownLoader(DownloaderEntity downloaderEntity,HttpServletRequest request) {
		return downloaderService.addDownLoader(downloaderEntity);
	}
	/**
	 * 获取单个下载器信息
	 * @param downloaderEntity
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/getDownLoader")
	public AjaxEntity getDownLoader(DownloaderEntity downloaderEntity,HttpServletRequest request) {
		return downloaderService.getDownLoader(downloaderEntity);
	}
	
	/**
	 * 修改系统基础设置 apptoken
	 * @param configEntity
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/saveConfig")
	public AjaxEntity saveConfig(ConfigEntity configEntity,HttpServletRequest request) {
		return configService.saveConfig(configEntity);
	}
	/**
	 * 分页获取已缓存的视频历史记录
	 * @param videoDataEntity
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/findVideoDataList")
	public AjaxEntity findVideoDataList(VideoDataEntity videoDataEntity,HttpServletRequest request) {
		return videoDataService.findPage(videoDataEntity);
	}
	
	/**
	 * 删除视频缓存信息及视频文件
	 * @param downloaderEntity
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/deleteVideoData")
	public AjaxEntity deleteVideoData(VideoDataEntity downloaderEntity,HttpServletRequest request) {
		return videoDataService.deleteVideoData(downloaderEntity);
	}
	
	/**
	 * 更新视频基础文件
	 * @param downloaderEntity
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/updateVideoData")
	public AjaxEntity updateVideoData(VideoDataEntity downloaderEntity,HttpServletRequest request) {
		return videoDataService.updateVideoData(downloaderEntity);
	}
	
	
	/**修改Bili配置信息
	 * @param biliConfigEntity
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/updateBiliConfig")
	public AjaxEntity updateBiliConfig(BiliConfigEntity biliConfigEntity,HttpServletRequest request) {
		return biliConfigService.updateBiliConfig(biliConfigEntity);
	}
	
	/**
	 * 获取Bili 登录验证码
	 * @return
	 */
	@GetMapping(value = "/getBiliCode")
	public AjaxEntity getBiliCode() {
		return biliConfigService.getBiliCode();
	}
	
	
	/**
	 * 检查Bili 登录状态
	 * @param qrcodekey
	 * @return
	 */
	@GetMapping(value = "/checkBiliLogin")
	public AjaxEntity checkBiliLogin(String qrcodekey) {
		return biliConfigService.checkBiliLogin(qrcodekey);
	}
	
	/**
	 * 获取历史进度数据
	 * @param processHistoryEntity
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/findProcessHistoryList")
	public AjaxEntity findProcessHistoryList(ProcessHistoryEntity processHistoryEntity,HttpServletRequest request) {
		return processHistoryService.findPage(processHistoryEntity);
	}
	
	/**
	 * 删除历史进度数据
	 * @param processHistoryEntity
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/deleteProcessHistoryData")
	public AjaxEntity deleteProcessHistoryData(ProcessHistoryEntity processHistoryEntity,HttpServletRequest request) {
		return processHistoryService.deleteProcessHistoryData(processHistoryEntity);
	}
	
	/**
	 * 获取收藏夹分页
	 * @param collectDataEntity
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/findCollectDataList")
	public AjaxEntity findCollectDataList(CollectDataEntity collectDataEntity,HttpServletRequest request) {
		return collectDataService.findPage(collectDataEntity);
	}
	
	/**
	 * 删除收藏夹信息
	 * @param collectDataEntity
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/deleteCollectData")
	public AjaxEntity deleteCollectData(CollectDataEntity collectDataEntity,HttpServletRequest request) {
		return collectDataService.deleteCollectData(collectDataEntity);
	}
	/**
	 * 新建收藏夹信息
	 * @param collectDataEntity
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/submitCollectData")
	public AjaxEntity submitCollectData(CollectDataEntity collectDataEntity,HttpServletRequest request) {
		return collectDataService.saveCollectData(collectDataEntity);
	}
	
	
	/**
	 * 手动/创建之后立即执行任务
	 * @param collectDataEntity
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/execCollectData")
	public AjaxEntity execCollectData(CollectDataEntity collectDataEntity,HttpServletRequest request) {
		return collectDataService.execCollectData(collectDataEntity);
	}
	
	//updateTikTokConfig
	/**
	 * 更新抖音相关下载配置
	 * @param tikTokConfigEntity
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/updateTikTokConfig")
	public AjaxEntity updateTikTokConfig(TikTokConfigEntity tikTokConfigEntity,HttpServletRequest request) {
		return tikTokConfigService.updateTikTokConfig(tikTokConfigEntity);
	}
	
	
	/**
	 * 获取对应收藏夹明细
	 * @param entity
	 * @return
	 */
	@PostMapping(value = "/findCollectDataDetail")
	public AjaxEntity findCollectDataDetail(CollectDataDetailEntity entity) {
		return collectDataDetailService.findPage(entity);
	}

	/**
	 * 获取线程池状态信息
	 * 
	 * @return 线程池状态信息
	 */
	@GetMapping(value = "/getThreadPoolStatus")
	public AjaxEntity getThreadPoolStatus() {
		try {
			Map<String, Object> result = new HashMap<>();
			Map<String, Object> analysisStatus = analysisService.getThreadPoolStatus();
			result.put("analysis", analysisStatus);
			Map<String, Object> collectStatus = collectDataService.getCollectThreadPoolStatus();
			result.put("collect", collectStatus);

			return new AjaxEntity(Global.ajax_success, "获取线程池状态成功", result);
		} catch (Exception e) {
			return new AjaxEntity(Global.ajax_uri_error, "获取线程池状态失败: " + e.getMessage(), null);
		}
	}

	/**
	 * 更新Cookie 配置
	 * @param entity
	 * @return
	 */
	@PostMapping(value = "/updateCookie")
	public AjaxEntity updateCookie(CookiesConfigEntity entity) {
		return cookiesConfigService.updateCookie(entity);
	}
	
	
	@GetMapping(value = "/getDouYinCodeLogin")
	public AjaxEntity getDouYinCodeLogin() throws Exception {
		return douYinService.getDouYinCodeLogin();
	}
	
	@GetMapping(value = "/checkDouYinLogin")
	public AjaxEntity checkDouYinLogin(String token) throws Exception {
		return douYinService.checkLoginStatus(token);
	}
	@GetMapping(value = "/checkVersion")
	public AjaxEntity checkVersion() {
		return systemService.checkVersion();
	}
	
	@PostMapping(value = "/writeCookies")
	public AjaxEntity writeCookies(CookiesRequestEntity cookiesRequestEntity) {
		return cookiesConfigService.writeCookies(cookiesRequestEntity);
	}
	
	@PostMapping(value = "/checkCookies")
	public AjaxEntity checkCookies() {
		return cookiesConfigService.checkCookies();
	}
	
	@GetMapping(value = "/loadDouFav")
	public AjaxEntity loadDouFav(String uid) {
		return collectDataService.loadDouFav(uid);
	}
	
	@GetMapping(value = "/fixBiliFav")
	public AjaxEntity fixBiliFav(String id) {
		return collectDataService.fixBiliFav(id);
	}
	
	@PostMapping(value = "/getMixList")
	public AjaxEntity getMixList(VideoMixEntity entity) {
		return videoMixService.getMixList(entity);
	}
	
	@PostMapping(value = "/saveMix")
	public AjaxEntity saveMix(@RequestBody VideoMixEntity entity) {
		return videoMixService.saveMix(entity);
	}
	
	@GetMapping(value = "/deleteMix")
	public AjaxEntity deleteMix(String id) {
		return videoMixService.deleteMix(id);
	}
	
	@GetMapping(value = "/startMix")
	public AjaxEntity startMix(String id) {
		return videoMixService.startMix(id);
	}
	
	@PostMapping(value = "/ytextractor")
	public AjaxEntity ytextractor(VideoDataEntity enity) {
		return configService.ytextractor(enity);
	}
	
	/**
	 * 分页获取已缓存的视频历史记录
	 * 
	 * @param videoDataEntity
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/findGraphicContentList")
	public AjaxEntity findGraphicContentList(GraphicContentEntity graphicContentEntity, HttpServletRequest request) {
		return graphicContentService.findPage(graphicContentEntity);
	}

	@GetMapping(value = "/deleteGraphicContent")
	public AjaxEntity deleteGraphicContent(String id, HttpServletRequest request) {
		return graphicContentService.deleteGraphicContent(id);
	}

	/**
	 * 获取数据统计信息
	 * 
	 * @return 统计数据
	 */
	@GetMapping(value = "/getDataStatistics")
	public AjaxEntity getDataStatistics() {
		try {
			Map<String, Object> result = new HashMap<>();
			result.put("videoPlatformStats", videoDataService.countByVideoplatformGroupBy());
			result.put("graphicPlatformStats", graphicContentService.countByPlatformGroupBy());
			result.put("collectDataTotal", collectDataService.countTotal());
			// 添加今日新增数据统计
			result.put("videoTodayAdded", videoDataService.countTodayAdded());
			result.put("graphicTodayAdded", graphicContentService.countTodayAdded());
			return new AjaxEntity(Global.ajax_success, "获取数据统计成功", result);
		} catch (Exception e) {
			return new AjaxEntity(Global.ajax_uri_error, "获取数据统计失败: " + e.getMessage(), null);
		}
	}
	
	/**
	 * 删除视频缓存信息及视频文件
	 * @param downloaderEntity
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/refreshDanmu")
	public AjaxEntity refreshDanmu(VideoDataEntity downloaderEntity,HttpServletRequest request) {
		return videoDataService.refreshDanmu(downloaderEntity);
	}
	
	/**
	 * 更新并检查应用及yt-dlp版本
	 * @param checkAndUpdate
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/checkAndUpdate")
	public AjaxEntity checkAndUpdate(String proxyup) {
		return systemService.checkAndUpdate(proxyup);
	}
	
	/**
	 * 直链提交
	 * @param directData
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/directData")
	public AjaxEntity directData(VideoDataEntity video,HttpServletRequest request) {
		return analysisService.directData(video,request);
	}
}
