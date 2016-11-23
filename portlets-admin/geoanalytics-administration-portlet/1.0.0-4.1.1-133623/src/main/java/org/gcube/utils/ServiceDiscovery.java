package org.gcube.utils;

import static org.gcube.resources.discovery.icclient.ICFactory.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.GeoanalyticsAdministrationHome;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ServiceDiscovery {

	private static Log logger = LogFactoryUtil.getLog(GeoanalyticsAdministrationHome.class);

	public static final String SERVICE_CLASS = "geoanalytics";
	public static final String SERVICE_NAME = "geoanalytics-main-service";

	public static String fetchServiceEndpoint(String scope) throws Exception {
		List<String> endpoints = discoverServiceEndpoints(scope);
		if (endpoints.isEmpty())
			throw new Exception("Did not manage to discover any " + SERVICE_CLASS + "/" + SERVICE_NAME + " endpoint");

		String endpoint = endpoints.get(0);// TODO random or sequential

		if (!endpoint.endsWith("/"))
			endpoint = endpoint + "/";

		logger.info("Managed to find geoanalytics-main-service endpoint " + endpoint);

		return endpoint;
	}

	public static List<String> discoverServiceEndpoints(String scope) {

		ScopeProvider.instance.set(scope);

		SimpleQuery query = queryFor(GCoreEndpoint.class);

		query.addCondition("$resource/Profile/ServiceClass/text() eq '" + ServiceDiscovery.SERVICE_CLASS + "'")
				.addCondition("$resource/Profile/ServiceName/text() eq '" + ServiceDiscovery.SERVICE_NAME + "'");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);

		List<GCoreEndpoint> eprs = client.submit(query);

		Set<String> clusterHosts = new HashSet<String>();
		for (GCoreEndpoint epr : eprs) {
			if (!"ready".equals(epr.profile().deploymentData().status().toLowerCase()))
				continue;
			for (Endpoint e : epr.profile().endpointMap().values().toArray(new Endpoint[epr.profile().endpointMap().values().size()]))
				if (e.uri().toString().endsWith("/geoanalytics"))
					clusterHosts.add(e.uri().toString());
		}
		
		return new ArrayList<String>(clusterHosts);
	}

}
