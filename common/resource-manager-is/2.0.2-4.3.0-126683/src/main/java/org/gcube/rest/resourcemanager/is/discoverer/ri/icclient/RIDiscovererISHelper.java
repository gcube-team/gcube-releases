package org.gcube.rest.resourcemanager.is.discoverer.ri.icclient;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RIDiscovererISHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(RIDiscovererISHelper.class);

	public static Set<String> discoverRunningInstances(String serviceName, String serviceClass, String endpointKey, String scope) {
		logger.info("Discovering : serviceName " + serviceName + " serviceClass, " + serviceClass + " scope : " + scope);

		Set<String> endpoints = new HashSet<String>();

		ScopeProvider.instance.set(scope);

		SimpleQuery query = queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceClass/text() eq '" + serviceClass + "'")
				.addCondition("$resource/Profile/ServiceName/text() eq '" + serviceName + "'");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
		List<GCoreEndpoint> resources = client.submit(query);

		for (GCoreEndpoint se : resources) {
			
			if (se != null && se.profile() != null && se.profile().endpointMap() != null){
				String status = se.profile().deploymentData().status();
				
				if (!status.equalsIgnoreCase("ready")){
					logger.info("running instance : " + se.id() + " is NOT ready");
					continue;
				}
				
				Endpoint endpoint = se.profile().endpointMap().get(endpointKey);
				if (endpoint != null && endpoint.uri() != null){
					endpoints.add(endpoint.uri().toString());
				}
			}
			
		}
		
		logger.info("endpoints found in discovering : " + endpoints);
		
		return endpoints;
	}
	
}
