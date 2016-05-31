package org.gcube.portlets.admin.software_upload_wizard.server.softwaremanagers.maven.is;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.IMavenRepositoryInfo;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.maven.MavenRepositoryInfo;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import com.google.inject.Singleton;

@Singleton
public class MavenRepositoryISClient implements IMavenRepositoryIS {

	private static final String RUNTIME_RESOURCE_NAME = "MavenRepository";

	private static final String REPOSITORY_ID_PROPERTY_NAME = "repositoryId";

	private static final String REPOSITORY_URL_ATTR_VALUE = "RepositoryUrl";
	private static final String REPOSITORY_NEXUSBROWSEURL_ATTR_VALUE = "NexusUrl";

	@Override
	public IMavenRepositoryInfo getMavenRepository(String id) {

		ServiceEndpoint serviceEndpoint = getMavenRepositoryServiceEndpoint(id);

		AccessPoint accessPoint = null;
		for (AccessPoint tmp : serviceEndpoint.profile().accessPoints())
			if (tmp.name().equals(REPOSITORY_URL_ATTR_VALUE))
				accessPoint = tmp;

		if (accessPoint == null)
			throw new RuntimeException(
					"Cannot find a valid access point within the retrieved Maven repository resource.");

		return new MavenRepositoryInfo(id, accessPoint.address());
	}

	@Override
	public IMavenRepositoryInfo getNexusRepository(String id) {

		ServiceEndpoint serviceEndpoint = getMavenRepositoryServiceEndpoint(id);

		AccessPoint accessPoint = null;
		for (AccessPoint tmp : serviceEndpoint.profile().accessPoints())
			if (tmp.name().equals(REPOSITORY_NEXUSBROWSEURL_ATTR_VALUE))
				accessPoint = tmp;

		if (accessPoint == null)
			throw new RuntimeException(
					"Cannot find a valid access point within the retrieved Maven repository resource.");

		return new MavenRepositoryInfo(id, accessPoint.address());
	}

	private ServiceEndpoint getMavenRepositoryServiceEndpoint(String id) {
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition(String.format("$resource/Profile/Name/text() eq '%s'", RUNTIME_RESOURCE_NAME));
		query.addCondition(String.format("$resource/Profile/AccessPoint/Properties/Property[Name='%s' and Value='%s']",
				REPOSITORY_ID_PROPERTY_NAME, id));

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> resources = client.submit(query);

		if (resources.size() != 1)
			throw new RuntimeException("Invalid number of Maven Repositories found with id " + id);

		return resources.get(0);
	}

}
