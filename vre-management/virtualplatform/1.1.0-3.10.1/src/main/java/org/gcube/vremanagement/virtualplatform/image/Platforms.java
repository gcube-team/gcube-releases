package org.gcube.vremanagement.virtualplatform.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.virtualplatform.model.TargetPlatform;

/**
 * Reader for {@link TargetPlatform} configurations
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public final class Platforms {

	private static GCUBELog logger = new GCUBELog(Platforms.class);
	
	/**
	 * Recursively reads the platform configurations from the given folder
	 * @param configFolder the folder containing the configurations 
	 * @return the configurations found
	 */
	public static List<PlatformConfiguration> listAvailablePlatforms(File configFolder) {
		if (!configFolder.isDirectory())
			return Collections.emptyList();
		List<PlatformConfiguration> platforms = new ArrayList<PlatformConfiguration>();
		scanFolder(configFolder, platforms);
		return Collections.unmodifiableList(platforms);
	}
	
	/**
	 * Looks for all the possible configuration files in the given folder
	 * @param folder the folder to read
	 * @param platforms the list of platforms to populate when a new configuration is found
	 */
	private static void scanFolder(File folder, List<PlatformConfiguration> platforms) {
		logger.debug("Scanning folder " + folder.getAbsolutePath());
		for (File file : folder.listFiles()) {
			if (file.isDirectory())
				scanFolder(file, platforms);
			else if (file.getAbsolutePath().endsWith(".properties") 
					|| (file.getAbsolutePath().endsWith(".props"))) {
				try {
					platforms.add(readConfig(file));
				}catch (Exception e) {
					e.printStackTrace();
					logger.warn("Failed to load a platform config from properties file " + file.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * Reads the configuration from the given file
	 * @param file a properties file
	 * @return the configuration found, if any
	 * @exception if the configuration is not valid
	 */
	private static PlatformConfiguration readConfig(File file)  throws Exception {
		logger.debug("Trying to load a platform from " + file.getAbsolutePath());
		PlatformConfiguration configuration = new PlatformConfiguration();
		InputStream stream = new FileInputStream(file);
		Properties properties = new Properties();
		properties.load(stream);
		/*	
		A typical platform file looks like:
		Name=TOMCAT
		Version=6
		PlatformClass=org.gcube.vremanagement.deployer.client.tomcat.Container
		Resources=
		ResourceFolder=
		DedicatedClassLoader=true
		URLManager=http://localhost:8080/manager
		User=manuele
		Password=manuele */
		
		configuration.setName((String) getPropertyValue(properties,"Name"));
		logger.debug("Found configuration for platform " + configuration.getName());
		configuration.setVersion(Short.valueOf((String) getPropertyValue(properties,"Version")));
		configuration.setPlatformClass(((String) getPropertyValue(properties,"PlatformClass")));
		String[] resources = ((String)getPropertyValue(properties,"Resources")).split(",");
		List<File> resourceFiles = new ArrayList<File>();
		for (int i=0; i<resources.length;i++)
			resourceFiles.add(new File(file.getParentFile().getAbsolutePath() + File.separator +resources[i]));
		configuration.setFolder(/*new File((String)getPropertyValue(properties,"ResourceFolder"))*/ file.getParentFile());		
		//scan the ResourceFolder and add the resources != from dir
		scanFolderForResources(configuration.getFolder(), resourceFiles);
		logger.trace("Resources found: " + resourceFiles.toString());
		configuration.setResources(resourceFiles.toArray(new File[] {}));
		configuration.setRequireDedicatedClassloader((Boolean.valueOf((String) getPropertyValue(properties,"DedicatedClassLoader"))));
		configuration.setBaseURL(new URL((String)getPropertyValue(properties,"BaseURL")));
		configuration.setUser((String)getPropertyValue(properties,"User"));
		configuration.setPassword((String)getPropertyValue(properties,"Password"));
		stream.close();
		return configuration;
	}
	
	private static Object getPropertyValue(Properties properties, String key) throws Exception {
		if (properties.get(key) == null)
			throw new Exception("Configuration property " + key + " not found");
		else 
			return properties.get(key); 
	}
	
	/**
	 * Looks for all the possible resources in the given folder
	 * @param folder the folder to read
	 * @param resources the list of platforms to populate when a new configuration is found
	 */
	private static void scanFolderForResources(File folder, List<File> resources) {
		logger.trace("Scanning folder " + folder.getAbsolutePath());
		//System.out.println("Scanning folder " + folder.getAbsolutePath());

		for (File file : folder.listFiles()) {
			if (file.isDirectory())
				scanFolderForResources(file, resources);
			else
				resources.add(file);
		}
	}
}
