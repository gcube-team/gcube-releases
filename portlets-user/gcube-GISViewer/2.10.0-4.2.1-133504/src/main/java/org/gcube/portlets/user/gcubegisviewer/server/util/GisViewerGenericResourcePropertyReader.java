package org.gcube.portlets.user.gcubegisviewer.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class GisViewerGenericResourcePropertyReader {

	protected static final String GENERIC_RESOURCE_GCUBE_APPS_PROPERTIES = "GisViewerResourceGcubeApps.properties";
	protected static final String GIS_VIEWER_BASE_LAYER_SECONDARY_TYPE = "GIS_VIEWER_BASE_LAYER_SECONDARY_TYPE";
	protected static final String APP_ID = "APP_ID";

	private String appId;
	private String baseLayersSecondaryType;

	public static Logger logger = Logger
			.getLogger(GisViewerGenericResourcePropertyReader.class);

	public GisViewerGenericResourcePropertyReader()
			throws PropertyFileNotFoundException {

		Properties prop = new Properties();

		try {

			InputStream in = (InputStream) GisViewerGenericResourcePropertyReader.class
					.getResourceAsStream(GENERIC_RESOURCE_GCUBE_APPS_PROPERTIES);

			// load a properties file
			prop.load(in);

			// get the property value - the application Id
			this.appId = prop.getProperty(APP_ID);

			this.baseLayersSecondaryType = prop
					.getProperty(GIS_VIEWER_BASE_LAYER_SECONDARY_TYPE);

		} catch (IOException e) {
			logger.error("An error occurred on read property file " + e, e);
			throw new PropertyFileNotFoundException(
					"An error occurred on read property file " + e);
		}
	}

	public String getAppId() {
		return appId;
	}

	public String getBaseLayersSecondaryType() {
		return baseLayersSecondaryType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GisViewerGenericResourcePropertyReader [appId=");
		builder.append(appId);
		builder.append(", baseLayersSecondaryType=");
		builder.append(baseLayersSecondaryType);
		builder.append("]");
		return builder.toString();
	}

	public static void main(String[] args) {
		GisViewerGenericResourcePropertyReader gr;
		try {
			gr = new GisViewerGenericResourcePropertyReader();
			System.out.println(gr);
		} catch (PropertyFileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
