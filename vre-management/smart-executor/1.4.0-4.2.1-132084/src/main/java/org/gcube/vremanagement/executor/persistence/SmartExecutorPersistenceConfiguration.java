/**
 * 
 */
package org.gcube.vremanagement.executor.persistence;

import java.net.URI;
import java.security.Key;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.resources.gcore.utils.Group;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.gcube.vremanagement.executor.SmartExecutorInitializator;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class SmartExecutorPersistenceConfiguration {
	
	public final String SERVICE_ENDPOINT_CATEGORY = "VREManagement";
	//public final String SERVICE_ENDPOINT_NAME = "SmartExecutor";
	public final String SERVICE_ENDPOINT_NAME = "SmartExecutorPersistenceConfiguration";
	
	protected static final String PERSISTENCE_CLASS_NAME = "persistenceClassName";
	protected static final String TARGET_SCOPE = "targetScope";
	
	protected URI uri;
	protected String username;
	protected String password;
	
	protected Map<String, Property> propertyMap;
	
	protected void init(){
		this.propertyMap = new HashMap<String, Property>();
	}
	
	public SmartExecutorPersistenceConfiguration(){
		init();
	}
	
	public SmartExecutorPersistenceConfiguration(URI uri, String username, String password){
		init();
		this.uri = uri;
		this.username = username;
		this.password = password;
	}
	
	public SmartExecutorPersistenceConfiguration(String persistenceClassName) throws Exception {
		init();
		ServiceEndpoint serviceEndpoint = getServiceEndpoint(SERVICE_ENDPOINT_CATEGORY, SERVICE_ENDPOINT_NAME, persistenceClassName);
		setValues(serviceEndpoint,persistenceClassName);
	}
	
	/**
	 * @return the uri
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * @param uri the uri to set
	 */
	public void setUri(URI uri) {
		this.uri = uri;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the propertyMap
	 * @throws Exception 
	 */
	public String getProperty(String propertyKey) throws Exception {
		Property propertyValue = propertyMap.get(propertyKey);
		String value = propertyValue.value();
		if(propertyValue.isEncrypted()){
			value = decrypt(value);
		}
		return value;
	}
	
	protected ServiceEndpoint getServiceEndpoint(String serviceEndpointCategory, String serviceEndpointName, String persistenceClassName){
		SimpleQuery query = ICFactory.queryFor(ServiceEndpoint.class);
		query.addCondition(String.format("$resource/Profile/Category/text() eq '%s'", serviceEndpointCategory));
		query.addCondition(String.format("$resource/Profile/Name/text() eq '%s'", serviceEndpointName));
		query.addCondition(String.format("$resource/Profile/AccessPoint/Properties/Property/Name/text() eq '%s'", PERSISTENCE_CLASS_NAME));
		query.addCondition(String.format("$resource/Profile/AccessPoint/Properties/Property/Value/text() eq '%s'", persistenceClassName));
		query.setResult("$resource");
		
		DiscoveryClient<ServiceEndpoint> client = ICFactory.clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> serviceEndpoints = client.submit(query);
		if(serviceEndpoints.size()>1){
			query.addCondition(String.format("$resource/Profile/AccessPoint/Properties/Property/Name/text() eq '%s'", TARGET_SCOPE));
			query.addCondition(String.format("$resource/Profile/AccessPoint/Properties/Property/Value/text() eq '%s'", SmartExecutorInitializator.getScopeFromToken()));
			serviceEndpoints = client.submit(query);
		}
		return serviceEndpoints.get(0);
	}
	
	private static String decrypt(String encrypted, Key... key) throws Exception {
		return StringEncrypter.getEncrypter().decrypt(encrypted);
	}
	
	protected void setValues(ServiceEndpoint serviceEndpoint, String persistenceClassName) throws Exception{
		Group<AccessPoint> accessPoints = serviceEndpoint.profile().accessPoints();
		for(AccessPoint accessPoint : accessPoints){
			Collection<Property> properties = accessPoint.propertyMap().values();
			
			if(properties.contains(new ServiceEndpoint.Property().nameAndValue(PERSISTENCE_CLASS_NAME, persistenceClassName))){
				this.uri = new URI(accessPoint.address());
				this.username = accessPoint.username();
				
				String encryptedPassword = accessPoint.password();
				String password = decrypt(encryptedPassword);
				
				this.password = password;
				this.propertyMap = accessPoint.propertyMap();
			}
		}
	}
		
}
