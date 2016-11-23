package org.gcube.portlets.widgets.applicationnews.shared;

import java.io.Serializable;

import org.gcube.portal.databook.shared.ImageType;

@SuppressWarnings("serial")
public class LinkPreview implements Serializable {
	
	private String title;
	private String description;
	private String linkThumbnailUrl;
	private ImageType imageType;

	public LinkPreview() {
		super();
	}

	public LinkPreview(String title, String description,
			String linkThumbnailUrl, ImageType imageType) {
		super();
		this.title = title;
		this.description = description;
		this.linkThumbnailUrl = linkThumbnailUrl;
		this.imageType = imageType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLinkThumbnailUrl() {
		return linkThumbnailUrl;
	}

	public void setLinkThumbnailUrl(String linkThumbnailUrl) {
		this.linkThumbnailUrl = linkThumbnailUrl;
	}

	public ImageType getImageType() {
		return imageType;
	}

	public void setImageType(ImageType imageType) {
		this.imageType = imageType;
	}

	@Override
	public String toString() {
		return "LinkPreview [title=" + title + ", description=" + description
				+ ", linkThumbnailUrl=" + linkThumbnailUrl + "]";
	}
	
}
