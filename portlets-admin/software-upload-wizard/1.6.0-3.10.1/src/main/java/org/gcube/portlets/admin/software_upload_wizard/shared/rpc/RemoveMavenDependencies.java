package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Action;

import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenCoordinates;

public class RemoveMavenDependencies implements
		Action<RemoveMavenDependenciesResult> {
	
	private String packageId;
	private ArrayList<MavenCoordinates> dependencies;
	
	@SuppressWarnings("unused")
	private RemoveMavenDependencies() {
		// Serialization only
	}

	public RemoveMavenDependencies(String packageId, ArrayList<MavenCoordinates> dependencies) {
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
