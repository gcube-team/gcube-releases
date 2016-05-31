/**
 * 
 */
package org.gcube.common.homelibrary.util.config;

import java.io.File;
import java.net.URL;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.util.config.easyconf.ComponentConfiguration;
import org.gcube.common.homelibrary.util.config.easyconf.ComponentProperties;
import org.gcube.common.homelibrary.util.config.easyconf.EasyConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class HomeLibraryConfiguration {

	/**
	 * Home Library persistence dir variable name.
	 */
	public static final String HOME_LIBRARY_PERSISTENCE_DIR_VARIABLE_NAME = "HOME_LIBRARY_PERSISTENCE_DIR";

	protected static final String PERSISTENCE_FOLDER = "persistence-folder";
	private static final String HOME_MANAGER_FACTORY_IMPLEMENTATION = "home-manager-factory-implementation";
//	protected static final String DEFAULT_PROPERTY_FILES = "/org/gcube/portlets/user/homelibrary/home/homelibrary.properties";
	protected static final String DEFAULT_PROPERTY_FILES = "/homelibrary.properties";

	protected static HomeLibraryConfiguration configuration;

	/**
	 * Returns the {@link HomeLibraryConfiguration} singleton instance.
	 * @return the singleton instance.
	 */
	public static HomeLibraryConfiguration getInstance()
	{
		if (configuration==null) configuration = new HomeLibraryConfiguration();
		return configuration;
	}

	protected ComponentConfiguration componentConfiguration;
	protected ComponentProperties properties;
	protected Logger logger = LoggerFactory.getLogger(HomeLibraryConfiguration.class);

	protected HomeLibraryConfiguration()
	{
	//	URL url = EasyConf.class.getResource(DEFAULT_PROPERTY_FILES);
		URL url = getClass().getResource(DEFAULT_PROPERTY_FILES);
		String name = url.toExternalForm();		
		int pos = name.lastIndexOf(".properties");
		if (pos != -1) name = name.substring(0, pos);
		componentConfiguration = EasyConf.getConfiguration(name);
		properties = componentConfiguration.getProperties();
		properties.setThrowExceptionOnMissing(true);
	}

	/**
	 * Returns the HomeManagerFactory class to use.
	 * @return the class to use as HomeManagerFactory.
	 * @throws ClassNotFoundException if the specified class is not found.
	 */
	public Class<?> getHomeManagerFactoryClass() throws ClassNotFoundException
	{
		return properties.getClass(HOME_MANAGER_FACTORY_IMPLEMENTATION);
	}

	/**
	 * Returns the persistence folder.
	 * @return the persistence folder to use.
	 * @throws InternalErrorException if an error occurs calculating the persistence folder.
	 */
	public String getPersistenceFolder() throws InternalErrorException
	{
		logger.info("calculating the persistence folder");

		String persistenceFolder = null;

		if (properties.containsKey(PERSISTENCE_FOLDER)){
			logger.debug("Persistence Folder specified through the properties file");
			persistenceFolder = properties.getString(PERSISTENCE_FOLDER);
		} else {
			logger.trace("calculating the base dir");
			String baseDir;
			
			if (System.getenv().containsKey(HOME_LIBRARY_PERSISTENCE_DIR_VARIABLE_NAME)){
				//BASE DIR specified on ENV vars
				logger.debug("Base dir specified through the environment dir variable");
				String envVariable = System.getenv(HOME_LIBRARY_PERSISTENCE_DIR_VARIABLE_NAME);
				logger.debug(HOME_LIBRARY_PERSISTENCE_DIR_VARIABLE_NAME+" = "+envVariable);
				baseDir = envVariable;
				
			} else if (System.getProperties().containsKey("catalina.base")) {
				//BASE DIR calculated from catalina.base property
				String catalinaBase = System.getProperty("catalina.base");
				logger.info("Using catalina.base property "+catalinaBase);
				baseDir = catalinaBase+File.separator+"webapps"+File.separator+"usersArea";
				
			}else{
				//using TMP dir as BASE DIR
				String tmpDir = System.getProperty("java.io.tmpdir");
				logger.info("Using tmp dir "+tmpDir);
				baseDir = tmpDir;
			}

			persistenceFolder = baseDir + File.separator + "home_library_persistence";
		}

		logger.trace("calculated persistenceFolder = "+persistenceFolder);

		return persistenceFolder;
	}

}
