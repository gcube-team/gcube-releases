package gr.cite.bluebridge.analytics.endpoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import gr.cite.bluebridge.analytics.endpoint.DatabaseCredentials;
import gr.cite.bluebridge.analytics.endpoint.DatabaseDiscovery;
import gr.cite.bluebridge.analytics.endpoint.ServiceDiscovery;
import gr.cite.bluebridge.analytics.endpoint.ServiceProfile;

public class EndpointManager {
	
	private static Map<String, Map<DatabaseProfile, DatabaseCredentials>> databaseEndpoints = new HashMap<>();
	private static Map<String, Map<ServiceProfile, Set<String>>> serviceEndpoints = new HashMap<>();
	
	private static Object databaseLock = new Object();	
	private static Object  serviceLock = new Object();

	private static Logger logger = LoggerFactory.getLogger(EndpointManager.class);
	
	@Scheduled(fixedDelayString = "#{${cache.refresh.interval} * 60 * 1000}")
	public void refreshEndpoints(){	
		
		synchronized(serviceLock){			
			for(String scope : serviceEndpoints.keySet()){	
				logger.info("Refreshing " + scope + " service endpoints");
				for(ServiceProfile serviceProfile : serviceEndpoints.get(scope).keySet()){
					try {
						fetchServiceEndpoints(scope, serviceProfile);
					} catch (Exception e) {
						logger.error("Cannot service refresh endpoints: " + e);
					}
				}
			}			
		}
		
//		synchronized(databaseLock){
//			for(String scope : databaseEndpoints.keySet()){
//				logger.info("Refreshing " + scope + " database endpoints");
//				for(DatabaseProfile databaseProfile : databaseEndpoints.get(scope).keySet()){
//					try {
//						fetchhDatabaseEndpoints(scope, databaseProfile);
//					} catch (Exception e) {
//						logger.error("Cannot database refresh endpoints: " + e);
//					}
//				}
//			}
//		}
	}	
	
	private void fetchhDatabaseEndpoints(String scope, DatabaseProfile databaseProfile) throws Exception{	
		logger.debug("Fetching  database endpoints for scope " + scope);
		DatabaseCredentials databaseCredentials = DatabaseDiscovery.fetchDatabaseCredentials(scope, databaseProfile);	
		databaseEndpoints.get(scope).put(databaseProfile, databaseCredentials);
	}
	
	private void fetchServiceEndpoints(String scope, ServiceProfile serviceProfile) throws Exception{
		logger.debug("Fetching service endpoints for scope " + scope);
		Set<String> endpoints = ServiceDiscovery.fetchServiceEndpoint(scope, serviceProfile);
		serviceEndpoints.get(scope).put(serviceProfile, endpoints);
	}		
	
	public DatabaseCredentials getDatabaseEndpoint(String scope, DatabaseProfile databaseProfile) throws Exception{
		DatabaseCredentials asynchronizedDatabaseCredentials;
		
		synchronized(databaseLock){
			boolean scopeNotExists	  = !databaseEndpoints.containsKey(scope);
			boolean databaseNotExists = scopeNotExists   ? true : !databaseEndpoints.get(scope).containsKey(databaseProfile);
			
			if(scopeNotExists){
				databaseEndpoints.put(scope, new HashMap<DatabaseProfile , DatabaseCredentials>());
			}

			if(databaseNotExists){
				fetchhDatabaseEndpoints(scope, databaseProfile);
			}

			DatabaseCredentials databaseCredentials = databaseEndpoints.get(scope).get(databaseProfile);
			asynchronizedDatabaseCredentials = databaseCredentials.clone();
		}
		
		return asynchronizedDatabaseCredentials;
	}	
	
	public List<String> getServiceEndpoints(String scope, ServiceProfile serviceProfile) throws Exception{
		List<String> asynchronizedEndpoints ;
		
		synchronized(serviceLock){
			boolean scopeNotExists	 = !serviceEndpoints.containsKey(scope);
			boolean serviceNotExists = scopeNotExists   ? true : !serviceEndpoints.get(scope).containsKey(serviceProfile);
			boolean hasNotEndpoints  = serviceNotExists ? true : serviceEndpoints.get(scope).get(serviceProfile).size() == 0;

			if(scopeNotExists){
				serviceEndpoints.put(scope, new HashMap<ServiceProfile, Set<String>>());
			}
			
			if(serviceNotExists || hasNotEndpoints){				
				fetchServiceEndpoints(scope, serviceProfile);
			}
			
			Set<String> endpoints = serviceEndpoints.get(scope).get(serviceProfile);
			asynchronizedEndpoints = new ArrayList<String>(endpoints);
		}
		
 		Collections.shuffle(asynchronizedEndpoints);
 		
 		logger.info("Returned " + asynchronizedEndpoints.size() + " service endpoint(s)");
 		
		return asynchronizedEndpoints;		
	}
	
	public void removeServiceEndpoint(String scope, ServiceProfile serviceProfile, String endpoint){
		synchronized(serviceLock){
			if(serviceEndpoints.get(scope).get(serviceProfile).contains(endpoint)){
				logger.debug("Removing endpoint " + endpoint + " from cache.");
				serviceEndpoints.get(scope).get(serviceProfile).remove(endpoint);	
			}
		}
	}	
}