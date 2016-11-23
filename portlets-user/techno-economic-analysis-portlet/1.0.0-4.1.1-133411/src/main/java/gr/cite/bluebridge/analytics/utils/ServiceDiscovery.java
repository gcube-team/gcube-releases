package gr.cite.bluebridge.analytics.utils;

import static org.gcube.resources.discovery.icclient.ICFactory.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import gr.cite.bluebridge.analytics.portlet.SpringPortal;

public class ServiceDiscovery {

	private static Log logger = LogFactoryUtil.getLog(SpringPortal.class);

	public static String fetchServiceEndpoint(String scope, ServiceProfile serviceProfile) throws Exception {
		List<String> endpoints = discoverServiceEndpoints(scope, serviceProfile);
		if (endpoints.isEmpty())
			throw new Exception("Did not manage to discover any " + serviceProfile.getServiceClass() + "/" + serviceProfile.getServiceName() + " endpoint");

		String endpoint = endpoints.get(0);// TODO random or sequential
		
		if (!endpoint.endsWith("/"))
			endpoint = endpoint + "/";

		logger.info("Managed to discover " + serviceProfile.getServiceName() + " endpoint " + endpoint);

		return endpoint;
	}

	public static List<String> discoverServiceEndpoints(String scope, ServiceProfile serviceProfile) {

		ScopeProvider.instance.set(scope);

		SimpleQuery query = queryFor(GCoreEndpoint.class);

		query.addCondition("$resource/Profile/ServiceClass/text() eq '" + serviceProfile.getServiceClass() + "'")
				.addCondition("$resource/Profile/ServiceName/text() eq '" + serviceProfile.getServiceName() + "'");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);

		List<GCoreEndpoint> eprs = client.submit(query);
		Set<String> clusterHosts = new HashSet<String>();
		
		for (GCoreEndpoint epr : eprs) {
			if (!"ready".equals(epr.profile().deploymentData().status().toLowerCase())){
				continue;
			}

			for (Endpoint e : epr.profile().endpointMap().values().toArray(new Endpoint[epr.profile().endpointMap().values().size()])){				
				if(serviceProfile.getPathContains() != null){
					if(!e.uri().toString().contains(serviceProfile.getPathContains())){
						continue;
					}
				}				
				
				if(serviceProfile.getPathEndsWith() != null){
					if (e.uri().toString().endsWith(serviceProfile.getPathEndsWith())){
						clusterHosts.add(e.uri().toString());
					}
				}else{
					if (!e.uri().toString().endsWith(serviceProfile.getPathNotEndsWith())){
						clusterHosts.add(e.uri().toString());
					}
				}
			}
		}

		return new ArrayList<String>(clusterHosts);
	}
}
