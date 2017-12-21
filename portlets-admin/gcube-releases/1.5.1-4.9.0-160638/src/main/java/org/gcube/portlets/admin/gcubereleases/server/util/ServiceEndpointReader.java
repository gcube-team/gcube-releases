/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.server.util;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ServiceEndpointReader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 20, 2015
 */
public class ServiceEndpointReader {
	
	public static final Logger logger = LoggerFactory.getLogger(ServiceEndpointReader.class);
	
	public ServerParameters serverParameters;

	private String resourceName;

	private String scope;


	/**
	 * Instantiates a new service endpoint reader.
	 *
	 * @param scope the scope
	 * @param resourceName the resource name
	 */
	public ServiceEndpointReader(String scope, String resourceName) {
		this.scope = scope;
		this.resourceName = resourceName;
	}
	
	/**
	 * Read resource.
	 *
	 * @param decrypt the decrypt
	 * @return the server parameters
	 * @throws Exception the exception
	 */
	public ServerParameters readResource(boolean decrypt) throws Exception {
		
		if(this.scope==null)
			throw new Exception("scope is null");
		
		if(this.resourceName==null)
			throw new Exception("resourceName is null");
		
		try{
			logger.info("Trying to read Service Endpoint with name: "+resourceName+", scope: "+scope);
			
			ScopeProvider.instance.set(this.scope);
			logger.info("scope provider set instance: "+this.scope);
			
			SimpleQuery query = queryFor(ServiceEndpoint.class);
			query.addCondition("$resource/Profile/Name/string() eq '"+resourceName+"'");
			
			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

			List<ServiceEndpoint> r = client.submit(query);
			if (r == null || r.isEmpty()) throw new Exception("Cannot retrieve the runtime resource: "+resourceName);
	
			ServiceEndpoint se = r.get(0); //the first
			if(se.profile()==null)
				throw new Exception("IS profile is null for resource: "+resourceName);
			
			Group<AccessPoint> accessPoints = se.profile().accessPoints();
			if(accessPoints.size()==0) throw new Exception("Accesspoint in resource "+resourceName+" not found");
			
			serverParameters = new ServerParameters();
			
			AccessPoint ap = accessPoints.iterator().next();
			serverParameters.setUrl(ap.address());
			serverParameters.setUser(ap.username()); //username
			
			String decryptedPassword = "";
			
			if(decrypt)
				decryptedPassword = StringEncrypter.getEncrypter().decrypt(ap.password());
			else
				decryptedPassword = ap.password();
			 
			serverParameters.setPassword(decryptedPassword); //password
	
			Group<Property> properties = ap.properties();
		
			if(properties.size()==0){
				logger.warn("Properties in resource "+resourceName+" not found! returning");
				return serverParameters;
			}
		
			Iterator<Property> iter = properties.iterator();
			
			Map<String, String> mapProperties = new HashMap<String, String>();
			while (iter.hasNext()) {
				Property prop = iter.next();
				mapProperties.put(prop.name(), prop.value());
			}
			
			serverParameters.setProperties(mapProperties);
			
			return serverParameters;
		
		}catch (Exception e) {
			String error = "Sorry, an error occurred on reading parameters in Runtime Reosurces, resource name: "+resourceName+" scope: "+scope;
			logger.error(error,e);
			throw new Exception(error);
		}
			
	}
	
	/**
	 * Gets the resource name.
	 *
	 * @return the resource name
	 */
	public String getResourceName() {
		return resourceName;
	}
	
	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}
	
	/**
	 * Gets the server parameters.
	 *
	 * @return the server parameters
	 */
	public ServerParameters getServerParameters() {
		return serverParameters;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServiceEndpointReader [serverParameters=");
		builder.append(serverParameters);
		builder.append(", resourceName=");
		builder.append(resourceName);
		builder.append(", scope=");
		builder.append(scope);
		builder.append("]");
		return builder.toString();
	}
	
	
	public static void main(String[] args) {
		ServiceEndpointReader reader = new ServiceEndpointReader("/gcube", "GcubeReleasesDB");
		
		try {
			System.out.println(reader.readResource(true));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
