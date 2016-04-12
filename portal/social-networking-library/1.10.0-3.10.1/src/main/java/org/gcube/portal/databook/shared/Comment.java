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
public class Comment implements Serializable, Comparable<Comment>  {

	private String key;
	private String userid;
	private Date time;
	private String feedid;
	private String text;
	private String fullName;
	private String thumbnailURL;
	private boolean isEdit; // false default
	private Date lastEditTime; // null default
	
	/**
	 * 
	 */
	public Comment() {
		super();
	}
	/**
	 * 
	 * @param key
	 * @param userid
	 * @param timestamp
	 * @param feedid
	 * @param text
	 * @param fullName
	 * @param thumbnailURL
	 */
	public Comment(String key, String userid, Date time, String feedid,
			String text, String fullName, String thumbnailURL) {
		super();
		this.key = key;
		this.userid = userid;
		this.time = time;
		this.feedid = feedid;
		this.text = text;
		this.fullName = fullName;
		this.thumbnailURL = thumbnailURL;
		this.isEdit = false;
		this.lastEditTime = null;
		
	}
	
	/**
	 * Constructor for edited comment
	 * @param key
	 * @param userid
	 * @param time
	 * @param feedid
	 * @param text
	 * @param fullName
	 * @param thumbnailURL
	 * @param isEdit
	 * @param editDate
	 */
	public Comment(String key, String userid, Date time, String feedid,
			String text, String fullName, String thumbnailURL, boolean isEdit, Date editDate) {
		super();
		this.key = key;
		this.userid = userid;
		this.time = time;
		this.feedid = feedid;
		this.text = text;
		this.fullName = fullName;
		this.thumbnailURL = thumbnailURL;
		this.isEdit = isEdit;
		this.lastEditTime = editDate;
	}
	
	/**
	 * 
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * 
	 * @param text text to add as string
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * 
	 * @return the uuid
	 */
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

	public boolean isEdit() {
		return isEdit;
	}
	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}
	public Date getLastEditTime() {
		return lastEditTime;
	}
	public void setLastEditTime(Date lastEditTime) {
		this.lastEditTime = lastEditTime;
	}
	public int compareTo(Comment toCompare) {
		if (this.time.after(toCompare.getTime()))
			return 1;
		if (this.time.before(toCompare.getTime()))
			return -1;
		return 0;
	}
	
	@Override
	public String toString() {
		return "Comment [key=" + key + ", userid=" + userid + ", time=" + time
				+ ", feedid=" + feedid + ", text=" + text + ", fullName="
				+ fullName + ", thumbnailURL=" + thumbnailURL + ", isEdit="
				+ isEdit + ", lastEditTime=" + lastEditTime + "]";
	}
}
