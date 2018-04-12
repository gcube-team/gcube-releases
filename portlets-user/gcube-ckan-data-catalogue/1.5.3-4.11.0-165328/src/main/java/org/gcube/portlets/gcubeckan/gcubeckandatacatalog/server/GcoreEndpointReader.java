/**
 *
 */
package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.server;

import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.Serializable;
import java.util.List;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 10, 2016
 */
public class GcoreEndpointReader implements Serializable{

	private static final long serialVersionUID = 7631710353375893823L;
	private static final String ckanResource = "org.gcube.data.access.ckanconnector.CkanConnector";
	private static final String serviceName = "CkanConnector";
	private static final String serviceClass = "DataAccess";
	private static final Log logger = LogFactoryUtil.getLog(GcoreEndpointReader.class);
	private String ckanResourceEntyName;

	/**
	 * Instantiates a new gcore endpoint reader.
	 *
	 * @param scope the scope
	 * @throws Exception the exception
	 */
	public GcoreEndpointReader(String scope) throws Exception {

		String currentScope = ScopeProvider.instance.get();
		try{

			logger.info("set scope "+scope);
			ScopeProvider.instance.set(scope);

			SimpleQuery query = queryFor(GCoreEndpoint.class);
			query.addCondition(String.format("$resource/Profile/ServiceClass/text() eq '%s'",serviceClass));
			query.addCondition("$resource/Profile/DeploymentData/Status/text() eq 'ready'");
			query.addCondition(String.format("$resource/Profile/ServiceName/text() eq '%s'",serviceName));
			query.addCondition(String.format("$resource/Scopes/Scope/text()[.='%s']", scope)); // i.e. check the resource contains among the scopes this one
			query.setResult("$resource/Profile/AccessPoint/RunningInstanceInterfaces//Endpoint[@EntryName/string() eq \""+ckanResource+"\"]/text()");

			logger.debug("submitting quey "+query.toString());

			DiscoveryClient<String> client = client();
			List<String> endpoints = client.submit(query);
			if (endpoints == null || endpoints.isEmpty()) 
				throw new Exception("Cannot retrieve the GCoreEndpoint serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+scope);

			logger.debug("Found " + endpoints.size() + " matching resources");

			this.ckanResourceEntyName = endpoints.get(0);
			if(ckanResourceEntyName==null)
				throw new Exception("Endpoint:"+ckanResource+", is null for serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+scope);

			logger.info("found entyname "+ckanResourceEntyName+" for ckanResource: "+ckanResource);

		}catch(Exception e){
			String error = "An error occurred during GCoreEndpoint discovery, serviceName: "+serviceName +", serviceClass: " +serviceClass +", in scope: "+scope +".";
			logger.error(error, e);
			throw new Exception(error);
		}finally{
			logger.info("scope provider reset");
			ScopeProvider.instance.set(currentScope);
		}
	}

	/**
	 * @return the ckanResourceEntyName
	 */
	public String getCkanResourceEntyName() {

		return ckanResourceEntyName;
	}
	
}
