package org.gcube.datatransfer.resolver.gis.property;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;


/**
 * The Class ApplicationProfileGenericResourcePropertyReader.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 9, 2017
 */
public class ApplicationProfileGenericResourcePropertyReader {

	//protected static final String GENERIC_RESOURCE_GCUBE_APPS_PROPERTIES = "gisviewerappgenericresource.properties";
	protected static final String SECONDARY_TYPE = "SECONDARY_TYPE";
	protected static final String APP_ID = "APP_ID";

	private String appId;
	private String genericResource;

	private Logger logger = Logger.getLogger(ApplicationProfileGenericResourcePropertyReader.class);


	/**
	 * Instantiates a new gis viewer app generic resource property reader.
	 *
	 * @param fileNameProperty the file name property
	 * @throws PropertyFileNotFoundException the property file not found exception
	 */
	public ApplicationProfileGenericResourcePropertyReader(String fileNameProperty) throws PropertyFileNotFoundException {

		Properties prop = new Properties();
		try {
			InputStream in = ApplicationProfileGenericResourcePropertyReader.class.getResourceAsStream(fileNameProperty);
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

	/**
	 * Gets the app id.
	 *
	 * @return the app id
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * Gets the generic resource.
	 *
	 * @return the generic resource
	 */
	public String getGenericResource() {
		return genericResource;
	}

	/*
	public static void main(String[] args) {
		try {
			GisViewerAppGenericResourcePropertyReader reader = new GisViewerAppGenericResourcePropertyReader();
			System.out.println(reader.getAppId());
			System.out.println(reader.getGenericResource());
		} catch (PropertyFileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

}
