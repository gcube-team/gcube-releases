package org.gcube.spatial.data.gis;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Configuration {

	private static final Long DEFAULT_TTL=60000l;
	
	private static Configuration singleton=null;
	
	
	public static synchronized Configuration get() throws IOException{
		if(singleton==null) singleton=new Configuration();
		return singleton;
	}
	
	
	public static final String GEOSERVER_HOSTED_LAYERS_TTL="geoserver.cache.hostedLayers.TTL";
	public static final String GEOSERVER_STYLES_TTL="geoserver.cache.hostedLayers.TTL";
	public static final String GEOSERVER_WORKSPACE_TTL="geoserver.cache.hostedLayers.TTL";
	public static final String GEOSERVER_DATASTORE_TTL="geoserver.cache.hostedLayers.TTL";
	public static final String IS_CACHE_TTL="geoserver.cache.hostedLayers.TTL";
	public static final String IS_SERVICE_PROFILE_CATEGORY="is.serviceProfile.category";
	public static final String IS_SERVICE_PROFILE_PLATFORM_NAME="is.serviceProfile.platform.name";
	public static final String IS_ACCESS_POLICY="is.accessPolicy";
	
	
	
	HashMap<String,String> properties;
	Properties props;
	
	private Configuration() throws IOException {
		props=new Properties();
			try {
				props.load(Configuration.class.getClassLoader().getResourceAsStream("gis-interface.properties"));				
			} catch (IOException e) {
				log.warn("****************** Unable to load properties file ****************** ",e);
				throw e;
			}			
	}
	
	
	public String getProperty(String propertyName){
		return props.getProperty(propertyName);
	}
	
	public static Long getTTL(String TTLParameter) {
		try{
			return Long.parseLong(Configuration.get().getProperty(TTLParameter));
		}catch(Exception e){
			log.warn("Unable to get TTL "+TTLParameter,e);
			return DEFAULT_TTL;
		}
	}
}
