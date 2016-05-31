package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class GetPackageFiles implements Action<GetPackageFilesResult> {

	private String packageId;
	
	@SuppressWarnings("unused")
	private GetPackageFiles() {
		// Serialization only
	}

	public GetPackageFiles(String packageId) {
		super();
		this.packageId = packageId;
	}
	
	public String getPackageId() {
		return packageId;
	}
	
}
