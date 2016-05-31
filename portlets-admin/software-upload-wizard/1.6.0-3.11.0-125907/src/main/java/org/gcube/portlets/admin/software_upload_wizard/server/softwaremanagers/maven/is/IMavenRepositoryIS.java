package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.is;

import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.IMavenRepositoryInfo;

public interface IMavenRepositoryIS {
	
	public static final String EXTERNALS_REPO_ID = "gcube-externals";
	public static final String RELEASES_REPO_ID = "gcube-releases";
	public static final String SNAPSHOTS_REPO_ID = "gcube-snapshots";

	IMavenRepositoryInfo getMavenRepository(String id);
	
	IMavenRepositoryInfo getNexusRepository(String id);
	
}
