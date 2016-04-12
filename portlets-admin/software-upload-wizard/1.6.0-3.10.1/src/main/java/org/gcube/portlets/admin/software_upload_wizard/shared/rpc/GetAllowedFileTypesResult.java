package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Result;

import org.gcube.portlets.admin.software_upload_wizard.shared.filetypes.FileType;

public class GetAllowedFileTypesResult implements Result {
	
	private ArrayList<FileType> fileTypes;
	
	@SuppressWarnings("unused")
	private GetAllowedFileTypesResult() {
		// Serialization only
	}

	public GetAllowedFileTypesResult(ArrayList<FileType> fileTypes) {
		super();
		this.fileTypes = fileTypes;
	}
	
	public ArrayList<FileType> getFileTypes() {
		return fileTypes;
	}

}
