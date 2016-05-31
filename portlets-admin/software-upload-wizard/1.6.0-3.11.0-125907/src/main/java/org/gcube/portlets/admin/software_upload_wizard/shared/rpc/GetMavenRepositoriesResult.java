package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import java.util.ArrayList;

import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenRepositoryInfo;

import net.customware.gwt.dispatch.shared.Result;

public class GetMavenRepositoriesResult implements Result {
	
	private ArrayList<MavenRepositoryInfo> repos;
	
	@SuppressWarnings("unused")
	private GetMavenRepositoriesResult() {
		// Serialization only
	}
	
	public GetMavenRepositoriesResult(ArrayList<MavenRepositoryInfo> repos) {
		super();
		this.repos = repos;
	}

	public ArrayList<MavenRepositoryInfo> getRepos() {
		return repos;
	}

}
