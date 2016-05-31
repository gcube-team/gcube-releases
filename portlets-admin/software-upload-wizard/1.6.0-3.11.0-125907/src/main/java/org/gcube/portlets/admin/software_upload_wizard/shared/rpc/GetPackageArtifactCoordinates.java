package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class GetPackageArtifactCoordinates implements Action<GetPackageArtifactCoordinatesResult> {

	private String packageId;
	
	
	@SuppressWarnings("unused")
	private GetPackageArtifactCoordinates() {
		// Serialization only
	}

	public GetPackageArtifactCoordinates(String packageId) {
		super();
		this.packageId = packageId;
		
	}

	public String getPackageId() {
		return packageId;
	}


	
}
