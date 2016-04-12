package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class GetAllowedFileTypes implements Action<GetAllowedFileTypesResult> {

	private String packageId;
	
	@SuppressWarnings("unused")
	private GetAllowedFileTypes() {
		// Serialization only
	}

	public GetAllowedFileTypes(String packageId) {
		super();
		this.packageId = packageId;
	}
	
	public String getPackageId() {
		return packageId;
	}
	
}
