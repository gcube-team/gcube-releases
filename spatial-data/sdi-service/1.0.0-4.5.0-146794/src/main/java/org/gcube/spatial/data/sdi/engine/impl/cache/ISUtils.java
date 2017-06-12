package org.gcube.spatial.data.sdi.engine.impl.cache;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ISUtils {

	static List<ServiceEndpoint> queryForServiceEndpoints(String category, String platformName){
		log.debug("Querying for Service Endpoints [category : {} , platformName : {}, currentScope : {} ]",category,platformName,ScopeUtils.getCurrentScope());
				
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq '"+category+"'")
		.addCondition("$resource/Profile/Platform/Name/text() eq '"+platformName+"'");				
		//		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		return client.submit(query);
	}
	
	static List<GCoreEndpoint> queryForGCoreEndpoint(String serviceClass,String serviceName){
		log.debug("Querying for GCore Endpoints [ServiceClass : {} , ServiceName : {}, currentScope : {} ]",serviceClass,serviceName,ScopeUtils.getCurrentScope());
		
		
		SimpleQuery query =queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceClass/text() eq '"+serviceClass+"'")
		.addCondition("$resource/Profile/ServiceName/text() eq '"+serviceName+"'");				
		//		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);

		return client.submit(query);
	}
}
