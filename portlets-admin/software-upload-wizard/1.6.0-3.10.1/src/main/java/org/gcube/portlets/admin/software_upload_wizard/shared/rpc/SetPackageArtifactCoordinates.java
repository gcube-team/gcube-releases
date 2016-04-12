package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenCoordinates;

public class SetPackageArtifactCoordinates implements
		Action<SetPackageArtifactCoordinatesResult> {
	
	private String packageId;
	private MavenCoordinates artifactCoordinates;
	
	@SuppressWarnings("unused")
	private SetPackageArtifactCoordinates() {
		// Serialization only
	}
	
	public SetPackageArtifactCoordinates(String packageId,
			MavenCoordinates artifactCoordinates) {
		super();
		this.packageId = packageId;
		this.artifactCoordinates = artifactCoordinates;
	}

	public String getPackageId() {
		return packageId;
	}

	public MavenCoordinates getArtifactCoordinates() {
		return artifactCoordinates;
	}

}
