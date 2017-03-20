package org.gcube.data_catalogue.grsf_publish_ws.utils;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.Iterator;
import java.util.List;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Retrieves the base url of the social-networking service in the scope provided
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class ServiceEndPointReaderSocial {

	private String basePath = null;

	private static Logger logger = LoggerFactory.getLogger(ServiceEndPointReaderSocial.class);
	private final static String RUNTIME_RESOURCE_NAME = "SocialNetworking";
	private final static String CATEGORY = "Portal";

	public ServiceEndPointReaderSocial(String context){

		if(context == null || context.isEmpty())
			throw new IllegalArgumentException("A valid context is needed to discover the service");


		String oldContext = ScopeProvider.instance.get();
		ScopeProvider.instance.set(context);

		try{

			List<ServiceEndpoint> resources = getConfigurationFromIS();
			if (resources.size() == 0){
				logger.error("There is no Runtime Resource having name " + RUNTIME_RESOURCE_NAME +" and Category " + CATEGORY + " in this scope.");
				throw new Exception("There is no Runtime Resource having name " + RUNTIME_RESOURCE_NAME +" and Category " + CATEGORY + " in this scope.");
			}
			else {

				for (ServiceEndpoint res : resources) {

					Iterator<AccessPoint> accessPointIterator = res.profile().accessPoints().iterator();

					while (accessPointIterator.hasNext()) {
						ServiceEndpoint.AccessPoint accessPoint = (ServiceEndpoint.AccessPoint) accessPointIterator
								.next();

						// get base path
						basePath = accessPoint.address();

						// break
						break;
					}
				}

			}

		}catch(Exception e){

			logger.error("Unable to retrieve such service endpoint information!", e);

		}finally{

			if(oldContext != null && !oldContext.equals(context))
				ScopeProvider.instance.set(oldContext);

		}

		logger.info("Found base path " + basePath + " for the service");

	}

	/**
	 * Retrieve endpoints information from IS for the Service endpoint
	 * @return list of endpoints
	 * @throws Exception
	 */
	private List<ServiceEndpoint> getConfigurationFromIS() throws Exception{

		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name/text() eq '"+ RUNTIME_RESOURCE_NAME +"'");
		query.addCondition("$resource/Profile/Category/text() eq '"+ CATEGORY +"'");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> toReturn = client.submit(query);
		return toReturn;

	}

	/**
	 * Get the base path of the social networking service
	 * @return
	 */
	public String getBasePath() {
		return basePath;
	}
}
