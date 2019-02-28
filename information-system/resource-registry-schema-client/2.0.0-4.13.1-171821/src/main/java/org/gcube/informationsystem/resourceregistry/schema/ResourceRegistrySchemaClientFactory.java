package org.gcube.informationsystem.resourceregistry.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.resourceregistry.api.Constants;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ResourceRegistrySchemaClientFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceRegistrySchemaClientFactory.class);
	
	protected static Map<String, ResourceRegistrySchemaClient> clients;

	static {
		clients = new HashMap<>();
	}
	
	private static String FORCED_URL = null;
	
	protected static void forceToURL(String url){
		FORCED_URL = url;
	}
	
	private static String classFormat = "$resource/Profile/ServiceClass/text() eq '%1s'";
	private static String nameFormat = "$resource/Profile/ServiceName/text() eq '%1s'";
	private static String statusFormat = "$resource/Profile/DeploymentData/Status/text() eq 'ready'";
	private static String containsFormat = "$entry/@EntryName eq '%1s'";
	
	
	private static String serviceEndpointCategoryFormat = "$resource/Profile/Category/text() eq '%1s'";
	private static String serviceEndpointNameFormat = "$resource/Profile/Name/text() eq '%1s'";
	private static String serviceEndpointstatusFormat = "$resource/Profile/RunTime/Status/text() eq 'READY'";

	
	
	private static SimpleQuery queryForService(){
		return ICFactory.queryFor(GCoreEndpoint.class)
				.addCondition(String.format(classFormat, Constants.SERVICE_CLASS))
				.addCondition(String.format(nameFormat, Constants.SERVICE_NAME))
				.addCondition(String.format(statusFormat))
				.addVariable("$entry","$resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint")
				.addCondition(String.format(containsFormat, Constants.SERVICE_ENTRY_NAME))
				.setResult("$entry/text()");
	}
	
	private static SimpleQuery queryForProxy(){
		return ICFactory.queryFor(ServiceEndpoint.class)
				.addCondition(String.format(serviceEndpointCategoryFormat, Constants.SERVICE_CLASS))
				.addCondition(String.format(serviceEndpointNameFormat, Constants.SERVICE_NAME))
				.addCondition(String.format(serviceEndpointstatusFormat))
				.addVariable("$entry","$resource/Profile/AccessPoint/Interface/Endpoint")
				.addCondition(String.format(containsFormat, Constants.SERVICE_ENTRY_NAME))
				.setResult("$entry/text()");
	}
	
	
	protected static List<String> getAddresses(){
		List<String> addresses = new ArrayList<>();
		
		try {
			SimpleQuery proxyQuery = queryForProxy();
			addresses = ICFactory.client().submit(proxyQuery);
			if(addresses==null || addresses.isEmpty()){
				throw new Exception("No ResourceRegistry Proxy Found");
			}
		} catch (Exception e) {
			logger.debug("{}. Looking for RunningInstance.", e.getMessage());
			SimpleQuery serviceQuery = queryForService();
			addresses = ICFactory.client().submit(serviceQuery);
		}
		
		return addresses;
	}
	
	
	public static ResourceRegistrySchemaClient create(){
		if(FORCED_URL!=null){
			return new ResourceRegistrySchemaClientImpl(FORCED_URL);
		}
		
		String key = null;
		if (SecurityTokenProvider.instance.get() == null) {
			if (ScopeProvider.instance.get() == null) {
				throw new RuntimeException(
						"Null Token and Scope. Please set your token first.");
			}
			key = ScopeProvider.instance.get();
		} else {
			key = SecurityTokenProvider.instance.get();
		}
		
		ResourceRegistrySchemaClient client = clients.get(key);
		
		if(client==null){
			
			List<String> addresses = getAddresses();
			
			if(addresses==null || addresses.isEmpty()){
				String error = String.format("No %s:%s found in the current context", Constants.SERVICE_CLASS, Constants.SERVICE_NAME);
				throw new RuntimeException(error);
			}
			
			Random random = new Random();
			int index = random.nextInt(addresses.size());
	        
			client = new ResourceRegistrySchemaClientImpl(addresses.get(index));
			
		}
		
		return client;
	}
	
}
