package org.gcube.portlets.user.gcubegeoexplorer.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class GeoexplorerGenericResourcePropertyReader {

	protected static final String GENERIC_RESOURCE_GCUBE_APPS_PROPERTIES = "genericGeoexplorerResourceGcubeApps.properties";
	protected static final String DEFAULT_LAYERS_SECONDARY_TYPE = "DEFAULT_LAYERS_SECONDARY_TYPE";
	protected static final String METADATA_STYLES_SECONDARY_TYPE = "METADATA_STYLES_SECONDARY_TYPE";
	protected static final String APP_ID = "APP_ID";
	
	private String appId;
	private String defaultLayersSecondaryType;
	private String metadataStylesSecondaryType;

	public static Logger logger = Logger.getLogger(GeoexplorerGenericResourcePropertyReader.class);
	
	public GeoexplorerGenericResourcePropertyReader() throws PropertyFileNotFoundException {

		Properties prop = new Properties();
		
		try {
			
			InputStream in = (InputStream) GeoexplorerGenericResourcePropertyReader.class.getResourceAsStream(GENERIC_RESOURCE_GCUBE_APPS_PROPERTIES);

			// load a properties file
			prop.load(in);

			// get the property value - the application Id
			this.appId = prop.getProperty(APP_ID);

			this.defaultLayersSecondaryType = prop.getProperty(DEFAULT_LAYERS_SECONDARY_TYPE);
			this.metadataStylesSecondaryType = prop.getProperty(METADATA_STYLES_SECONDARY_TYPE);

		} catch (IOException e) {
			logger.error("An error occurred on read property file "+e, e);
			throw new PropertyFileNotFoundException("An error occurred on read property file "+e);
		}
	}
	
	public String getAppId() {
		return appId;
	}

	public String getMetadataStylesSecondaryType() {
		return metadataStylesSecondaryType;
	}
	
	public String getDefaultLayersSecondaryType() {
		return defaultLayersSecondaryType;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeoexplorerGenericResourcePropertyReader [appId=");
		builder.append(appId);
		builder.append(", defaultLayersSecondaryType=");
		builder.append(defaultLayersSecondaryType);
		builder.append(", metadataStylesSecondaryType=");
		builder.append(metadataStylesSecondaryType);
		builder.append("]");
		return builder.toString();
	}
	
	public static void main(String[] args) {
		GeoexplorerGenericResourcePropertyReader gr;
		try {
			gr = new GeoexplorerGenericResourcePropertyReader();
			System.out.println(gr);
		} catch (PropertyFileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
}
