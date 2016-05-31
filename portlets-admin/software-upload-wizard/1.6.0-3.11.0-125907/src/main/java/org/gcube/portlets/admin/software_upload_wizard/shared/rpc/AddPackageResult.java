package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Result;

public class AddPackageResult implements Result {
	
	private String packageId;
	
	private AddPackageResult() {
		// Serialization only
	}

	public AddPackageResult(String packageId) {
		super();
		this.packageId = packageId;
	}

	public String getPackageId() {
		return packageId;
	}
	
}
