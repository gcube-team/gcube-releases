package org.gcube.portlets.user.td.gwtservice.server.file;

import java.io.File;
import java.io.Serializable;

import org.gcube.portlets.user.td.gwtservice.shared.file.FileUploadState;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class CodelistMappingFileUploadSession implements Serializable {

	private static final long serialVersionUID = -5683392670926072167L;

	private String id;

	private FileUploadState fileUploadState;

	private File codelistMappingFile;
	private String codelistMappingName;

	//protected CodelistMappingMonitor codelistMappingMonitor;

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

	
	public File getCodelistMappingFile() {
		return codelistMappingFile;
	}

	public void setCodelistMappingFile(File codelistMappingFile) {
		this.codelistMappingFile = codelistMappingFile;
	}

	public String getCodelistMappingName() {
		return codelistMappingName;
	}

	public void setCodelistMappingName(String codelistMappingName) {
		this.codelistMappingName = codelistMappingName;
	}

	@Override
	public String toString() {
		return "CodelistMappingFileUploadSession [id=" + id
				+ ", fileUploadState=" + fileUploadState
				+ ", codelistMappingFile=" + codelistMappingFile
				+ ", codelistMappingName=" + codelistMappingName + "]";
	}

	
	
	
	
}
