package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.is;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.IMavenRepositoryInfo;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class MavenRepositoryISClientTest {

	IMavenRepositoryIS mavenRepositoryIS = new MavenRepositoryISClient();

	@BeforeClass
	public static void setUp() throws Exception {
		ScopeProvider.instance.set("/gcube/devsec");
	}

	@Test
	public void testGetMavenRepository() {
		IMavenRepositoryInfo mavenRepositoryInfo = mavenRepositoryIS
				.getMavenRepository(IMavenRepositoryIS.EXTERNALS_REPO_ID);
		Assert.assertNotNull(mavenRepositoryInfo);
		System.out.println(mavenRepositoryInfo);

		mavenRepositoryInfo = mavenRepositoryIS.getMavenRepository(IMavenRepositoryIS.SNAPSHOTS_REPO_ID);
		Assert.assertNotNull(mavenRepositoryInfo);
		System.out.println(mavenRepositoryInfo);
	}

	@Test
	public void testGetNexusRepository() {
		IMavenRepositoryInfo mavenRepositoryInfo = mavenRepositoryIS
				.getNexusRepository(IMavenRepositoryIS.EXTERNALS_REPO_ID);
		Assert.assertNotNull(mavenRepositoryInfo);
		System.out.println(mavenRepositoryInfo);

		mavenRepositoryInfo = mavenRepositoryIS.getNexusRepository(IMavenRepositoryIS.SNAPSHOTS_REPO_ID);
		Assert.assertNotNull(mavenRepositoryInfo);
		System.out.println(mavenRepositoryInfo);
	}

}
