package org.gcube.content.storage.rest.utils;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.ClientInfo;
import org.gcube.content.storage.rest.bean.Credentials;
import org.gcube.mongodb.driver.MongoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Roberto Cirillo (ISTI-CNR) 2017
 *
 */
public class Utils {
	
	private static final Logger logger= LoggerFactory.getLogger(Utils.class);
	
	public static boolean isValid(String token){
		logger.trace("token is always valid");
		return true;
	}
	
	public static String getTokenId(String token){
		ClientInfo info=AuthorizationProvider.instance.get().getClient();
		String id=info.getId();
		logger.debug("id "+id);
		return id;
	}

	public static String getTokenType(String token){
		ClientInfo info=AuthorizationProvider.instance.get().getClient();
		String name=info.getType().name();
		logger.debug("name:  "+name);
		return name;
	}

	public static void printCredentials(Credentials credentials) {
		logger.info("first server : "+credentials.getServers().get(0)+"db: "+credentials.getDb()+"\tcollection: "+credentials.getCollection()+"\tuser: "+credentials.getUser());
	}

	public static void printConfiguration(MongoConfiguration configuration) {
		if (configuration == null)
			throw new RuntimeException("Configuration object not istantiated correctly. Configuration null ");
		logger.info("db: "+configuration.getDb()+" "+configuration.getPwd()+" "+configuration.getUser()+" ");
		if(configuration.getServers()== null){
			throw new RuntimeException("Configuration object not istantiated correctly. Server null ");
		}else
			logger.info("server: "+configuration.getServers().get(0));
	}
	
	public static boolean notNullNotEmpty(String field){
		if ((field!= null) && (field.length() > 0))
			return true;
		throw new RuntimeException("Found a mandatory field with value empy or null");
	}
}
