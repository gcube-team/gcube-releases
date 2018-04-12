package org.gcube.portal.databook.shared;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class ClientAttachment {
	public String id;
	public String uri;
	public String name;
	public String description;
	public String thumbnailURL;
	public String mimeType;
	/**
	 * @param id the id in the cassandra CF
	 * @param uri where you can download the file from
	 * @param name the name of the attached file
	 * @param description the description of the attached file
	 * @param thumbnailURL the URL of the image representing the attached file
	 * @param mimeType the type of file
	 */
	@JsOverlay
	public static ClientAttachment create(String id, String uri, String name, String description, String thumbnailURL, String mimeType) {
		ClientAttachment o = new ClientAttachment();
		o.id = id;
		o.uri = uri;
		o.name = name;
		o.description = description;
		o.thumbnailURL = thumbnailURL;
		o.mimeType = mimeType;
		return o;
	}
}
