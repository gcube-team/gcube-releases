package org.gcube.portlets.user.results.shared;

import java.io.InputStream;
import java.net.URL;

public class ContentInfo {

	private URL url;
	private String mimeType;
	private InputStream is;
	
	public ContentInfo(URL url, String mimeType, InputStream stream) {
		this.url = url;
		this.mimeType = mimeType;
		this.is = stream;
	}

	public URL getUrl() {
		return url;
	}

	public String getMimeType() {
		return mimeType;
	}

	public InputStream getIs() {
		return is;
	}

}
