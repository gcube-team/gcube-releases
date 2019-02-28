package org.gcube.common.storagehub.client;

import java.io.InputStream;

public class StreamDescriptor {

	private InputStream stream;
	private String fileName;
	private String contentType;
	
	public StreamDescriptor(InputStream stream, String fileName, String contentType) {
		super();
		this.stream = stream;
		this.fileName = fileName;
		this.contentType= contentType;
	}

	public InputStream getStream() {
		return stream;
	}

	public String getFileName() {
		return fileName;
	}

	public String getContentType() {
		return contentType;
	}
	
}
