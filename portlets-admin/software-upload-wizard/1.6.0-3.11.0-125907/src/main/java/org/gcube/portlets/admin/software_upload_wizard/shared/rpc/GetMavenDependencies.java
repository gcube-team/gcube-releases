package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class GetMavenDependencies implements Action<GetMavenDependenciesResult> {
	
	private String packageId;
	
	@SuppressWarnings("unused")
	private GetMavenDependencies() {
		// Serialization only
	}

	public GetMavenDependencies(String packageId) {
		super();
		this.packageId = packageId;
	}

	public String getPackageId() {
		return packageId;
	}
	
	
	
}
