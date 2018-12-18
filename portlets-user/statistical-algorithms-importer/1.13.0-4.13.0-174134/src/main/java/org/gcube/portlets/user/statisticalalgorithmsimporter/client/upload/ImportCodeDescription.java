package org.gcube.portlets.user.statisticalalgorithmsimporter.client.upload;

import org.gcube.portlets.user.statisticalalgorithmsimporter.client.type.ImportCodeType;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ImportCodeDescription {
	private ImportCodeType importCodeType;
	private String file;

	public ImportCodeDescription(ImportCodeType importCodeType, String file) {
		super();
		this.importCodeType = importCodeType;
		this.file = file;
	}

	public ImportCodeType getImportCodeType() {
		return importCodeType;
	}

	public void setImportCodeType(ImportCodeType importCodeType) {
		this.importCodeType = importCodeType;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return "ImportCodeDescription [importCodeType=" + importCodeType
				+ ", file=" + file + "]";
	}

}
