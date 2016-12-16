package org.gcube.rest.index.client;

import java.util.HashMap;
import java.util.Map;

import org.gcube.rest.index.client.cache.CacheConfig;
import org.gcube.rest.index.client.cache.IndexClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class ClientFactory {

	private static final Logger logger = LoggerFactory.getLogger(ClientFactory.class);
	
	private static Map<String, IndexClient> indexClients = new HashMap<String, IndexClient>();
	
	public static IndexClient getMeAnIndexClient(String scope){
		IndexClient indexClient = indexClients.get(scope);
		if(indexClient == null){
			AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
			ctx.register(CacheConfig.class);
			ctx.refresh();
			indexClient = ctx.getBean(IndexClient.class);
			indexClient.initiateClient(scope);
			ctx.close();
			indexClients.put(scope, indexClient);
		}
		return indexClients.get(scope);
	}
	
	
	public static void closeClient(String scope){
		indexClients.get(scope).getEndpointProvider().stopForCurrentScope();
	}
	
	public static void closeAllClients(){
		
		//java 7
		for(String key : indexClients.keySet()){
			try{
				indexClients.get(key).getEndpointProvider().stopForCurrentScope();
			}
			catch(Exception ex){
				logger.warn("Could not close client for scope "+key +". However, that's not a problem.");
			}
		}
		
		//java 8
//		indexClients.keySet().forEach((key)->{
//			try{
//				indexClients.get(key).getEndpointProvider().stopForCurrentScope();
//			}
//			catch(Exception ex){
//				logger.warn("Could not close client for scope "+key +". However, that's not a problem.");
//			}
//		});
		if(!indexClients.keySet().isEmpty())
			indexClients.get(indexClients.keySet().toArray(new String[0])[0]).getEndpointProvider().terminate();
	}
	
	
}