/**
 *
 */
package org.gcube.portlets.widgets.wstaskexecutor.server.util;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.List;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class RuntimeResourceReader.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Sep 13, 2019
 */
public class RuntimeResourceReader {

	public static final Logger logger = LoggerFactory.getLogger(RuntimeResourceReader.class);

	/**
	 * Gets the parameters.
	 *
	 * @param scope the scope
	 * @param resourceName the resource name
	 * @param resourceCategory the resource category
	 * @return the parameters
	 * @throws Exception the exception
	 */
	public static ServerParameters getParameters(String scope, String resourceName, String resourceCategory) throws Exception
	{

		ServerParameters parameters = new ServerParameters();
		String originalScope = null;
		try{
			
			ServiceEndpoint se = getServiceEndpoint(scope, resourceName, resourceCategory);
			if(se.profile()==null)
				throw new Exception("IS profile is null for resource: "+resourceName);

			Group<AccessPoint> accessPoints = se.profile().accessPoints();
			if(accessPoints.size()==0) throw new Exception("Accesspoint in resource "+resourceName+" not found");

			AccessPoint ap = accessPoints.iterator().next();
			parameters.setUrl(ap.address());
			parameters.setUser(ap.username()); //username

			String decryptedPassword = StringEncrypter.getEncrypter().decrypt(ap.password());
			parameters.setPassword(decryptedPassword); //password


		}catch (Exception e) {
			logger.error("Sorry, an error occurred on reading parameters in Runtime Resources",e);
		}finally{
			if(originalScope!=null && !originalScope.isEmpty()){
				ScopeProvider.instance.set(originalScope);
				logger.info("scope provider setted to orginal scope: "+originalScope);
			}else{
				ScopeProvider.instance.reset();
				logger.info("scope provider reset");
			}
		}

		return parameters;
	}
	
	
	/**
	 * Service endpoint exists.
	 *
	 * @param scope the scope
	 * @param resourceName the resource name
	 * @param resourceCategory the resource category
	 * @return true, if successful
	 */
	public static boolean serviceEndpointExists(String scope, String resourceName, String resourceCategory) {
		
		ServiceEndpoint sEp = getServiceEndpoint(scope, resourceName, resourceCategory);
		return sEp!=null;
		
	}
	
	
	/**
	 * Gets the service endpoint.
	 *
	 * @param scope the scope
	 * @param resourceName the resource name
	 * @param resourceCategory the resource category
	 * @return the service endpoint found. Otherwise null.
	 */
	public static ServiceEndpoint getServiceEndpoint(String scope, String resourceName, String resourceCategory) {
		
		String originalScope = null;
		List<ServiceEndpoint> listEndPoints = null;
		try{

			originalScope = ScopeProvider.instance.get();
			logger.info("Setting scope: {}", scope);
			ScopeProvider.instance.set(scope);

			SimpleQuery query = queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Name/string() eq '"+resourceName+"'");
			
			if(resourceCategory!=null && !resourceCategory.isEmpty()) {
				query.addCondition("$resource/Profile/Category/string() eq '"+resourceCategory+"'");
			}
			
			logger.info("Searching the RR with Profile/Name '{}' and Profile/Category '{}'", resourceName, resourceCategory);
			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
			listEndPoints = client.submit(query);
		
		}catch (Exception e) {
			logger.error("Unexpeted error occurred on searching the EndPoint: ",e);
			
		}finally{
			if(originalScope!=null && !originalScope.isEmpty()){
				ScopeProvider.instance.set(originalScope);
				logger.info("scope provider setted to orginal scope: "+originalScope);
			}else{
				ScopeProvider.instance.reset();
				logger.info("scope provider reset");
			}
		}
		
		if (listEndPoints == null || listEndPoints.isEmpty()) {
			logger.info("No RR found with Profile/Name '{}' and Profile/Category '{}'"+ " in the scope "+scope , resourceName, resourceCategory);
			return null;
		}

		ServiceEndpoint sEp = listEndPoints.get(0); //Returning the first EndPoint. I'm assuming that only one instance exists for input RR
		logger.info("Found the RR with Profile/Name '{}' and Profile/Category '{}' in the instancied scope", resourceName, resourceCategory);
		return sEp;
	}

}
