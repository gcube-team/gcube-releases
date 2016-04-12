package org.gcube.portal.databook.shared;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 * @version 0.2 Nov 2012
 *
 */
@SuppressWarnings("serial")
public class Notification implements Serializable {

	private String key;
	private NotificationType type;
	private String userid;
	private String subjectid;
	private Date time;
	private String uri;
	private String description;
	private boolean read;
	private String senderid;
	private String senderFullName;
	private String senderThumbnail;


	/**
	 * default constructor
	 */
	public Notification() {
		super();
	}



	/**
	 * 
	 * @param key
	 * @param type
	 * @param userid
	 * @param subjectid the subject id of this notification, if is a like on a feed then is the feedid, it is a message then is the messageid and so on 
	 * @param time
	 * @param uri
	 * @param description
	 * @param read
	 * @param senderid
	 * @param senderFullName
	 * @param senderThumbnail
	 */
	public Notification(String key, NotificationType type, String userid,
			String subjectid, Date time, String uri, String description,
			boolean read, String senderid, String senderFullName,
			String senderThumbnail) {
		super();
		this.key = key;
		this.type = type;
		this.userid = userid;
		this.subjectid = subjectid;
		this.time = time;
		this.uri = uri;
		this.description = description;
		this.read = read;
		this.senderid = senderid;
		this.senderFullName = senderFullName;
		this.senderThumbnail = senderThumbnail;
	}




	/**
	 * 
	 * @return .
	 */
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public NotificationType getType() {
		return type;
	}
	public void setType(NotificationType type) {
		this.type = type;
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
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
	public String getSenderid() {
		return senderid;
	}
	public void setSenderid(String senderid) {
		this.senderid = senderid;
	}
	public String getSenderFullName() {
		return senderFullName;
	}
	public void setSenderFullName(String senderFullName) {
		this.senderFullName = senderFullName;
	}
	public String getSenderThumbnail() {
		return senderThumbnail;
	}
	public void setSenderThumbnail(String senderThumbnail) {
		this.senderThumbnail = senderThumbnail;
	}
	public String getSubjectid() {
		return subjectid;
	}
	public void setSubjectid(String subjectid) {
		this.subjectid = subjectid;
	}

	@Override
	public String toString() {
		return "Notification [key=" + key + ", type=" + type + ", userid="
				+ userid + ", subjectid=" + subjectid + ", time=" + time
				+ ", uri=" + uri + ", description=" + description + ", read="
				+ read + ", senderid=" + senderid + ", senderFullName="
				+ senderFullName + ", senderThumbnail=" + senderThumbnail + "]";
	}	
	
	
}
