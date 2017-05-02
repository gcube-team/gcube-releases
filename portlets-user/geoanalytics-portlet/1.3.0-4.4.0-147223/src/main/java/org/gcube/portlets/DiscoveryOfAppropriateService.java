package org.gcube.portlets;

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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class DiscoveryOfAppropriateService{
	private static Log _log = LogFactoryUtil.getLog(DiscoveryOfAppropriateService.class);

	public static String discoverServiceNodes(String scope) throws Exception {
		_log.info("Attempting to discover service under the scope: " + scope);

		ScopeProvider.instance.set(scope);

		SimpleQuery query = queryFor(GCoreEndpoint.class);

		String serviceName = "geoanalytics-main-service";
		String serviceClass = "geoanalytics";

		query.addCondition("$resource/Profile/ServiceClass/text() eq '" + serviceClass + "'")
		.addCondition("$resource/Profile/ServiceName/text() eq '" + serviceName + "'");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);

		List<GCoreEndpoint> eprs = client.submit(query);

		Set<String> clusterHosts = new HashSet<String>();
		if(eprs != null){
			for (GCoreEndpoint epr : eprs) {
				if (!"ready".equals(epr.profile().deploymentData().status().toLowerCase())){
					continue;
				}

				for (Endpoint e : epr.profile().endpointMap().values().toArray(new Endpoint[epr.profile().endpointMap().values().size()]))
					if (e.uri().toString().endsWith("/"))
						clusterHosts.add(e.uri().toString());
			}
		}

		String clusterHost="";
		for(String host: clusterHosts){
			clusterHost = host;
		}
		
		if(clusterHosts.size() > 0){
			_log.info("********************************* Discovered more than one endpoints ****************************");
		}
		
		if(!clusterHost.equals("")){
			_log.info("Service: " + clusterHost +" was discovered successfully");
		}else{
			_log.info("Failed to discover service: " + serviceName + " under the scope: " + scope);
			throw new Exception("Failed to discover service: " + serviceName + " under the scope: "  + scope);
		}

		return clusterHost;
	}
}
