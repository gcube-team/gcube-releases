package org.gcube.portal.databook.shared;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
@SuppressWarnings("serial")
public class Feed implements Serializable, Comparable<Feed> {

	private String key;
	private FeedType type;
	private String entityId;
	private Date time;
	private String vreid;
	private String uri;
	private String uriThumbnail;
	private String description;
	private PrivacyLevel privacy;
	private String fullName;
	private String email;
	private String thumbnailURL;
	private String commentsNo;
	private String likesNo;
	private String linkTitle;
	private String linkDescription;
	private String linkHost;
	boolean applicationFeed;
	/**
	 * this boolean indicates that the attachments to the post are > 1
	 */
	boolean multiFileUpload;
	/**
	 * default constructor
	 */
	public Feed() {
		super();
	}
	/**
	 * To use ONLY for USER Feeds
	 * 
	 * 
	 * @param key a UUID
	 * @param type an instance of <class>FeedType</class>
	 * @param entityId the user or the app unique indentifier
	 * @param timestamp when
	 * @param vreid a unique vre id
	 * @param uri optional uri
	 * @param uriThumbnail the thumbnail for the link posted
	 * @param description optional description
	 * @param privacy the privacy level of <class>PrivacyLevel</class>
	 * @param fullName
	 * @param email
	 * @param thumbnailURL this is the user thumbnail url
	 * @param linkTitle optional to be used when posting links
	 * @param linkDescription optional to be used when posting links
	 * @param linkHost option to be used when posting linkgs
	  */
	public Feed(String key, FeedType type, String entityId, Date time,
			String vreid, String uri, String uriThumbnail, String description, PrivacyLevel privacy,
			String fullName, String email, String thumbnailURL, String linkTitle, String linkDescription, String linkHost) {
		this.key = key;
		this.type = type;
		this.entityId = entityId;
		this.time = time;
		this.vreid = vreid;
		this.uri = uri;
		this.uriThumbnail = uriThumbnail;
		this.description = description;
		this.privacy = privacy;
		this.fullName = fullName;
		this.email = email;
		this.thumbnailURL = thumbnailURL;
		this.commentsNo = "0";
		this.likesNo = "0";
		this.linkDescription = linkDescription;
		this.linkTitle = linkTitle;
		this.linkHost = linkHost;
		this.applicationFeed = false;
	}
	/**
	 * To use for USER and ApplicationProfile Feeds
	 *  
	 * @param key a UUID
	 * @param type an instance of <class>FeedType</class>
	 * @param entityId the user or the app unique indentifier
	 * @param timestamp when
	 * @param vreid a unique vre id
	 * @param uri optional uri
	 * @param uriThumbnail the thumbnail for the link posted
	 * @param description optional description
	 * @param privacy the privacy level of <class>PrivacyLevel</class>
	 * @param fullName
	 * @param email
	 * @param thumbnailURL this is the user thumbnail url
	 * @param linkTitle optional to be used when posting links
	 * @param linkDescription optional to be used when posting links
	 * @param applicationFeed tell if this is an application feed or a user feed
	  */
	public Feed(String key, FeedType type, String entityId, Date time,
			String vreid, String uri, String uriThumbnail, String description, PrivacyLevel privacy,
			String fullName, String email, String thumbnailURL, String linkTitle, String linkDescription, String linkHost, boolean applicationFeed) {
		this(key, type, entityId, time, vreid, uri, uriThumbnail, description, privacy, fullName, email, thumbnailURL, linkTitle, linkDescription, linkHost);
		this.applicationFeed = applicationFeed;
	}
	
	
	/**
	 * for serialization purposes
	 * @param key a UUID
	 * @param type an instance of <class>FeedType</class>
	 * @param entityId the user or the app unique indentifier
	 * @param timestamp when
	 * @param vreid a unique vre id
	 * @param uri optional uri
	 * @param uriThumbnail the thumbnail for the link posted
	 * @param description optional description
	 * @param privacy the privacy level of <class>PrivacyLevel</class>
	 * @param fullName
	 * @param email
	 * @param thumbnailURL this is the user thumbnail url
	 * @param linkTitle optional to be used when posting links
	 * @param linkDescription optional to be used when posting links
	 */
	public Feed(String key, FeedType type, String entityId, Date time,
			String vreid, String uri, String uriThumbnail, String description, PrivacyLevel privacy,
			String fullName, String email, String thumbnailURL, String commentsNo,
			String likesNo, String linkTitle, String linkDescription, String linkHost, boolean applicationFeed, boolean multiFileUpload) {
		super();
		this.key = key;
		this.type = type;
		this.entityId = entityId;
		this.time = time;
		this.vreid = vreid;
		this.uri = uri;
		this.uriThumbnail = uriThumbnail;
		this.description = description;
		this.privacy = privacy;
		this.fullName = fullName;
		this.email = email;
		this.thumbnailURL = thumbnailURL;
		this.commentsNo = commentsNo;
		this.likesNo = likesNo;
		this.linkDescription = linkDescription;
		this.linkTitle = linkTitle;
		this.linkHost = linkHost;
		this.applicationFeed = applicationFeed;
		this.multiFileUpload = multiFileUpload;
	}
	/**
	 * 
	 * @return
	 */
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public FeedType getType() {
		return type;
	}

