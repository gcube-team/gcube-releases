package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.UUID;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.n52.wps.commons.WPSConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationManager {

	private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
	
	public static String serviceUserNameParameterVariable = "ServiceUserName";
	public static String processingSessionVariable = "Session";
	public static String webpathVariable = "WebPath";
	public static String webPersistencePathVariable = "";
	public static String usernameParameter = "user.name";
	public static String scopeParameter = "scope";
	public static String tokenParameter = "usertoken";
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
			logger.error("error initializing properties",e);
		}
	}

	private AlgorithmConfiguration config;
	private String scope;
	private String username;
	private String token;

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

	public void setComputationId(String computationId){
		config.setTaskID(computationId);
	}
	
	public void configAlgorithmEnvironment(LinkedHashMap<String, Object> inputs) throws Exception {
		// set config container
		config = new AlgorithmConfiguration();
		config.setAlgorithmClassLoader(Thread.currentThread().getContextClassLoader());
		String webperspath = WPSConfig.getConfigDir() + "../persistence/";
		// selecting persistence path
//		String persistencePath = File.createTempFile("wpsstatcheck", ".sm").getParent() + "/../cfg/";
	
		//TODO: REMOVE this shit (the persistence must be the persistence dir of the webapp)
		String persistencePath = WPSConfig.getConfigDir() + "../ecocfg/";
		String configPath = persistencePath;
		if (!new File(configPath).isDirectory()) {
			configPath = "./cfg/";
			persistencePath = "./";
		}
		logger.debug("Taking configuration from " + (new File(configPath).getAbsolutePath()) + " and persistence in " + persistencePath);
		// setting configuration and logger
		config.setPersistencePath(persistencePath);
		config.setConfigPath(configPath);
		config.setNumberOfResources(1);
		// setting application paths
		String webapp = WPSConfig.getInstance().getWPSConfig().getServer().getWebappPath();
		String host = WPSConfig.getInstance().getWPSConfig().getServer().getHostname();
		String port = WPSConfig.getInstance().getWPSConfig().getServer().getHostport();
		logger.debug("Host: " + host + " Port: " + port + " Webapp: " + webapp + " ");
		logger.debug("Web persistence path: " + webperspath);
		
		String webPath = "http://" + host + ":" + port + "/" + webapp + "/persistence/";
		
		// logger.debug("Env Vars: \n"+System.getenv());
		logger.debug("Web app path: " + webPath);

		// retrieving scope
		scope = (String) inputs.get(scopeParameter);
		logger.debug("Retrieved scope: " + scope);
		if (scope == null)
			throw new Exception("Error: scope parameter (scope) not set! This violates e-Infrastructure security policies");
		if (!scope.startsWith("/"))
			scope = "/" + scope;

		username = (String) inputs.get(usernameParameter);
		token = (String) inputs.get(tokenParameter);
		
		logger.debug("User name used by the client: " + username);
		logger.debug("User token used by the client: " + token);
		
		if (username == null || username.trim().length() == 0)
			throw new Exception("Error: user name parameter (user.name) not set! This violates e-Infrastructure security policies");
		
		if (token == null || token.trim().length() == 0)
			throw new Exception("Error: token parameter not set! This violates e-Infrastructure security policies");

		config.setGcubeScope(scope);
		config.setGcubeUserName(username);
		config.setGcubeToken(token);
		// DONE get username from request
		config.setParam(serviceUserNameParameterVariable, username);
		config.setParam(processingSessionVariable, "" + UUID.randomUUID());
		config.setParam(webpathVariable, webPath);
		config.setParam(webPersistencePathVariable, webperspath);

	}

}
