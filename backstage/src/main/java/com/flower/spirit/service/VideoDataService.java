package com.flower.spirit.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.VideoDataDao;
import com.flower.spirit.entity.VideoDataEntity;
import com.flower.spirit.utils.BiliUtil;
import com.flower.spirit.utils.CommandUtil;
import com.flower.spirit.utils.StringUtil;


@Service
public class VideoDataService {
	
    @Value("${file.save}")
    private String savefile;
    
    @Value("${file.save.path}")
    private String uploadRealPath;
	
	
	@Autowired
	private VideoDataDao videoDataDao;

	private Logger logger = LoggerFactory.getLogger(VideoDataService.class);

	public List<VideoDataEntity> findByVideoid(String videoid) {
		return videoDataDao.findByVideoid(videoid);
	}
	
	
	public AjaxEntity findPage(VideoDataEntity res) {
	    PageRequest of = PageRequest.of(res.getPageNo(), res.getPageSize());

	    Specification<VideoDataEntity> specification = (root, query, cb) -> {
	        List<Predicate> predicates = new ArrayList<>();

	        if (res != null) {
	            if (StringUtil.isString(res.getVideoname()) && StringUtil.isString(res.getVideodesc())) {
	                predicates.add(cb.or(
	                        cb.like(root.get("videoname"), "%" + res.getVideoname() + "%"),
	                        cb.like(root.get("videodesc"), "%" + res.getVideodesc() + "%")
	                ));
	            } else if (StringUtil.isString(res.getVideoname())) {
	                predicates.add(cb.like(root.get("videoname"), "%" + res.getVideoname() + "%"));
	            } else if (StringUtil.isString(res.getVideodesc())) {
	                predicates.add(cb.like(root.get("videodesc"), "%" + res.getVideodesc() + "%"));
	            }

	            if (StringUtil.isString(res.getVideoplatform())) {
	                predicates.add(cb.like(root.get("videoplatform"), "%" + res.getVideoplatform() + "%"));
	            }
	            
	            if (StringUtil.isString(res.getVideotag())) {
	                predicates.add(cb.like(root.get("videotag"), "%" + res.getVideotag() + "%"));
	            }
	            if (StringUtil.isString(res.getVideoauthor())) {
	                predicates.add(cb.like(root.get("videoauthor"), "%" + res.getVideoauthor() + "%"));
	            }
	        }

	        query.orderBy(cb.desc(root.get("id")));
	        return cb.and(predicates.toArray(new Predicate[0]));
	    };

	    Page<VideoDataEntity> findAll = videoDataDao.findAll(specification, of);
	    return new AjaxEntity(Global.ajax_success, "数据获取成功", findAll);
	}

	/**
	 * 删除
	 * @param downloaderEntity
	 * @return
	 */
	public AjaxEntity deleteVideoData(VideoDataEntity data) {
		//删除也要删除资源
		Optional<VideoDataEntity> findById = videoDataDao.findById(data.getId());
		if(findById.isPresent()) {
			VideoDataEntity videoDataEntity = findById.get();
			//新版 优化 此处可以修改为直接删除文件夹
//			FileUtils.deleteFile(videoDataEntity.getVideoaddr()); 
//			FileUtils.deleteParentDirectorie(videoDataEntity.getVideoaddr());  //直接删除上级文件夹
			CommandUtil.deleteDirectory(new File(videoDataEntity.getVideoaddr()).getParentFile().getPath());//更换系统级原生删除方法
//			FileUtils.deleteFile(videoDataEntity.getVideocover().replace(savefile, uploadRealPath));
			videoDataDao.deleteById(data.getId());
		}
		return new AjaxEntity(Global.ajax_success, "操作成功", null);
	}

	/**
	 * 更新
	 * @param data
	 * @return
	 */
	public AjaxEntity updateVideoData(VideoDataEntity data) {
		Optional<VideoDataEntity> findById = videoDataDao.findById(data.getId());
		if (findById.isPresent()) {
			VideoDataEntity videoDataEntity = findById.get();
			videoDataEntity.setVideoprivacy(data.getVideoprivacy());
			videoDataEntity.setVideotag(data.getVideotag());
			videoDataDao.save(videoDataEntity);
		}
		return new AjaxEntity(Global.ajax_success, "操作成功", null);
	}

