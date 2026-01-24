package com.flower.spirit.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Transient;

import com.flower.spirit.common.DataEntity;


/**
 * 视频资源
 * @author flower
 *
 */
@Entity
@Table(name = "biz_video")
public class VideoDataEntity  extends DataEntity<VideoDataEntity> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7980669221676123703L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE,generator="biz_video_seq")
	@TableGenerator(name = "biz_video_seq", allocationSize = 1, table = "seq_common", pkColumnName = "seq_id", valueColumnName = "seq_count")
    private Integer id;
	
	/**
	 * 对应视频站的视频id
	 */
	private String videoid;
	
	/**
	 * 源地址
	 */
	public String originaladdress;
	
	private String videoname;
	
	private String videoauthor;
	
	private String videodesc;
	
	private String videoprivacy;  //视频是否隐私模式  隐私模式 则不会直接显示 图片
	
	private String videotag;     //视频tag
	
	private String videoplatform;
	
	private String videocover;
	
	private String videounrealaddr;
	
	private String videoaddr;
	
	private Date createtime;

	private String videoinfo;
	
	
	
	
	public VideoDataEntity() {
		super();
	}

	public VideoDataEntity(String videoid,String videoname, String videodesc, String videoplatform, String videocover,
			String videoaddr,String videounrealaddr,String originaladdress) {
		super();
		this.videoid = videoid;
		this.videoname = videoname;
		this.videodesc = videodesc;
		this.videoplatform = videoplatform;
		this.videocover = videocover;
		this.videoaddr = videoaddr;
		this.createtime = new Date();
		this.videounrealaddr =videounrealaddr;
		this.originaladdress  = originaladdress;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getVideoname() {
		return videoname;
	}

	public void setVideoname(String videoname) {
		this.videoname = videoname;
	}

	public String getVideoplatform() {
		return videoplatform;
	}

	public void setVideoplatform(String videoplatform) {
		this.videoplatform = videoplatform;
	}

	public String getVideocover() {
		return videocover;
	}

	public void setVideocover(String videocover) {
		this.videocover = videocover;
	}

	public String getVideoaddr() {
		return videoaddr;
	}

	public void setVideoaddr(String videoaddr) {
		this.videoaddr = videoaddr;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getVideodesc() {
		return videodesc;
	}

	public void setVideodesc(String videodesc) {
		this.videodesc = videodesc;
	}

	public String getVideoprivacy() {
		return videoprivacy;
	}

	public void setVideoprivacy(String videoprivacy) {
		this.videoprivacy = videoprivacy;
	}

	public String getVideotag() {
		return videotag;
	}

	public void setVideotag(String videotag) {
		this.videotag = videotag;
	}

	public String getVideounrealaddr() {
		return videounrealaddr;
	}

	public void setVideounrealaddr(String videounrealaddr) {
		this.videounrealaddr = videounrealaddr;
	}

	public String getOriginaladdress() {
		return originaladdress;
	}

	public void setOriginaladdress(String originaladdress) {
		this.originaladdress = originaladdress;
	}

	public String getVideoid() {
		return videoid;
	}

	public void setVideoid(String videoid) {
		this.videoid = videoid;
	}

	public String getVideoinfo() {
		return videoinfo;
	}

	public void setVideoinfo(String videoinfo) {
		this.videoinfo = videoinfo;
	}

	public String getVideoauthor() {
		return videoauthor;
	}

	public void setVideoauthor(String videoauthor) {
		this.videoauthor = videoauthor;
	}
	
	/**
	 * 排除的平台（用于查询时排除特定平台的视频，不持久化到数据库）
	 */
	@Transient
	private String excludePlatform;
	
	public String getExcludePlatform() {
		return excludePlatform;
	}
	
	public void setExcludePlatform(String excludePlatform) {
		this.excludePlatform = excludePlatform;
	}
	

}
