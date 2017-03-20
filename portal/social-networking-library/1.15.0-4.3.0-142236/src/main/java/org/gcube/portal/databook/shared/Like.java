package org.gcube.portal.databook.shared;

import java.io.Serializable;
import java.util.Date;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * @version 0.1 July 2012
 *
 */
@SuppressWarnings("serial")
public class Like implements Serializable {

	private String key;
	private String userid;
	private Date time;
	private String feedid;
	private String fullName;
	private String thumbnailURL;

	public Like() {
		super();
	}

	public Like(String key, String userid, Date time, String feedid,
			String fullName, String thumbnailURL) {
		super();
		this.key = key;
		this.userid = userid;
		this.time = time;
		this.feedid = feedid;
		this.fullName = fullName;
		this.thumbnailURL = thumbnailURL;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getFeedid() {
		return feedid;
	}

	public void setFeedid(String feedid) {
		this.feedid = feedid;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getThumbnailURL() {
		return thumbnailURL;
	}

	public void setThumbnailURL(String thumbnailURL) {
		this.thumbnailURL = thumbnailURL;
	}
	
	public String toString() {
		return "KEY: " + key + " Time: "+ time + "\nuserid: "+userid + " Full name: " + fullName;
		
	}
}
