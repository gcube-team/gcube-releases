package org.gcube.portlets.admin.software_upload_wizard.shared;

import java.io.Serializable;

public class SoftwareFileDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5772708958453093655L;
	
	private String filename;
	private String type;
	
	public SoftwareFileDetail() {
	}

	public SoftwareFileDetail(String filename, String type) {
		this.filename = filename;
		this.type = type;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
