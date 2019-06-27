package org.gcube.gcat.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.gcat.api.GCatConstants;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class GCatClientDiscovery {
	
	private static final Logger logger = LoggerFactory.getLogger(GCatClientDiscovery.class);
	
	protected static Map<String, URL> clients;

	static {
		clients = new HashMap<>();
	}
	
	protected static void forceToURL(String adddress) throws MalformedURLException {
		if(adddress!=null && adddress.compareTo("")!=0) {
			String context = getContext();
			URL url = new URL(adddress);
			clients.put(context, url);
		}
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
				.addCondition(String.format(classFormat, GCatConstants.SERVICE_CLASS))
				.addCondition(String.format(nameFormat, GCatConstants.SERVICE_NAME))
				.addCondition(String.format(statusFormat))
				.addVariable("$entry","$resource/Profile/AccessPoint/RunningInstanceInterfaces/Endpoint")
				.addCondition(String.format(containsFormat, GCatConstants.SERVICE_ENTRY_NAME))
				.setResult("$entry/text()");
	}
	
	private static SimpleQuery queryForProxy(){
		return ICFactory.queryFor(ServiceEndpoint.class)
				.addCondition(String.format(serviceEndpointCategoryFormat, GCatConstants.SERVICE_CLASS))
				.addCondition(String.format(serviceEndpointNameFormat, GCatConstants.SERVICE_NAME))
				.addCondition(String.format(serviceEndpointstatusFormat))
				.addVariable("$entry","$resource/Profile/AccessPoint/Interface/Endpoint")
				.addCondition(String.format(containsFormat, GCatConstants.SERVICE_ENTRY_NAME))
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
	
	
	protected static String getContext() {
		String context = null;
		if (SecurityTokenProvider.instance.get() == null) {
			if (ScopeProvider.instance.get() == null) {
				throw new RuntimeException(
						"Null Token and Scope. Please set your token first.");
			}
			context = ScopeProvider.instance.get();
		} else {
			context = SecurityTokenProvider.instance.get();
		}
		return context;
	}
	
	public static URL getServiceURL() throws MalformedURLException {
		
		String context = getContext();
		URL url = clients.get(context);
		
		if(url==null){
			
			List<String> addresses = getAddresses();
			
			if(addresses==null || addresses.isEmpty()){
				String error = String.format("No %s:%s found in the current context", GCatConstants.SERVICE_CLASS, GCatConstants.SERVICE_NAME);
				throw new RuntimeException(error);
			}
			Random random = new Random();
			int index = random.nextInt(addresses.size());
	        
			url = new URL(addresses.get(index));
			clients.put(context, url);
		}
		
		return url;
	}
	
}