	public void setType(FeedType type) {
		this.type = type;
	}
	/**
	 * 
	 * @return the User or the App id
	 */
	public String getEntityId() {
		return entityId;
	}
	/**
	 * set the User or the App id
	 * @param entityId the UserId or the AppId id
	 */
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	
	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getVreid() {
		return vreid;
	}

	public void setVreid(String vreid) {
		this.vreid = vreid;
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

	public PrivacyLevel getPrivacy() {
		return privacy;
	}

	public void setPrivacy(PrivacyLevel privacy) {
		this.privacy = privacy;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getThumbnailURL() {
		return thumbnailURL;
	}

	public void setThumbnailURL(String thumbnailURL) {
		this.thumbnailURL = thumbnailURL;
	}
	
	public String getCommentsNo() {
		return commentsNo;
	}
	public void setCommentsNo(String commentsNo) {
		this.commentsNo = commentsNo;
	}
	public String getLikesNo() {
		return likesNo;
	}
	public void setLikesNo(String likesNo) {
		this.likesNo = likesNo;
	}	
	public String getUriThumbnail() {
		return uriThumbnail;
	}
	public void setUriThumbnail(String uriThumbnail) {
		this.uriThumbnail = uriThumbnail;
	}

	public String getLinkTitle() {
		return linkTitle;
	}
	public void setLinkTitle(String linkTitle) {
		this.linkTitle = linkTitle;
	}
	public String getLinkDescription() {
		return linkDescription;
	}
	public void setLinkDescription(String linkDescription) {
		this.linkDescription = linkDescription;
	}
	public int compareTo(Feed toCompare) {
		if (this.time.after(toCompare.getTime()))
			return 1;
		if (this.time.before(toCompare.getTime()))
			return -1;
		return 0;
	}
	public String getLinkHost() {
		return linkHost;
	}
	public void setLinkHost(String linkHost) {
		this.linkHost = linkHost;
	}
	
	public boolean isApplicationFeed() {
		return applicationFeed;
	}
	public void setApplicationFeed(boolean applicationFeed) {
		this.applicationFeed = applicationFeed;
	}
	public boolean isMultiFileUpload() {
		return multiFileUpload;
	}
	public void setMultiFileUpload(boolean multiFileUpload) {
		this.multiFileUpload = multiFileUpload;
	}
	@Override
	public String toString() {
		return "Feed [key=" + key + ", type=" + type + ", entityId=" + entityId
				+ ", time=" + time + ", vreid=" + vreid + ", uri=" + uri
				+ ", uriThumbnail=" + uriThumbnail + ", description="
				+ description + ", privacy=" + privacy + ", fullName="
				+ fullName + ", email=" + email + ", thumbnailURL="
				+ thumbnailURL + ", commentsNo=" + commentsNo + ", likesNo="
				+ likesNo + ", linkTitle=" + linkTitle + ", linkDescription="
				+ linkDescription + ", linkHost=" + linkHost
				+ ", applicationFeed=" + applicationFeed
				+ ", multiFileUpload=" + multiFileUpload + "]";
	}

}
