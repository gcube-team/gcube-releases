package org.gcube.portlets.user.td.gwtservice.server.file;

import java.io.File;
import java.io.Serializable;

import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVParserConfiguration;
import org.gcube.portlets.user.td.gwtservice.shared.file.FileUploadState;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class CSVFileUploadSession implements Serializable {

	private static final long serialVersionUID = -7906477664944910362L;

	private String id;

	private FileUploadState fileUploadState;

	private File csvFile;
	private String csvName;

	protected CSVParserConfiguration parserConfiguration;

	// protected CSVImportMonitor csvImportMonitor;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public FileUploadState getFileUploadState() {
		return fileUploadState;
	}

	public void setFileUploadState(FileUploadState fileUploadState) {
		this.fileUploadState = fileUploadState;
	}

	public File getCsvFile() {
		return csvFile;
	}

	public void setCsvFile(File csvFile) {
		this.csvFile = csvFile;
	}

	public String getCsvName() {
		return csvName;
	}

	public void setCsvName(String csvName) {
		this.csvName = csvName;
	}

	public CSVParserConfiguration getParserConfiguration() {
		return parserConfiguration;
	}

	public void setParserConfiguration(
			CSVParserConfiguration parserConfiguration) {
		this.parserConfiguration = parserConfiguration;
	}

	@Override
	public String toString() {
		return "CSVFileUploadSession [id=" + id + ", fileUploadState="
				+ fileUploadState + ", csvFile=" + csvFile + ", csvName="
				+ csvName + ", parserConfiguration=" + parserConfiguration
				+ "]";
	}

}
