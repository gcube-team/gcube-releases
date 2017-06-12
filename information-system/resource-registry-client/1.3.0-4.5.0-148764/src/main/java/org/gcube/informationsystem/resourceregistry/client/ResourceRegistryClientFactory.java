package org.gcube.informationsystem.resourceregistry.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.resourceregistry.api.Constants;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ResourceRegistryClientFactory {
	
	protected static Map<String, ResourceRegistryClient> clients;

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
	
	private static SimpleQuery getQuery(){
		return ICFactory.queryFor(GCoreEndpoint.class)
				.addCondition(String.format(classFormat, Constants.SERVICE_CLASS))
				.addCondition(String.format(nameFormat, Constants.SERVICE_NAME))
				.addCondition(String.format(statusFormat))
				.addVariable("$entry","$resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint")
				.addCondition(String.format(containsFormat, Constants.SERVICE_ENTRY_NAME))
				.setResult("$entry/text()");
	}
	
	public static ResourceRegistryClient create(){
		if(FORCED_URL!=null){
			return new ResourceRegistryClientImpl(FORCED_URL);
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
		
		ResourceRegistryClient client = clients.get(key);
		
		if(client==null){
			SimpleQuery query = getQuery();
			List<String> addresses = ICFactory.client().submit(query);
			
			if(addresses==null || addresses.isEmpty()){
				String error = String.format("No %s:%s found in the current context", Constants.SERVICE_CLASS, Constants.SERVICE_NAME);
				throw new RuntimeException(error);
			}
			
			Random random = new Random();
			int index = random.nextInt(addresses.size());
	        
			client = new ResourceRegistryClientImpl(addresses.get(index));
			
		}
		
		return client;
	}
	
}
