package com.flower.spirit.entity;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

@Entity
@Table(name = "biz_cookies_config")
public class CookiesConfigEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 574236590065434047L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE,generator="biz_cookies_config")
	@TableGenerator(name = "biz_cookies_config", allocationSize = 1, table = "seq_common", pkColumnName = "seq_id", valueColumnName = "seq_count")
    private Integer id;
	
	private String youtubecookies;
	
	private String twittercookies;
	
	private String kuaishouCookie;
	
	private String weibocookie;
	
	private String rednotecookie;
	
	private String cookiecloud;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getYoutubecookies() {
		return youtubecookies;
	}

	public void setYoutubecookies(String youtubecookies) {
		this.youtubecookies = youtubecookies;
	}

	public String getTwittercookies() {
		return twittercookies;
	}

	public void setTwittercookies(String twittercookies) {
		this.twittercookies = twittercookies;
	}

	public String getKuaishouCookie() {
		return kuaishouCookie;
	}

	public void setKuaishouCookie(String kuaishouCookie) {
		this.kuaishouCookie = kuaishouCookie;
	}

	public String getWeibocookie() {
		return weibocookie;
	}

	public void setWeibocookie(String weibocookie) {
		this.weibocookie = weibocookie;
	}

	public String getRednotecookie() {
		return rednotecookie;
	}

	public void setRednotecookie(String rednotecookie) {
		this.rednotecookie = rednotecookie;
	}

	public String getCookiecloud() {
		return cookiecloud;
	}

	public void setCookiecloud(String cookiecloud) {
		this.cookiecloud = cookiecloud;
	}	
	
	

}
