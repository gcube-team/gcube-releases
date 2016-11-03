package org.gcube.portal.databook.shared;

import java.io.Serializable;

import org.jsonmaker.gwt.client.Jsonizer;

@SuppressWarnings("serial")
public class Attachment implements Serializable {
	
	public interface AttachmentJsonizer extends Jsonizer {}
	
	private String id;
	private String uri;
	private String name;
	private String description;
	private String thumbnailURL;
	private String mimeType;

	public Attachment() {
		super();
	}

	/**
	 * @param id the id in the cassandra CF
	 * @param uri where you can download the file from
	 * @param name the name of the attached file
	 * @param description the description of the attached file
	 * @param thumbnailURL the URL of the image representing the attached file
	 * @param mimeType the type of file
	 */
	public Attachment(String id, String uri, String name, String description,
			String thumbnailURL, String mimeType) {
		super();
		this.id = id;
		this.uri = uri;
		this.name = name;
		this.description = description;
		this.thumbnailURL = thumbnailURL;
		this.mimeType = mimeType;
	}	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getThumbnailURL() {
		return thumbnailURL;
	}


	public void setThumbnailURL(String thumbnailURL) {
		this.thumbnailURL = thumbnailURL;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public String toString() {
		return "Attachment [uri=" + uri + ", name=" + name + ", description="
				+ description + ", thumbnailURL=" + thumbnailURL
				+ ", mimeType=" + mimeType + "]";
	}




}
