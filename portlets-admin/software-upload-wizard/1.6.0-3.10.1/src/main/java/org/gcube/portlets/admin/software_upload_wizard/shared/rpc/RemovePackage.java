package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class RemovePackage implements Action<RemovePackageResult> {
	
	private String packageId;
	
	@SuppressWarnings("unused")
	private RemovePackage() {
		// Serialization only
	}

	public RemovePackage(String packageId) {
		super();
		this.packageId = packageId;
	}

	public String getPackageId() {
		return packageId;
	}

}
