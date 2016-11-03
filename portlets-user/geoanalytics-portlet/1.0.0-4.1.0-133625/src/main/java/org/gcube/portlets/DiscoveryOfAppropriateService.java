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

public class DiscoveryOfAppropriateService {
	public static String discoverServiceNodes(String scope) {

		  ScopeProvider.instance.set(scope);

		  SimpleQuery query = queryFor(GCoreEndpoint.class);

		  String serviceName = "geoanalytics-main-service";
		  String serviceClass = "geoanalytics";

		  query.addCondition("$resource/Profile/ServiceClass/text() eq '" + serviceClass + "'")
		    .addCondition("$resource/Profile/ServiceName/text() eq '" + serviceName + "'");

		  DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);

		  List<GCoreEndpoint> eprs = client.submit(query);
		  
		  Set<String> clusterHosts = new HashSet<String>();
		  for (GCoreEndpoint epr : eprs) {
		   for (Endpoint e : epr.profile().endpointMap().values().toArray(new Endpoint[epr.profile().endpointMap().values().size()]))
		    if (e.uri().toString().endsWith("/"))
		     clusterHosts.add(e.uri().toString());
		  }
		  
		  String clusterHost="";
		  for(String host: clusterHosts){
			  clusterHost = host;
			}

		  return clusterHost;
		 }

}
