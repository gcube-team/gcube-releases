package org.gcube.common.storagehub.client;

import java.io.InputStream;

public class StreamDescriptor {

	private InputStream stream;
	private String fileName;
	
	public StreamDescriptor(InputStream stream, String fileName) {
		super();
		this.stream = stream;
		this.fileName = fileName;
	}

	public InputStream getStream() {
		return stream;
	}

	public String getFileName() {
		return fileName;
	}
	
}
