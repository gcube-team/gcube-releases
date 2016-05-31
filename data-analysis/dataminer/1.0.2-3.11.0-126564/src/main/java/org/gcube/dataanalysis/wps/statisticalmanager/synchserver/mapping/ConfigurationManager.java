package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping;

import java.io.File;
import java.io.InputStream;
import java.net.Inet4Address;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.n52.wps.commons.WPSConfig;

public class ConfigurationManager {

	public static String serviceUserNameParameterVariable = "ServiceUserName";
	public static String processingSessionVariable = "Session";
	public static String webpathVariable = "WebPath";
	public static String webPersistencePathVariable = "";
	public static String usernameParameter = "user.name";
	public static String scopeParameter = "scope";
	public static String defaultScope= "/gcube/devsec";
	public static String defaultUsername= "statistical.wps";
	
	private static Integer maxComputations = null;
	private static Boolean useStorage = null;
	static boolean simulationMode = false;
	
	public static synchronized Integer getMaxComputations(){
		return maxComputations;
	}
	
	public static synchronized Boolean useStorage(){
		return useStorage;
	}
	
	public static synchronized Boolean isSimulationMode(){
		return simulationMode;
	}
	
	public void getInitializationProperties() {
		try {
			if (maxComputations == null) {
				Properties options = new Properties();
				InputStream is = this.getClass().getClassLoader().getResourceAsStream("templates/setup.cfg");
				options.load(is);
				is.close();
				maxComputations = Integer.parseInt(options.getProperty("maxcomputations"));
				useStorage = Boolean.parseBoolean(options.getProperty("saveond4sstorage"));
				simulationMode=Boolean.parseBoolean(options.getProperty("simulationMode"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private AlgorithmConfiguration config;
	private String scope;
	private String username;

	public String getScope() {
		return scope;
	}

	public String getUsername() {
		return username;
	}

	public ConfigurationManager() {
		getInitializationProperties();
	}

	public AlgorithmConfiguration getConfig() {
		return config;
	}

	public void configAlgorithmEnvironment(LinkedHashMap<String, Object> inputs) throws Exception {
		// set config container
		config = new AlgorithmConfiguration();
		String webperspath = WPSConfig.getConfigDir() + "../persistence/";
		// selecting persistence path
//		String persistencePath = File.createTempFile("wpsstatcheck", ".sm").getParent() + "/../cfg/";
		String persistencePath = WPSConfig.getConfigDir() + "../ecocfg/";
		String configPath = persistencePath;
		if (!new File(configPath).isDirectory()) {
			configPath = "./cfg/";
			persistencePath = "./";
		}
		System.out.println("Taking configuration from " + (new File(configPath).getAbsolutePath()) + " and persistence in " + persistencePath);
		// setting configuration and logger
		config.setPersistencePath(persistencePath);
		config.setConfigPath(configPath);
		config.setNumberOfResources(1);
		AnalysisLogger.setLogger(configPath + "/" + AlgorithmConfiguration.defaultLoggerFile);
		AnalysisLogger.getLogger().debug("Taking configuration from " + configPath + " and persistence in " + persistencePath);
		// setting application paths
		String webapp = WPSConfig.getInstance().getWPSConfig().getServer().getWebappPath();
		if (webapp == null)
			webapp = "wps";
		String host = WPSConfig.getInstance().getWPSConfig().getServer().getHostname();
		if (host.toLowerCase().equals("localhost"))
			host = Inet4Address.getLocalHost().getHostAddress();
		String port = WPSConfig.getInstance().getWPSConfig().getServer().getHostport();
		AnalysisLogger.getLogger().debug("Host: " + host + " Port: " + port + " Webapp: " + webapp + " ");
		AnalysisLogger.getLogger().debug("Web persistence path: " + webperspath);
		
		String webPath = "http://" + host + ":" + port + "/" + webapp + "/persistence/";
		
		// AnalysisLogger.getLogger().debug("Env Vars: \n"+System.getenv());
		AnalysisLogger.getLogger().debug("Web app path: " + webPath);

		// retrieving scope
		scope = (String) inputs.get(scopeParameter);
		AnalysisLogger.getLogger().debug("Retrieved scope: " + scope);
		if (scope == null)
			throw new Exception("Error: scope parameter (scope) not set! This violates e-Infrastructure security policies");
		if (!scope.startsWith("/"))
			scope = "/" + scope;

		username = (String) inputs.get(usernameParameter);
		AnalysisLogger.getLogger().debug("User name used by the client: " + username);
		if (username == null || username.trim().length() == 0)
			throw new Exception("Error: user name parameter (user.name) not set! This violates e-Infrastructure security policies");
		
		config.setGcubeScope(scope);
		// DONE get username from request
		config.setParam(serviceUserNameParameterVariable, username);
		config.setParam(processingSessionVariable, "" + UUID.randomUUID());
		config.setParam(webpathVariable, webPath);
		config.setParam(webPersistencePathVariable, webperspath);

	}

}
