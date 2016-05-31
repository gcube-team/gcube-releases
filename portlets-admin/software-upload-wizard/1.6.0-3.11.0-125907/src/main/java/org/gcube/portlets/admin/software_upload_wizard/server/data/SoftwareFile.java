package org.gcube.portlets.admin.software_upload_wizard.server.data;

import java.io.File;


public class SoftwareFile {

	private String filename;
	private File file;
	private String typeName;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String type) {
		this.typeName = type;
	}

}
