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
	private GeoServerBridgeConfig geoServerBridgeConfig = null;
	private ApplicationConfig applicationConfig = null;
	private SmtpConfig smtpConfig = null;
	
	public static final String GEOSPATIAL_OPERATION_SERVICE = "GeospatialOperationService";
	public static final String GEOSERVER_ENDPOINT_REQUEST = "geoserverEndpoint";
	
	@Inject
	public void setDataLayerConfig(DataLayerConfig dataLayerConfig) {
		this.dataLayerConfig = dataLayerConfig;
	}
	
	public DataLayerConfig getDataLayerConfig() {
		return dataLayerConfig;
	}
	
	@Inject
	public void setGeoServerBridgeConfig(GeoServerBridgeConfig geoServerBridgeConfig) {
		this.geoServerBridgeConfig = geoServerBridgeConfig;
	}
	
	public GeoServerBridgeConfig getGeoServerBridgeConfig() {
		return geoServerBridgeConfig;
	}
	
	@Inject
	public void setApplicationConfig(ApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}
	
	public ApplicationConfig getApplicationConfig() {
		return applicationConfig;
	}

	public SmtpConfig getSmtpConfig() {
		return smtpConfig;
	}

	@Inject
	public void setSmtpConfig(SmtpConfig smtpConfig) {
		this.smtpConfig = smtpConfig;
	}
	
//	public static final String getDaoImplementationClassName(Class<?> entityType)
//	{
//		if(!init) initialize();
//		return daoImpls.get(entityType.getName());
//	}
}
