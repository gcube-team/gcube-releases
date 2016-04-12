package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Result;

import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenCoordinates;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenVersionRule;

public class GetPackageArtifactCoordinatesResult implements Result {

	private MavenCoordinates artifactCoordinates;
	private MavenVersionRule versionRule;
	
	@SuppressWarnings("unused")
	private GetPackageArtifactCoordinatesResult() {
		// Serialization only
	}

	public GetPackageArtifactCoordinatesResult(
			MavenCoordinates artifactCoordinates, MavenVersionRule versionRule) {
		super();
		this.artifactCoordinates = artifactCoordinates;
		this.versionRule=versionRule;
	}

	public MavenCoordinates getArtifactCoordinates() {
		return artifactCoordinates;
	}
	
	public MavenVersionRule getVersionRule() {
		return versionRule;
	}

}
