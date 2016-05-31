package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Result;

public class ValidateUploadedFilesResult implements Result {
	
	private boolean valid;
	
	@SuppressWarnings("unused")
	private ValidateUploadedFilesResult() {
		// Serialization only
	}

	public ValidateUploadedFilesResult(boolean valid) {
		this.valid=valid;
	}
	
	public boolean isValid() {
		return valid;
	}
	
}
