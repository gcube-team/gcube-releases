/**
 *
 */
package org.gcube.data_catalogue.grsf_publish_ws;

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
 * Retrieve the GRSF Service endpoint in the Infrastructure.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class GcoreEndpointGRSFService {

	private static final String resource = "jersey-servlet";
	private static final String serviceName = "GRSFPublisher";
	private static final String serviceClass = "Data-Catalogue";

	private static Logger logger = LoggerFactory.getLogger(GcoreEndpointGRSFService.class);

	/**
	 * Instantiates a new gcore endpoint reader.
	 * @param scope the scope
	 * @throws Exception the exception
	 */
	public static String getServiceUrl(String scope) throws Exception {

		if(scope == null || scope.isEmpty())
			throw new IllegalArgumentException();

		String oldScope = ScopeProvider.instance.get();

		try{

			logger.info("set scope "+scope);
			ScopeProvider.instance.set(scope);

			SimpleQuery query = queryFor(GCoreEndpoint.class);
			query.addCondition(String.format("$resource/Profile/ServiceClass/text() eq '%s'",serviceClass));
			query.addCondition("$resource/Profile/DeploymentData/Status/text() eq 'ready'");
			query.addCondition(String.format("$resource/Profile/ServiceName/text() eq '%s'",serviceName));
			query.setResult("$resource/Profile/AccessPoint/RunningInstanceInterfaces//Endpoint[@EntryName/string() eq \""+resource+"\"]/text()");

			logger.debug("submitting quey "+query.toString());

			DiscoveryClient<String> client = client();
			List<String> endpoints = client.submit(query);
			if (endpoints == null || endpoints.isEmpty()) throw new Exception("Cannot retrieve the GCoreEndpoint serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+scope);

			String urlFound = endpoints.get(0);
			if(urlFound==null)
				throw new Exception("Endpoint:"+resource+", is null for serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+scope);

			logger.info("found entyname "+urlFound+" for ckanResource: "+resource);

			urlFound = urlFound.replaceFirst(":80", "").replace("http", "https");
			return urlFound;

		}catch(Exception e){
			String error = "An error occurred during GCoreEndpoint discovery, serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+scope +".";
			logger.error(error, e);
			throw new Exception(error);
		}finally{
			if(oldScope != null && !oldScope.equals(scope))
				ScopeProvider.instance.set(oldScope);
		}
	}
}
