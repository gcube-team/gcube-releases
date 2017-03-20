package gr.cite.bluebridge.analytics.endpoint;

import static org.gcube.resources.discovery.icclient.ICFactory.*;

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

import gr.cite.bluebridge.analytics.endpoint.exceptions.ServiceDiscoveryException;

public class ServiceDiscovery {
	
	private static Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

	public static Set<String> fetchServiceEndpoint(String scope, ServiceProfile serviceProfile) throws Exception {
		ScopeProvider.instance.set(scope);			

		Set<String> endpoints = discoverServiceEndpoints(serviceProfile);
		if (endpoints.isEmpty()){
			throw new ServiceDiscoveryException("Did not manage to discover any " 
					+ serviceProfile.getServiceClass() + "/" + serviceProfile.getServiceName() 
					+ " endpoint for scope " + scope);
		}	

		logger.debug("Managed to discover " + endpoints.size() + " " + serviceProfile.getServiceName()   + " endpoint(s)");
		
		return endpoints;
	}

	public static Set<String> discoverServiceEndpoints(ServiceProfile serviceProfile) {
		SimpleQuery query = queryFor(GCoreEndpoint.class);

		query.addCondition("$resource/Profile/ServiceClass/text() eq '" + serviceProfile.getServiceClass() + "'")
				.addCondition("$resource/Profile/ServiceName/text() eq '" + serviceProfile.getServiceName() + "'");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);

		List<GCoreEndpoint> eprs = client.submit(query);

		Set<String> clusterHosts = new HashSet<String>();
		
		if(eprs != null){		
			for (GCoreEndpoint epr : eprs) {
				if (!"ready".equals(epr.profile().deploymentData().status().toLowerCase())){
					continue;
				}
				
				for (Endpoint e : epr.profile().endpointMap().values().toArray(new Endpoint[epr.profile().endpointMap().values().size()])){						
					String endpoint = e.uri().toString();
					
					if(serviceProfile.hasPathContains() && !endpoint.contains(serviceProfile.getPathContains())){
						continue;
					}					
					
					if(serviceProfile.hasPathEndsWith() && !endpoint.endsWith(serviceProfile.getPathEndsWith())){
						continue;					
					}
					
					if(serviceProfile.hasPathNotEndsWith() && endpoint.endsWith(serviceProfile.getPathNotEndsWith())){
						continue;						
					}
					
					if(!endpoint.endsWith("/")){
						endpoint += "/";
					}
					
					clusterHosts.add(endpoint);
					
					logger.debug("Found Service Endpoint: " + endpoint);
				}
			}
		}
		
		return clusterHosts;
	}	
}
