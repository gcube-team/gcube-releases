package org.gcube.spatial.data.sdi;

import java.net.URL;
import java.util.Properties;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalConfiguration {

	//GN
	final static public String GEONETWORK_CACHE_TTL="gn.cache.TTL";
	final static public String GEONETWORK_SE_CATEGORY="gn.se.category";
	final static public String GEONETWORK_SE_PLATFORM="gn.se.platform";
	final static public String GEONETWORK_SE_PRIORITY="gn.se.priority";
	final static public String GEONETWORK_SE_ENDPOINT_NAME="gn.se.endpointName";
	
	final static public String GEONETWORK_GE_SERVICE_CLASS="gn.ge.serviceClass";
	final static public String GEONETWORK_GE_SERVICE_NAME="gn.ge.serviceName";
	final static public String GEONETWORK_UPDATE_TIMEOUT="gn.update.timeout";
	final static public String GEONETWORK_UPDATE_WAIT="gn.update.wait";
	final static public String GEONETWORK_MAIL="gn.contact.mail";
	final static public String GEONETWORK_PASSWORD_LENGTH="gn.password.length";
	
	final static public String GEONETWORK_SE_SUFFIXES="gn.se.suffixes";	
	final static public String GEONETWORK_SE_ASSIGNED_SCOPE_PREFIX="gn.se.assigned.scope.prefix";
	final static public String GEONETWORK_SE_SCOPE_USER_PREFIX="gn.se.scope.user.prefix";
	final static public String GEONETWORK_SE_SCOPE_PASSWORD_PREFIX="gn.se.scope.password.prefix";
	final static public String GEONETWORK_SE_CKAN_USER_PREFIX="gn.se.ckan.user.prefix";
	final static public String GEONETWORK_SE_CKAN_PASSWORD_PREFIX="gn.se.ckan.password.prefix";
	final static public String GEONETWORK_SE_MANAGER_USER_PREFIX="gn.se.manager.user.prefix";
	final static public String GEONETWORK_SE_MANAGER_PASSWORD_PREFIX="gn.se.manager.password.prefix";
	final static public String GEONETWORK_SE_DEFAULT_GROUP_PREFIX="gn.se.default.group.prefix";
	final static public String GEONETWORK_SE_SHARED_GROUP_PREFIX="gn.se.shared.group.prefix";
	final static public String GEONETWORK_SE_CONFIDENTIAL_GROUP_PREFIX="gn.se.confidential.group.prefix";
	final static public String GEONETWORK_SE_CONTEXT_GROUP_PREFIX="gn.se.context.group.prefix";
	final static public String GEONETWORK_GROUP_ALL="gn.groups.all";
	
	
	final static public String GEONETWORK_MANDATORY_SG="gn.mandatorySG";
	
	
	//GS
	final static public String GEOSERVER_CACHE_TTL="gs.cache.TTL";
	final static public String GEOSERVER_GE_SERVICE_CLASS="gs.ge.serviceClass";
	final static public String GEOSERVER_GE_SERVICE_NAME="gs.ge.serviceName";
	final static public String GEOSERVER_SE_CATEGORY="gs.se.category";
	final static public String GEOSERVER_SE_PLATFORM="gs.se.platform";
	final static public String GEOSERVER_SE_ENDPOINT_NAME="gs.se.endpointName";
	
	public static final String GEOSERVER_HOSTED_LAYERS_TTL="gs.cache.hostedLayers.TTL";
	public static final String GEOSERVER_STYLES_TTL="gs.cache.hostedLayers.TTL";
	public static final String GEOSERVER_WORKSPACE_TTL="gs.cache.hostedLayers.TTL";
	public static final String GEOSERVER_DATASTORE_TTL="gs.cache.hostedLayers.TTL";
	
	final static public String GEOSERVER_MANDATORY_SG="gs.mandatorySG";
	
	
	//TH
	final static public String THREDDS_CACHE_TTL="th.cache.TTL";
	final static public String THREDDS_SE_CATEGORY="th.se.category";
	final static public String THREDDS_SE_PLATFORM="th.se.platform";
	final static public String THREDDS_GE_SERVICE_CLASS="th.ge.serviceClass";
	final static public String THREDDS_GE_SERVICE_NAME="th.ge.serviceName";
	final static public String THREDDS_SE_ENDPOINT_NAME="th.se.endpointName";
	
	final static public String THREDDS_MANDATORY_SG="th.mandatorySG";
	final static public String THREDDS_SE_REMOTE_MANAGEMENT_ACCESS="th.se.remoteManagement.access";
	
	//META
	final static public String TEMPLATE_FOLDER="tpl.folder";
	
	final static public String TEMPORARY_PERSISTENCE_TTL="temp.ttl";

	final static public String IS_REGISTRATION_TIMEOUT="is.registration.timeout";
	
	static LocalConfiguration instance=null;
	
	
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
	
	public static String getProperty(String property){
		return instance.props.getProperty(property);
	}
	
	public static String getProperty(String property,String defaultValue){
		return instance.props.getProperty(property, defaultValue);
	}
	
	public static Long getTTL(String property) {
		return Long.parseLong(getProperty(property));
	}
	
	public static boolean getFlag(String property) {
		return Boolean.parseBoolean(property);
	}
	
	private static Object templateConfiguration=null;
	
	public static Object getTemplateConfigurationObject() {return templateConfiguration;}
	public static void setTemplateConfigurationObject(Object obj) {templateConfiguration=obj;}
}