	public ResponseEntity<StreamingResponseBody> playVideo(HttpHeaders headers, String video) throws IOException {
		if (video != null && !video.isEmpty()) {
			Optional<VideoDataEntity> findById = videoDataDao.findById(Integer.valueOf(video));
			if (findById.isPresent()) {
				VideoDataEntity videoDataEntity = findById.get();
				File videoFile = new File(videoDataEntity.getVideoaddr());
				long fileLength = videoFile.length();
				String mimeType = Files.probeContentType(videoFile.toPath());
				List<HttpRange> httpRanges = headers.getRange();

				long start = 0;
				long end = fileLength - 1;
				boolean isPartial = false;

				if (!httpRanges.isEmpty()) {
					// 只处理第一个 range
					HttpRange range = httpRanges.get(0);
					start = range.getRangeStart(fileLength);
					end = range.getRangeEnd(fileLength);
					isPartial = true;
				}

				long rangeLength = end - start + 1;
				long finalStart = start;
				long finalEnd = end;

				StreamingResponseBody responseBody = outputStream -> {
					try (RandomAccessFile raf = new RandomAccessFile(videoFile, "r")) {
						raf.seek(finalStart);
						try (InputStream inputStream = new BufferedInputStream(new FileInputStream(raf.getFD()))) {
							byte[] buffer = new byte[8192];
							long remaining = rangeLength;
							int bytesRead;
							while (remaining > 0) {
								bytesRead = inputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining));
								if (bytesRead == -1) {
									break;
								}
								try {
									outputStream.write(buffer, 0, bytesRead);
								} catch (IOException e) {
									// 客户端断开连接，停止写入，记录日志
									logger.warn("客户端断开连接，停止视频流传输: {}", e.toString());
									break;
								}
								remaining -= bytesRead;
							}
							outputStream.flush(); // 确保数据发送完毕
						}
					} catch (IOException e) {
						// 其他IO异常，记录错误日志
						logger.error("视频流传输异常", e);
					}
				};

				HttpHeaders responseHeaders = new HttpHeaders();
				responseHeaders.set(HttpHeaders.CONTENT_TYPE, mimeType);
				responseHeaders.set(HttpHeaders.ACCEPT_RANGES, "bytes");

				if (isPartial) {
					responseHeaders.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(rangeLength));
					responseHeaders.set(HttpHeaders.CONTENT_RANGE,
							String.format("bytes %d-%d/%d", finalStart, finalEnd, fileLength));
					return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
							.headers(responseHeaders)
							.body(responseBody);
				} else {
					responseHeaders.set(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength));
					return ResponseEntity.ok()
							.headers(responseHeaders)
							.body(responseBody);
				}
			}
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}

	public Map<String, Long> countByVideoplatformGroupBy() {
		List<Object[]> videoPlatformStats = videoDataDao.countByVideoplatformGroupBy();
		Map<String, Long> videoPlatformMap = new HashMap<>();
		for (Object[] stat : videoPlatformStats) {
			String platform = (String) stat[0];
			Long count = (Long) stat[1];
			videoPlatformMap.put(platform != null ? platform : "未知", count);
		}
		return videoPlatformMap;
	}

	/**
	 * 统计今日新增视频数量
	 * 
	 * @return 今日新增视频数量
	 */
	public Long countTodayAdded() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date startDate = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date endDate = calendar.getTime();
		return videoDataDao.countTodayAdded(startDate, endDate);
	}


	/**
	 * 刷新弹幕
	 * @param data
	 * @return
	 */
	public AjaxEntity refreshDanmu(VideoDataEntity data) {
	    Optional<VideoDataEntity> findById = videoDataDao.findById(data.getId());
	    if (!findById.isPresent()) {
	        return new AjaxEntity(Global.ajax_uri_error, "视频资源不存在,刷新失败", null);
	    }
	    VideoDataEntity videoDataEntity = findById.get();
	    if (!(videoDataEntity.getVideoplatform().equals(Global.platform.bilibili.name()) || videoDataEntity.getVideoplatform().equals("哔哩"))) {
	        return new AjaxEntity(Global.ajax_uri_error, "当前平台暂时不支持刷新弹幕,目前仅支持BiliBili", null);
	    }
	    String videoinfo = videoDataEntity.getVideoinfo();
	    if (videoinfo == null || videoinfo.isEmpty()) {
	        return new AjaxEntity(Global.ajax_uri_error, "当前视频未旧版数据,暂时不支持刷新弹幕", null);
	    }
	    String videoaddr = videoDataEntity.getVideoaddr();
	    JSONObject video = JSONObject.parseObject(videoinfo);
	    String filepathname = videoaddr.substring(0, videoaddr.lastIndexOf(".")) + ".ass";
	    BiliUtil.biliDanmaku("1", videoDataEntity.getVideoid(), video.getString("aid"), Integer.valueOf(video.getString("duration")), filepathname, videoDataEntity.getVideoname());
	    
	    return new AjaxEntity(Global.ajax_success, "刷新成功", null);
	}

	public VideoDataEntity findRandomByVideoplatform(String platform) {
		return videoDataDao.findRandomByVideoplatform(platform);
	}

}
