package com.flower.spirit.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.flower.spirit.entity.VideoMixEntity;
import com.flower.spirit.entity.VideoMixSegmentEntity;
import com.flower.spirit.utils.FileUtil;
import com.flower.spirit.entity.VideoDataEntity;
import com.flower.spirit.dao.VideoMixDao;
import com.flower.spirit.dao.VideoMixSegmentDao;
import com.flower.spirit.dao.VideoDataDao;
import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.Global;

@Service
public class VideoMixService {

	private ExecutorService ffmpeg = Executors.newFixedThreadPool(1);

	@Autowired
	private VideoMixDao videoMixDao;

	@Autowired
	private VideoMixSegmentDao videoMixSegmentDao;

	@Autowired
	private VideoDataDao videoDataDao;

	public AjaxEntity getMixList(VideoMixEntity entity) {
		try {
			Specification<VideoMixEntity> specification = (root, query, cb) -> {
				List<Predicate> predicates = new ArrayList<>();
				if (entity.getMixName() != null && !entity.getMixName().isEmpty()) {
					predicates.add(cb.like(root.get("mixName"), "%" + entity.getMixName() + "%"));
				}
				if (entity.getStatus() != null && !entity.getStatus().isEmpty()) {
					predicates.add(cb.equal(root.get("status"), entity.getStatus()));
				}

				return cb.and(predicates.toArray(new Predicate[0]));
			};
			Sort sort = Sort.by(Direction.DESC, "createTime");
			PageRequest pageRequest = PageRequest.of(
					Math.max(0, entity.getPageNo() - 1), // 确保页码不小于0
					entity.getPageSize() > 0 ? entity.getPageSize() : 10, // 确保每页数量大于0
					sort);

			Page<VideoMixEntity> page = videoMixDao.findAll(specification, pageRequest);
			for (VideoMixEntity mix : page.getContent()) {
				List<VideoMixSegmentEntity> segments = videoMixSegmentDao
						.findByVideomixidOrderBySegmentNoAsc(mix.getId());
				mix.setSegments(segments);
			}
			return new AjaxEntity(Global.ajax_success, "查询成功", page);

		} catch (Exception e) {
			return new AjaxEntity(Global.ajax_uri_error, "查询失败", null);
		}

	}

	public AjaxEntity saveMix(VideoMixEntity entity) {
		try {
			// 1. 保存VideoMixEntity主体
			entity.setCreateTime(new Date());
			entity.setUpdateTime(new Date());
			entity.setStatus("待处理"); // 设置初始状态
			VideoMixEntity savedMix = videoMixDao.save(entity);

			// 2. 保存VideoMixSegmentEntity列表
			if (entity.getSegments() != null && !entity.getSegments().isEmpty()) {
				for (VideoMixSegmentEntity segment : entity.getSegments()) {
					segment.setVideomixid(savedMix.getId()); // 设置关联ID
					segment.setCreateTime(new Date());
				}
				videoMixSegmentDao.saveAll(entity.getSegments());
			}

			return new AjaxEntity(Global.ajax_success, "保存成功", savedMix);
		} catch (Exception e) {
			return new AjaxEntity(Global.ajax_uri_error, "保存失败：" + e.getMessage(), null);
		}
	}

	@Transactional
	public AjaxEntity deleteMix(String id) {
		try {
			// 1. 先删除子表数据
			videoMixSegmentDao.deleteByVideomixid(Integer.parseInt(id));
			videoMixDao.deleteById(Integer.parseInt(id));

			return new AjaxEntity(Global.ajax_success, "删除成功", null);
		} catch (Exception e) {
			return new AjaxEntity(Global.ajax_uri_error, "删除失败：" + e.getMessage(), null);
		}
	}

