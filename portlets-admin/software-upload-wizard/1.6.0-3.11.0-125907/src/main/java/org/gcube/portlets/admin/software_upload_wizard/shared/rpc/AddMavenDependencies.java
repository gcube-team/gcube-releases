package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Action;

import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenCoordinates;

public class AddMavenDependencies implements Action<AddMavenDependenciesResult> {
	
	private ArrayList<MavenCoordinates> dependencies;
	private String packageId;
	
	@SuppressWarnings("unused")
	private AddMavenDependencies() {
		// Serialization only
	}

	public AddMavenDependencies(String packageId, ArrayList<MavenCoordinates> dependencies) {
		super();
		this.packageId=packageId;
		this.dependencies = dependencies;
	}

	public String getPackageId() {
		return packageId;
	}

	public ArrayList<MavenCoordinates> getDependencies() {
		return dependencies;
	}

}
