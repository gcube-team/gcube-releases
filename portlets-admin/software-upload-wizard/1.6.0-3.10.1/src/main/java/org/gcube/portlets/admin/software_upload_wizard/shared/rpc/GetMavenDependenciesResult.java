package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import java.util.ArrayList;

import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenCoordinates;

import net.customware.gwt.dispatch.shared.Result;

public class GetMavenDependenciesResult implements Result {
	ArrayList<MavenCoordinates> dependencies;
	
	@SuppressWarnings("unused")
	private GetMavenDependenciesResult() {
		// Serialization only
	}

	public GetMavenDependenciesResult(ArrayList<MavenCoordinates> dependencies) {
		super();
		this.dependencies = dependencies;
	}

	public ArrayList<MavenCoordinates> getDependencies() {
		return dependencies;
	}
}