	public AjaxEntity startMix(String id) {
		// 混剪目录 定义为svkpop
		try {
			// 1. 更新状态为"进行中"
			VideoMixEntity mix = videoMixDao.findById(Integer.parseInt(id))
					.orElseThrow(() -> new RuntimeException("混剪不存在"));
			mix.setStatus("进行中");
			mix.setUpdateTime(new Date());
			videoMixDao.save(mix);
			String fileDir = FileUtil.generateDir(true, "svkpop", true, mix.getMixName(), null, null);
			// 2. 获取所有视频片段
			List<VideoMixSegmentEntity> segments = videoMixSegmentDao
					.findByVideomixidOrderBySegmentNoAsc(Integer.parseInt(id));
			// System.out.println(fileDir);
			// 3. 在线程池中处理视频
			ffmpeg.submit(() -> {
				try {
					// 创建临时目录
					String tempDir = fileDir + "temp/";
					new File(tempDir).mkdirs();

					// 创建文件列表文件
					String listFile = tempDir + "filelist.txt";
					FileWriter writer = new FileWriter(listFile);

					// 处理每个视频片段
					try {
						for (VideoMixSegmentEntity segment : segments) {
							// 获取视频信息
							VideoDataEntity video = videoDataDao.findById(segment.getVideoid())
									.orElseThrow(() -> new RuntimeException("视频不存在"));

							// 构建临时文件路径
							String tempFile = tempDir + "segment_" + segment.getSegmentNo() + ".mp4";
//							if(Global.encoder == null) {
//								EncoderUtil.checkAndSetEncoder();
//							}
							// 使用FFmpeg提取视频片段并统一为30fps
							String ffmpegCmd = String.format(
							    "ffmpeg -y -i %s -ss %d -t %d -r 30 -c:v %s -crf 23 -preset medium -threads 4 %s",
							    video.getVideoaddr(),
							    segment.getStartTime(),
							    segment.getEndTime() - segment.getStartTime(),
							    Global.encoder,  // 使用动态选择的编码器
							    tempFile
							);

							Process process = null;
							BufferedReader errorReader = null;
							try {
								// 执行FFmpeg命令
								process = Runtime.getRuntime().exec(ffmpegCmd);

								// 读取错误流
								errorReader = new BufferedReader(
										new InputStreamReader(process.getErrorStream()));
								StringBuilder errorOutput = new StringBuilder();
								String errorLine;
								while ((errorLine = errorReader.readLine()) != null) {
									errorOutput.append(errorLine).append("\n");
								}

								process.waitFor();

								// 检查命令执行结果
								if (process.exitValue() != 0) {
									throw new RuntimeException("FFmpeg处理失败: " + errorOutput.toString());
								}

								// 写入文件列表
								writer.write("file '" + tempFile + "'\n");
							} finally {
								if (errorReader != null) {
									try {
										errorReader.close();
									} catch (Exception e) {
										// 忽略关闭异常
									}
								}
								if (process != null) {
									process.destroy();
								}
							}
						}
					} finally {
						if (writer != null) {
							try {
								writer.close();
							} catch (Exception e) {
								// 忽略关闭异常
							}
						}
					}

					// 合并所有视频片段（不包含音频）
					String outputFile = fileDir + mix.getMixName() + ".mp4";
					Process mergeProcess = null;
					BufferedReader mergeErrorReader = null;
					try {
						String mergeCmd = String.format(
								"ffmpeg -y -f concat -safe 0 -i %s -c copy -threads 4 %s",
								listFile,
								outputFile);

						mergeProcess = Runtime.getRuntime().exec(mergeCmd);
						mergeErrorReader = new BufferedReader(new InputStreamReader(mergeProcess.getErrorStream()));
						StringBuilder errorOutput = new StringBuilder();
						String errorLine;
						while ((errorLine = mergeErrorReader.readLine()) != null) {
							errorOutput.append(errorLine).append("\n");
						}

						mergeProcess.waitFor();

						if (mergeProcess.exitValue() != 0) {
							throw new RuntimeException("视频合并失败: " + errorOutput.toString());
						}

						// 更新状态为完成
						mix.setStatus("完成");
						mix.setUpdateTime(new Date());
						mix.setOutputPath(outputFile);
						videoMixDao.save(mix);

						// 生成缩略图
						try {
							String thumbnailPath = outputFile.substring(0, outputFile.lastIndexOf(".")) + ".jpg";
							String thumbnailCmd = String.format(
									"ffmpeg -y -i %s -ss 00:00:01 -vframes 1 -q:v 2 %s",
									outputFile,
									thumbnailPath);

							Process thumbnailProcess = Runtime.getRuntime().exec(thumbnailCmd);
							thumbnailProcess.waitFor();

							if (thumbnailProcess.exitValue() == 0) {
								mix.setThumbnailPath(thumbnailPath);
								videoMixDao.save(mix);
							}
						} catch (Exception e) {
							System.err.println("生成缩略图失败: " + e.getMessage());
						}

						System.gc();
						// 清理临时文件
						try {
							File tempDirFile = new File(tempDir);
							if (tempDirFile.exists()) {
								File[] tempFiles = tempDirFile.listFiles();
								if (tempFiles != null) {
									for (File file : tempFiles) {
										file.delete();
									}
								}
								tempDirFile.delete();
							}
						} catch (Exception e) {
							System.err.println("清理临时文件失败: " + e.getMessage());
						}
						//建档
						String vid = Integer.toString(mix.getId())+Long.toString(mix.getCreateTime().getTime());
						String coverunaddr = FileUtil.generateDir(false, "svkpop", true, mix.getMixName(), null, "jpg");
						String videounrealaddr = FileUtil.generateDir(false, "svkpop", true, mix.getMixName(), null, "mp4");
						VideoDataEntity videoDataEntity = new VideoDataEntity(vid, StringUtil.getFileName(mix.getMixName(), vid), StringUtil.cleanText(mix.getMixName()), "StreamVault", coverunaddr, outputFile,
								videounrealaddr, "合并任务无地址");
						videoDataDao.save(videoDataEntity);
					} finally {
						if (mergeErrorReader != null) {
							try {
								mergeErrorReader.close();
							} catch (Exception e) {
								// 忽略关闭异常
							}
						}
						if (mergeProcess != null) {
							mergeProcess.destroy();
						}
					}

				} catch (Exception e) {
					// 发生错误时更新状态为"失败"
					mix.setStatus("失败");
					mix.setUpdateTime(new Date());
					videoMixDao.save(mix);

					// 记录错误日志
					e.printStackTrace();
				}
			});

			return new AjaxEntity(Global.ajax_success, "开始处理混剪", null);
		} catch (Exception e) {
			return new AjaxEntity(Global.ajax_uri_error, "处理失败：" + e.getMessage(), null);
		}
	}

}
