package org.gcube.application.framework.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//import org.apache.axis.message.addressing.EndpointReference;
//import org.apache.axis.types.URI.MalformedURIException;
//import org.gcube.application.framework.core.cache.RIsManager;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GCoreEndpoint.Profile.Endpoint;
import org.gcube.common.resources.gcore.ServiceInstance;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.gcube.resources.discovery.icclient.ICFactory.*;

public class ServiceUtils {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(ServiceUtils.class);
	
	/**
	 * 
	 * @param srvClass The service class
	 * @param srvName The service name
	 * @param srvType The service type (SIMPLE, FACTORY or STATEFULL)
	 * @param session
	 * @return	the epr Address
	 */
//	public static String getEprAddressOfServiceOLD(String srvClass, String srvName, String srvType, ASLSession session) {
//		EndpointReference[] eprs;
//		try {
//			
//			eprs = RIsManager.getInstance().getISCache(session.getScope()).getEPRsFor(srvClass, srvName,srvType);
//			
//			Random rand = new Random();
//			int random = rand.nextInt(eprs.length);
//			
//			return eprs[random].getAddress().toString();
//		} catch (Exception e) {
//			logger.error("Exception:", e);
//		}
//		return null;
//	}

	/**
	 * 
	 * @param srvClass The service class
	 * @param srvName The service name
	 * @param srvType The service type (SIMPLE, FACTORY or STATEFULL)
	 * @param session
	 * @return	the epr Address
	 */
	
	/*synchronized ?*/ 
	public static String getEprAddressOfService(String srvClass, String srvName, String srvType, ASLSession session){
		return getEprAddressOfService(srvClass, srvName, srvType, session.getScope());
	}
	
	
	/*synchronized ?*/ 
	public static String getEprAddressOfService(String srvClass, String srvName, String srvType, String scope){
		ScopeProvider.instance.set(scope);
		ArrayList <String> endpoints = new ArrayList<String>();
		if (srvType == ServiceType.STATEFULL.name()) {
			logger.debug("Performing query for stateful services with serviceClass="+srvClass+" serviceName="+srvName+" serviceType="+srvType);
			DiscoveryClient<ServiceInstance> client = clientFor(ServiceInstance.class);
			SimpleQuery query = queryFor(ServiceInstance.class);
			query.addCondition("$resource/Profile/ServiceClass/text() eq '"+srvClass+"'")
			 	 .addCondition("$resource/Profile/ServiceName/text() eq '"+srvName+"'");
			List<ServiceInstance> endpointsList = client.submit(query);
//			logger.debug("Found " + endpointsList.size() + " endpoints.");
			for (ServiceInstance endpoint : endpointsList) // normally it's only one endpoint per instance
				endpoints.add(endpoint.endpoint().toString()); //maybe here we should use the endpoint.endpoint().toASCIIString()
		}
		else{ // srvType = SIMPLE or FACTORY 
			logger.debug("Performing SIMPLE or FACTORY query with serviceClass="+srvClass+" serviceName="+srvName+" serviceType="+srvType);
			DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);
			SimpleQuery query = queryFor(GCoreEndpoint.class);
			query.addCondition("$resource/Profile/ServiceClass/text() eq '"+srvClass+"'")
			 	 .addCondition("$resource/Profile/ServiceName/text() eq '"+srvName+"'");
			List <GCoreEndpoint> endpointsList = client.submit(query);
//			logger.debug("Found " + endpointsList.size() + " endpoints.");
			if (srvType == ServiceType.FACTORY.name()){ //if it's FACTORY, add only URIs which contain 'Factory'
				for (GCoreEndpoint endpoint : endpointsList) // normally it's only one endpoint per instance
					for (Endpoint ep : endpoint.profile().endpoints())
						if(ep.uri().toString().indexOf("Factory") != -1)
							endpoints.add(ep.uri().toString()); //maybe here we should use the ep.uri().toASCIIString()
			}
			else{ //it's SIMPLE, so add all URIs
				for (GCoreEndpoint endpoint : endpointsList) // normally it's only one endpoint per instance
					for (Endpoint ep : endpoint.profile().endpoints())
						endpoints.add(ep.uri().toString()); //maybe here we should use the ep.uri().toASCIIString()
			}
//			logger.debug("Number of Endpoint URIs: "+endpoints.size());
		}
		//now return just one from all, at random 
		Random rand = new Random();
		int random = rand.nextInt(endpoints.size());
		logger.debug("Returning Endpoint URI: "+endpoints.get(random));
		return endpoints.get(random);
	}

}
