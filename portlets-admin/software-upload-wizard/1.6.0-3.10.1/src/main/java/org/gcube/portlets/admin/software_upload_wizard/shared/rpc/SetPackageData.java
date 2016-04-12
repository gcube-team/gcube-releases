package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.PackageData;

import net.customware.gwt.dispatch.shared.Action;

public class SetPackageData implements Action<SetPackageDataResult> {

	String packageId;
	PackageData data;
	
	@SuppressWarnings("unused")
	private SetPackageData() {
		// Serialization only
	}

	public SetPackageData(String packageId, PackageData data) {
		this.packageId=packageId;
		this.data = data;
	}
	
	public String getPackageId() {
		return packageId;
	}

	public PackageData getData() {
		return data;
	}

}
