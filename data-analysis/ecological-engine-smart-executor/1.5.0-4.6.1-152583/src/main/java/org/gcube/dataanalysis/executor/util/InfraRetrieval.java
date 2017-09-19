package org.gcube.dataanalysis.executor.util;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;

public class InfraRetrieval {

	public static List<String> retrieveAddresses(String Category, String scope, String exclude) {
		if (scope == null || scope.length() == 0)
			return new ArrayList<String>();

//		AnalysisLogger.getLogger().debug("RetrieveAddressesFromInfra->Setting Scope to " + scope+" and executing query");
//		ScopeProvider.instance.set(scope);

		SimpleQuery query = ICFactory.queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq '" + Category + "'").addCondition("$resource/Profile[Name[not(contains(., '" + exclude + "'))]]").setResult("$resource/Profile/AccessPoint/Interface/Endpoint/text()");
		DiscoveryClient<String> client = ICFactory.client();
		List<String> addresses = client.submit(query);
//		AnalysisLogger.getLogger().debug("RetrieveAddressesFromInfra->Query to IS finished");
		return addresses;
	}

	public static List<String> retrieveServiceAddress(String Category, String Name, String scope, String exclude) {
		if (scope == null || scope.length() == 0)
			return new ArrayList<String>();

//		ScopeProvider.instance.set(scope);

		SimpleQuery query = ICFactory.queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/text() eq '" + Category + "'").addCondition("$resource/Profile/Name/text() eq '" + Name+ "'").addCondition("$resource/Profile[Name[not(contains(., '" + exclude + "'))]]").setResult("$resource/Profile/AccessPoint/Interface/Endpoint/text()");
		DiscoveryClient<String> client = ICFactory.client();
		List<String> addresses = client.submit(query);

		return addresses;
	}
	
	public static List<String> retrieveService(String service, String scope) {
		if (scope == null || scope.length() == 0)
			return new ArrayList<String>();

//		ScopeProvider.instance.set(scope);

		SimpleQuery query = ICFactory.queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceName/text() eq '"+service+"'").setResult("$resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint/text()");
		DiscoveryClient<String> client = ICFactory.client();
		List<String> addresses = client.submit(query);

		return addresses;
	}
	
	
	public static String findEmailingSystemAddress(String scope) throws Exception {
		
		String serviceName = "SocialNetworking";
		String serviceClass = "Portal";
		
		List<String> endpoints  = retrieveServiceAddress(serviceClass, serviceName,scope,"test");
		
		
		/* new code - deprecated
			String resource = "jersey-servlet";

			SimpleQuery query = ICFactory.queryFor(GCoreEndpoint.class);
			query.addCondition(String.format("$resource/Profile/ServiceClass/text() eq '%s'",serviceClass));
			query.addCondition("$resource/Profile/DeploymentData/Status/text() eq 'ready'");
			query.addCondition(String.format("$resource/Profile/ServiceName/text() eq '%s'",serviceName));
			query.setResult("$resource/Profile/AccessPoint/RunningInstanceInterfaces//Endpoint[@EntryName/string() eq \""+resource+"\"]/text()");

			DiscoveryClient<String> client = ICFactory.client();
			List<String> endpoints = client.submit(query);
		
			
		
			
			
			*/
		
		String resourceEntyName = endpoints.get(0);
		if (endpoints == null || endpoints.isEmpty()) throw new Exception("Cannot retrieve the GCoreEndpoint serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+scope);
		
		if(resourceEntyName==null)
			throw new Exception("Endpoint:"+serviceName+", is null for serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+scope);
	
		
			return resourceEntyName;
			
	}

	
	
}
