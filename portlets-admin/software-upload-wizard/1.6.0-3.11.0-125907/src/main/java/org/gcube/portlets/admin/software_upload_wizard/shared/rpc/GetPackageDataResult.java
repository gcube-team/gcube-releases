package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData;

import net.customware.gwt.dispatch.shared.Result;

public class GetPackageDataResult implements Result {

	private String packageId;
	private PackageData data;
	
	@SuppressWarnings("unused")
	private GetPackageDataResult() {
		// Serialization only
	}
	
	public GetPackageDataResult(String packageId, PackageData data) {
		this.packageId = packageId;
		this.data = data;
	}

	public GetPackageDataResult(PackageData data) {
		this.data=data;
	}
	
	public String getPackageId() {
		return packageId;
	}

	public PackageData getData() {
		return data;
	}

}
