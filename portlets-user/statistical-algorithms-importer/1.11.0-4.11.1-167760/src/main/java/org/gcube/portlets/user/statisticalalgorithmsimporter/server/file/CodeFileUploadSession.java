package org.gcube.portlets.user.statisticalalgorithmsimporter.server.file;

import java.io.File;
import java.io.Serializable;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.file.FileUploadState;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public class CodeFileUploadSession implements Serializable {
	
	private static final long serialVersionUID = -7906477664944910362L;
	
	private String id;
	
	private FileUploadState fileUploadState;
	
	private File codeFile;
	private String codeName;
	
	

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


	public File getCodeFile() {
		return codeFile;
	}

	public void setCodeFile(File codeFile) {
		this.codeFile = codeFile;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	@Override
	public String toString() {
		return "CSVFileUploadSession [id=" + id + ", fileUploadState="
				+ fileUploadState + ", csvFile=" + codeFile + ", csvName="
				+ codeName + "]";
	}

	
	
		
}
