package org.gcube.portlets.user.td.widgetcommonevent.shared.thumbnail;

import java.io.Serializable;

/**
 * Thumbnail for Tabular Data Manager
 * 
 * @author Giancarlo Panichi
 * 
 */
public class ThumbnailTD implements Serializable {

	private static final long serialVersionUID = 897893891284145975L;

	private String url;
	private String mimeType;

	public ThumbnailTD() {

	}

	public ThumbnailTD(String url, String mimeType) {
		super();
		this.url = url;
		this.mimeType = mimeType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public String toString() {
		return "ThumbnailTD [url=" + url + ", mimeType=" + mimeType + "]";
	}

}
