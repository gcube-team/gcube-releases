package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import java.util.ArrayList;

import org.gcube.portlets.admin.software_upload_wizard.shared.SoftwareFileDetail;

import net.customware.gwt.dispatch.shared.Result;

public class GetPackageFilesResult implements Result {
	
	ArrayList<SoftwareFileDetail> files;
	
	@SuppressWarnings("unused")
	private GetPackageFilesResult() {
		// Serialization only
	}

	public GetPackageFilesResult(ArrayList<SoftwareFileDetail> files) {
		super();
		this.files = files;
	}
	
	public ArrayList<SoftwareFileDetail> getFiles() {
		return files;
	}

}
