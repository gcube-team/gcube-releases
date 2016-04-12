package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.is;

import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.IMavenRepositoryInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenRepositoryInfo;

import com.google.inject.Singleton;

@Singleton
public class MavenRepositoryInlineIS implements IMavenRepositoryIS {

	private static final String EXTERNALS_REPO_URL = "http://maven.research-infrastructures.eu:8081/nexus/content/repositories/gcube-externals/";
	private static final String RELEASES_REPO_URL = "http://maven.research-infrastructures.eu:8081/nexus/content/repositories/gcube-releases/";
	private static final String SNAPSHOTS_REPO_URL = "http://maven.research-infrastructures.eu:8081/nexus/content/repositories/gcube-snapshots/";
	
	private static final String NEXUS_EXTERNALS_URL = "http://maven.research-infrastructures.eu/nexus/index.html#view-repositories;gcube-externals%7Ebrowsestorage";
	private static final String NEXUS_RELEASES_URL = "http://maven.research-infrastructures.eu/nexus/index.html#view-repositories;gcube-releases%7Ebrowsestorage";
	private static final String NEXUS_SNAPSHOTS_URL = "http://maven.research-infrastructures.eu/nexus/index.html#view-repositories;gcube-snapshots%7Ebrowsestorage";
	
	@Override
	public IMavenRepositoryInfo getMavenRepository(String id) {
		if (id.equals(RELEASES_REPO_ID)) return new MavenRepositoryInfo(RELEASES_REPO_ID, RELEASES_REPO_URL);
		if (id.equals(EXTERNALS_REPO_ID)) return new MavenRepositoryInfo(EXTERNALS_REPO_ID, EXTERNALS_REPO_URL);
		if (id.equals(SNAPSHOTS_REPO_ID)) return new MavenRepositoryInfo(SNAPSHOTS_REPO_ID, SNAPSHOTS_REPO_URL);
		
		throw new RuntimeException("Repository with id " + id + " not found");
	}

	@Override
	public IMavenRepositoryInfo getNexusRepository(String id) {
		if (id.equals(RELEASES_REPO_ID)) return new MavenRepositoryInfo(RELEASES_REPO_ID, NEXUS_RELEASES_URL);
		if (id.equals(EXTERNALS_REPO_ID)) return new MavenRepositoryInfo(EXTERNALS_REPO_ID, NEXUS_EXTERNALS_URL);
		if (id.equals(SNAPSHOTS_REPO_ID)) return new MavenRepositoryInfo(SNAPSHOTS_REPO_ID, NEXUS_SNAPSHOTS_URL);
		
		throw new RuntimeException("Repository with id " + id + " not found");
	}

}
