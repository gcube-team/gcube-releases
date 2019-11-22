package org.gcube.portal.databook.shared;

import java.util.Date;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
/**
 * 
 * @author Massimiliano Assante, CNR-ISTI
 * Uses JsInterop annotations to deserialize the object
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ClientPost {
	public String key;
	public String type;
	public String userid;
	public Date time;
	public String uri;
	public String description;
	public String fullName;
	public String email;
	public String thumbnailURL;
	public String linkTitle;
	public String linkDescription;
	public String linkUrlThumbnail;
	public String linkHost;
	public ClientAttachment[] attachments;
	@JsOverlay
	public static ClientPost create(String key, String type, String userid, Date time,
			String uri, String description, String fullName, String email,
			String thumbnailURL, String linkTitle, String linkDescription,
			String linkUrlThumbnail, String linkHost, ClientAttachment[] attachments) {
		ClientPost o = new ClientPost();
		o.key = key;
		o.type = type;
		o.userid = userid;
		o.time = time;
		o.uri = uri;
		o.description = description;
		o.fullName = fullName;
		o.email = email;
		o.thumbnailURL = thumbnailURL;
		o.linkTitle = linkTitle;
		o.linkDescription = linkDescription;
		o.linkUrlThumbnail = linkUrlThumbnail;
		o.linkHost = linkHost;
		o.attachments = attachments;		
		return o;
	}
}
