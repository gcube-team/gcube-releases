package org.gcube.spatial.data.sdi;

import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalConfiguration {

	
	final static public String GEONETWORK_CACHE_TTL="gn.cache.TTL";
	final static public String GEONETWORK_SE_CATEGORY="gn.se.category";
	final static public String GEONETWORK_SE_PLATFORM="gn.se.platform";
	final static public String GEONETWORK_SE_PRIORITY="gn.se.priority";
	final static public String GEONETWORK_SE_ENDPOINT_NAME="gn.se.endpointName";
	
	
	final static public String GEOSERVER_CACHE_TTL="gs.cache.TTL";
	
	
	final static public String THREDDS_CACHE_TTL="th.cache.TTL";
	final static public String THREDDS_SE_CATEGORY="th.se.category";
	final static public String THREDDS_SE_PLATFORM="th.se.platform";
	final static public String THREDDS_GE_SERVICE_CLASS="th.ge.serviceClass";
	final static public String THREDDS_GE_SERVICE_NAME="th.ge.serviceName";
	
	
	
	static LocalConfiguration instance=null;
	
	
	public static synchronized LocalConfiguration get(){		
		return instance;
	}
	
	public static void init(URL propertiesURL){
		instance=new LocalConfiguration(propertiesURL);
	}
	
	private Properties props=new Properties();
	
	private LocalConfiguration(URL propertiesURL) {
		try{
			log.debug("Loading {} ",propertiesURL);
			props.load(propertiesURL.openStream());
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public String getProperty(String property){
		return props.getProperty(property);
	}
	
	public String getProperty(String property,String defaultValue){
		return props.getProperty(property, defaultValue);
	}
}
