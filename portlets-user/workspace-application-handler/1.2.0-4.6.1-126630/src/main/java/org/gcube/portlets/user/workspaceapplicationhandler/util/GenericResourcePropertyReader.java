package org.gcube.portlets.user.workspaceapplicationhandler.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.workspaceapplicationhandler.exception.PropertyFileNotFoundException;

public class GenericResourcePropertyReader {

	protected static final String GENERIC_RESOURCE_GCUBE_APPS_PROPERTIES = "genericResourceGcubeApps.properties";
	protected static final String SECONDARY_TYPE = "SECONDARY_TYPE";
	protected static final String APP_ID = "APP_ID";
	
	private String appId;
	private String genericResource;

	private Logger logger = Logger.getLogger(GenericResourcePropertyReader.class);
	
	public GenericResourcePropertyReader() throws PropertyFileNotFoundException {

		Properties prop = new Properties();
		
		try {
			
			InputStream in = (InputStream) GenericResourcePropertyReader.class.getResourceAsStream(GENERIC_RESOURCE_GCUBE_APPS_PROPERTIES);

			// load a properties file
			prop.load(in);

			// get the property value - the application Id
			this.appId = prop.getProperty(APP_ID);

			this.genericResource = prop.getProperty(SECONDARY_TYPE);

		} catch (IOException e) {
			logger.error("An error occurred on read property file "+e, e);
			throw new PropertyFileNotFoundException("An error occurred on read property file "+e);
		}
	}
	
	public String getAppId() {
		return appId;
	}

	public String getGenericResource() {
		return genericResource;
	}

}
