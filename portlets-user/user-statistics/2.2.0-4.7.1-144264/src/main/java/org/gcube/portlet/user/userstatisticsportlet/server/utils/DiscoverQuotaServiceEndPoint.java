package org.gcube.portlet.user.userstatisticsportlet.server.utils;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;


/**
 * Discover the base path of the QuotaServiceEndPoint
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class DiscoverQuotaServiceEndPoint {

	private static final Log logger = LogFactoryUtil.getLog(DiscoverQuotaServiceEndPoint.class);

	// info to retrieve it
	private static final String SERVICE_TYPE = "Quota";
	private static final String SERVICE_NAME = "Persistence";
	private static final String SERVICE_PROPERTY = "urlService";

	public static String discover(){

		// this service endpoint needs to be discovered into the root
		String basePath = null;
		String currentContext = ScopeProvider.instance.get();
		try{

			PortalContext pc = PortalContext.getConfiguration();
			String rootScope = "/" + pc.getInfrastructureName();
			ScopeProvider.instance.set(rootScope);

			// perform the query
			logger.info("Looking up " + SERVICE_TYPE + " service in context " + rootScope);
			List<ServiceEndpoint> resources = getConfigurationFromIS();

			if(resources == null || resources.isEmpty())
				throw new RuntimeException("Missing service endpoint for " + SERVICE_TYPE);
			else{

				Iterator<AccessPoint> accessPointIterator = resources.get(0).profile().accessPoints().iterator();
				while (accessPointIterator.hasNext()) {
					ServiceEndpoint.AccessPoint accessPoint = (ServiceEndpoint.AccessPoint) accessPointIterator
							.next();

					Map<String, Property> propertiesMap = accessPoint.propertyMap();
					basePath = (String)propertiesMap.get(SERVICE_PROPERTY).value();

				}

			}
			basePath = basePath == null ? basePath :  basePath + "gcube/service/quotaStatus/detail";
			logger.info("Base path value is " + basePath);
		}catch(Exception e){
			logger.error("The following errore arose while looking up service endpoint for " + SERVICE_TYPE, e);
		}finally{
			ScopeProvider.instance.set(currentContext);
		}

		return basePath;
	}

	/**
	 * Retrieve endpoints information from IS
	 * @return the service endpoint
	 * @throws Exception
	 */
	private static List<ServiceEndpoint> getConfigurationFromIS() throws Exception{

		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name/text() eq '"+ SERVICE_NAME +"'");
		query.addCondition("$resource/Profile/Category/text() eq '"+ SERVICE_TYPE +"'");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> toReturn = client.submit(query);
		return toReturn;

	}
}
