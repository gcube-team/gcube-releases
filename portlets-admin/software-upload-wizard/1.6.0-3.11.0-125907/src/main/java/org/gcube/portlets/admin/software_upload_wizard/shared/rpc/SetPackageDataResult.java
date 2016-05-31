package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData;

import net.customware.gwt.dispatch.shared.Result;

public class SetPackageDataResult implements Result {
	
	String packageId;
	PackageData data;
	
	@SuppressWarnings("unused")
	private SetPackageDataResult() {
		// Serialization only
	}
	
	public SetPackageDataResult(String packageId, PackageData data) {
		super();
		this.packageId = packageId;
		this.data = data;
	}

	public String getPackageId() {
		return packageId;
	}
	
	public PackageData getData() {
		return data;
	}

}
