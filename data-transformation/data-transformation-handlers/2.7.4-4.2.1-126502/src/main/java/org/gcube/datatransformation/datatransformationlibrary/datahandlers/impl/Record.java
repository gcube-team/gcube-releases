package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl;

import java.io.Serializable;

class Record implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String payload;
	private String mimetype;


	boolean isInitialised() {
		return id != null && mimetype != null && payload != null;
	}
	
	public Record() {
	}

	public Record(String id, String payload, String mimetype) {
		this.id = id;
		this.payload = payload;
		this.mimetype = mimetype;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	@Override
	public String toString() {
		return "Record [id=" + id + ", mimetype=" + mimetype + ", payload=" + payload + "]";
	}
}
