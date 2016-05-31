package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class GetPackageData implements Action<GetPackageDataResult> {

	private String packageId;
	
	@SuppressWarnings("unused")
	private GetPackageData() {
		// Serialization only
	}

	public GetPackageData(String packageId) {
		this.packageId = packageId;
	}
	
	public String getPackageId() {
		return packageId;
	}

}
