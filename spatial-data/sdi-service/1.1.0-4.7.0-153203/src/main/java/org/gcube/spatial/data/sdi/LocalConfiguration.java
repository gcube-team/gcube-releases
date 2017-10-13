package org.gcube.spatial.data.sdi;

import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletContext;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalConfiguration {

	
	final static public String GEONETWORK_CACHE_TTL="gn.cache.TTL";
	final static public String GEONETWORK_SE_CATEGORY="gn.se.category";
	final static public String GEONETWORK_SE_PLATFORM="gn.se.platform";
	final static public String GEONETWORK_SE_PRIORITY="gn.se.priority";
	final static public String GEONETWORK_SE_ENDPOINT_NAME="gn.se.endpointName";
	
	final static public String GEONETWORK_GE_SERVICE_CLASS="gn.ge.serviceClass";
	final static public String GEONETWORK_GE_SERVICE_NAME="gn.ge.serviceName";
	
	final static public String GEOSERVER_CACHE_TTL="gs.cache.TTL";
	final static public String GEOSERVER_GE_SERVICE_CLASS="gs.ge.serviceClass";
	final static public String GEOSERVER_GE_SERVICE_NAME="gs.ge.serviceName";
	final static public String GEOSERVER_SE_CATEGORY="gs.se.category";
	final static public String GEOSERVER_SE_PLATFORM="gs.se.platform";
	final static public String GEOSERVER_SE_ENDPOINT_NAME="gs.se.endpointName";
	
	
	final static public String THREDDS_CACHE_TTL="th.cache.TTL";
	final static public String THREDDS_SE_CATEGORY="th.se.category";
	final static public String THREDDS_SE_PLATFORM="th.se.platform";
	final static public String THREDDS_GE_SERVICE_CLASS="th.ge.serviceClass";
	final static public String THREDDS_GE_SERVICE_NAME="th.ge.serviceName";
	final static public String THREDDS_SE_ENDPOINT_NAME="th.se.endpointName";
	
	final static public String METADATA_TEMPLATE_FOLDER="meta.tpl.folder";
	
	final static public String TEMPORARY_PERSISTENCE_TTL="temp.ttl";

	final static public String IS_REGISTRATION_TIMEOUT="is.registration.timeout";
	
	static LocalConfiguration instance=null;
	
	
	public static synchronized LocalConfiguration get(){		
		return instance;
	}
	
	@Synchronized
	public static LocalConfiguration init(URL propertiesURL){
		if(instance==null)
			instance=new LocalConfiguration(propertiesURL);
		return instance; 
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
	
	
	private Object templateConfiguration=null;
	
	public Object getTemplateConfigurationObject() {return templateConfiguration;}
	public void setTemplateConfigurationObject(Object obj) {this.templateConfiguration=obj;}
}
