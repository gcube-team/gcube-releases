package gr.cite.geoanalytics.context;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Configuration
{
	private static Logger log = LoggerFactory.getLogger(Configuration.class);
	
	private DataLayerConfig dataLayerConfig = null;
//	private GeoServerBridgeConfig geoServerBridgeConfig = null;
	private ApplicationConfig applicationConfig = null;
	private SmtpConfig smtpConfig = null;
	
	public static final String GEOSPATIAL_OPERATION_SERVICE = "GeospatialOperationService";
	public static final String GEOSERVER_ENDPOINT_REQUEST = "geoserverEndpoint";
	
	@Inject
	public void setDataLayerConfig(DataLayerConfig dataLayerConfig) {
		log.trace("Setting data layer configuration...");
		this.dataLayerConfig = dataLayerConfig;
		log.trace("Data layer configuration has been set");
	}
	
	public DataLayerConfig getDataLayerConfig() {
		return dataLayerConfig;
	}
	
//	@Inject
//	public void setGeoServerBridgeConfig(GeoServerBridgeConfig geoServerBridgeConfig) {
//		log.trace("Setting geoserverbridge configuration...");
//		this.geoServerBridgeConfig = geoServerBridgeConfig;
//		log.trace("Geoserverbridge configuration has been set");
//	}
//	
//	public GeoServerBridgeConfig getGeoServerBridgeConfig() {
//		return geoServerBridgeConfig;
//	}
	
	@Inject
	public void setApplicationConfig(ApplicationConfig applicationConfig) {
		log.trace("Setting application config: " + applicationConfig);
		this.applicationConfig = applicationConfig;
		log.trace("Setting application config has been set ");
	}
	
	public ApplicationConfig getApplicationConfig() {
		return applicationConfig;
	}

	public SmtpConfig getSmtpConfig() {
		return smtpConfig;
	}

	@Inject
	public void setSmtpConfig(SmtpConfig smtpConfig) {
		log.debug("Setting SmtpConfig...");
		this.smtpConfig = smtpConfig;
		log.debug("SmtpConfig has been set");
	}
	
	public static String getFullGosEndpoint(String gosHost, String gosPort){
		return "http://"+gosHost+":"+gosPort+"/"+Configuration.GEOSPATIAL_OPERATION_SERVICE;
	}
	
//	public static final String getDaoImplementationClassName(Class<?> entityType)
//	{
//		if(!init) initialize();
//		return daoImpls.get(entityType.getName());
//	}
}
