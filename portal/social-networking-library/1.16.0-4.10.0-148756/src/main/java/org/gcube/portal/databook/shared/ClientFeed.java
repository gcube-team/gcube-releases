package org.gcube.portal.databook.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.jsonmaker.gwt.client.Jsonizer;


/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
@SuppressWarnings("serial")
public class ClientFeed implements Serializable {
	
	public interface ClientFeedJsonizer extends Jsonizer {}
	
	private String key;
	private String type;
	private String userid;
	private Date time;
	private String uri;
	private String description;
	private String fullName;
	private String email;
	private String thumbnailURL;
	private String linkTitle;
	private String linkDescription;
	private String linkUrlThumbnail;
	private String linkHost;
	private List<Attachment> attachments;

	public ClientFeed() {
		super();
	}	
	
	public ClientFeed(String key, String type, String userid, Date time,
			String uri, String description, String fullName, String email,
			String thumbnailURL, String linkTitle, String linkDescription,
			String linkUrlThumbnail, String linkHost, List<Attachment> attachments) {
		super();
		this.key = key;
		this.type = type;
		this.userid = userid;
		this.time = time;
		this.uri = uri;
		this.description = description;
		this.fullName = fullName;
		this.email = email;
		this.thumbnailURL = thumbnailURL;
		this.linkTitle = linkTitle;
		this.linkDescription = linkDescription;
		this.linkUrlThumbnail = linkUrlThumbnail;
		this.linkHost = linkHost;
		this.attachments = attachments;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
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

	public String getLinkUrlThumbnail() {
		return linkUrlThumbnail;
	}

	public void setLinkUrlThumbnail(String linkUrlThumbnail) {
		this.linkUrlThumbnail = linkUrlThumbnail;
	}

	public String getLinkHost() {
		return linkHost;
	}

	public void setLinkHost(String linkHost) {
		this.linkHost = linkHost;
	}
	

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	@Override
	public String toString() {
		return "ClientFeed [key=" + key + ", type=" + type + ", userid="
				+ userid + ", time=" + time + ", uri=" + uri + ", description="
				+ description + ", fullName=" + fullName + ", email=" + email
				+ ", thumbnailURL=" + thumbnailURL + ", linkTitle=" + linkTitle
				+ ", linkDescription=" + linkDescription
				+ ", linkUrlThumbnail=" + linkUrlThumbnail + ", linkHost="
				+ linkHost + ", attachments=" + attachments + "]";
	}
}
