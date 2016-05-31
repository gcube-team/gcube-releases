package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class ValidateUploadedFiles implements
		Action<ValidateUploadedFilesResult> {

	private String packageId;
	
	@SuppressWarnings("unused")
	private ValidateUploadedFiles() {
		// Serialization only
	}

	public ValidateUploadedFiles(String packageId) {
		super();
		this.packageId = packageId;
	}
	
	public String getPackageId() {
		return packageId;
	}
	
}
