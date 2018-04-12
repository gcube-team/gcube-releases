/**
 *
 */
package org.gcube.datacatalogue.catalogue.utils;

import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Discover the Social Networking Service in the Infrastructure.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class GcoreEndpointReaderSNL {

	private static final String RESOURCE = "jersey-servlet";
	private static final String SERVICE_NAME = "SocialNetworking";
	private static final String SERVICE_CLASSE = "Portal";

	private static Logger logger = LoggerFactory.getLogger(GcoreEndpointReaderSNL.class);
	private String serviceBasePath;

	/**
	 * Discover the gcore endpoint for the social networking service.
	 * @param scope the scope
	 * @throws Exception the exception
	 */
	public GcoreEndpointReaderSNL() throws Exception {

		String currentScope = ScopeProvider.instance.get();

		try{
			SimpleQuery query = queryFor(GCoreEndpoint.class);
			query.addCondition(String.format("$resource/Profile/ServiceClass/text() eq '%s'",SERVICE_CLASSE));
			query.addCondition("$resource/Profile/DeploymentData/Status/text() eq 'ready'");
			query.addCondition(String.format("$resource/Profile/ServiceName/text() eq '%s'",SERVICE_NAME));
			query.setResult("$resource/Profile/AccessPoint/RunningInstanceInterfaces//Endpoint[@EntryName/string() eq \""+RESOURCE+"\"]/text()");

			DiscoveryClient<String> client = client();
			List<String> endpoints = client.submit(query);
			if (endpoints == null || endpoints.isEmpty()) 
				throw new Exception("Cannot retrieve the GCoreEndpoint SERVICE_NAME: "+SERVICE_NAME +", SERVICE_CLASSE: " +SERVICE_CLASSE +", in scope: "+currentScope);

			this.serviceBasePath = endpoints.get(0);
			
			if(serviceBasePath==null)
				throw new Exception("Endpoint:"+RESOURCE+", is null for SERVICE_NAME: "+SERVICE_NAME +", SERVICE_CLASSE: " +SERVICE_CLASSE +", in scope: "+currentScope);

			serviceBasePath = serviceBasePath.endsWith("/") ? serviceBasePath : serviceBasePath + "/";
			
		}catch(Exception e){
			String error = "An error occurred during GCoreEndpoint discovery, SERVICE_NAME: "+SERVICE_NAME +", SERVICE_CLASSE: " +SERVICE_CLASSE +", in scope: "+currentScope +".";
			logger.error(error, e);
			throw new Exception(error);
		}
	}

	/**
	 * @return the base path of the service
	 */
	public String getServiceBasePath() {
		return serviceBasePath;
	}
}
