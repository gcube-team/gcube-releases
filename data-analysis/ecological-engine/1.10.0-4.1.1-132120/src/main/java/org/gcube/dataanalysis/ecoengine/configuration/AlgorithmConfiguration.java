package org.gcube.dataanalysis.ecoengine.configuration;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.hibernate.SessionFactory;

import com.rapidminer.RapidMiner;

public class AlgorithmConfiguration extends LexicalEngineConfiguration implements Serializable{

	private static final long serialVersionUID = 1L;

	public static Properties getProperties(String absoluteFilePath) {
		Properties props = new Properties();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(absoluteFilePath);
			props.load(fis);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return props;
	}

	// constants
	public static String defaultConnectionFile = "DestinationDBHibernate.cfg.xml";
	public static String defaultLoggerFile = "ALog.properties";
	public static String algorithmsFile = "algorithms.properties";
	public static String nodeAlgorithmsFile = "nodealgorithms.properties";
	public static String generatorsFile = "generators.properties";
	public static String modelsFile = "models.properties";
	public static String modelersFile = "modelers.properties";
	public static String evaluatorsFile = "evaluators.properties";
	public static String clusterersFile = "clusterers.properties";
	public static String transducererFile = "transducerers.properties";
	public static String dynamicTransducerersFile = "dynamictransducerers.properties";
	public static String userperspectiveFile = "userperspective.properties";
	public static String RapidMinerOperatorsFile = "operators.xml";
	public static String StatisticalManagerService = "StatisticalManager";
	public static String StatisticalManagerClass = "Services";
	public static String listSeparator="#";
	
	public static String getListSeparator() {
		return listSeparator;
	}

	public static void setListSeparator(String listSeparator) {
		AlgorithmConfiguration.listSeparator = listSeparator;
	}

	public static int chunkSize = 100000;
	public static int refreshResourcesTime = 10;
	
	// Algorithm Parameters
	private String configPath;
	private String persistencePath;
	private String tableSpace;
	private String taskID="";
	
	private List<String> endpoints;
	
	//service and remote
	private Integer numberOfResources = 0;

	//modeling
	private String model;
	private String generator;
	private String gcubeScope;
	private String gcubeUserName;
	private String gcubeToken;
	
	//other properties
	private HashMap<String, String> generalProperties;
	
	public String getParam(String key){
		if (generalProperties != null)
			return generalProperties.get(key);
		else return null;
	}
	
	public void setParam(String key,String value){
		if (generalProperties == null)
			generalProperties = new HashMap<String, String>();
		
		generalProperties.put(key,value);
	}
	
	
	public void setConfigPath(String configPath) {
		if (!configPath.endsWith("/"))
			configPath+="/";
		this.configPath = configPath;
	}

	public String getConfigPath() {
		return configPath;
	}

	public void setNumberOfResources(Integer numberOfThreads) {
		this.numberOfResources = numberOfThreads;
	}

	public Integer getNumberOfResources() {
		return numberOfResources;
	}

	public void addGeneralProperties(HashMap<String, String> generalProperties) {
		for (String key:generalProperties.keySet()) {
			this.generalProperties.put(key,generalProperties.get(key));
		}
	}
	
	public void setGeneralProperties(HashMap<String, String> generalProperties) {
		this.generalProperties = generalProperties;
	}

	public HashMap<String, String> getGeneralProperties() {
		return generalProperties;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
	
	public String getPersistencePath() {
		return persistencePath;
	}

	public void setPersistencePath(String persistencePath) {
		this.persistencePath = persistencePath;
	}

	//the agent is the processor running an algorithm, or a modeler or an evaluator of performances
	//it has been distinguished from the Model variable in order to separate the meta-processor from the underlying processor
	public String getAgent() {
		return generator;
	}
	//the agent is the processor running an algorithm, or a modeler or an evaluator of performances
	//it has been distinguished from the Model variable in order to separate the meta-processor from the underlying processor
	public void setAgent(String generator) {
		this.generator = generator;
	}

	public static SessionFactory getConnectionFromConfig(AlgorithmConfiguration Input){
	// init the database
			String defaultDatabaseFile = Input.getConfigPath() + AlgorithmConfiguration.defaultConnectionFile;

			Input.setDatabaseDriver(Input.getParam("DatabaseDriver"));
			Input.setDatabaseUserName(Input.getParam("DatabaseUserName"));
			Input.setDatabasePassword(Input.getParam("DatabasePassword"));
			Input.setDatabaseURL(Input.getParam("DatabaseURL"));
			SessionFactory connection = null;
			try {
				connection = DatabaseFactory.initDBConnection(defaultDatabaseFile, Input);
			} catch (Exception e) {
				e.printStackTrace();
				AnalysisLogger.getLogger().trace("ERROR initializing connection");
			}
			return connection;
	}

	
	public void initRapidMiner(){
		System.setProperty("rapidminer.init.operators", configPath+ AlgorithmConfiguration.RapidMinerOperatorsFile);
		RapidMiner.init();
		AnalysisLogger.setLogger(getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		AnalysisLogger.getLogger().info("Rapid Miner initialized");
	}

	public String getGcubeUserName() {
		return gcubeUserName;
	}

	public void setGcubeUserName(String gcubeUserName) {
		this.gcubeUserName = gcubeUserName;
	}

	public String getGcubeToken() {
		return gcubeToken;
	}

	public void setGcubeToken(String gcubeToken) {
		this.gcubeToken = gcubeToken;
	}
	
	public String getGcubeScope() {
		return gcubeScope;
	}

	public void setGcubeScope(String gcubeScope) {
		this.gcubeScope = gcubeScope;
	}

	public List<String> getEndpoints() {
		return endpoints;
	}

	public void setEndpoints(List<String> endpoints) {
		this.endpoints = endpoints;
	}

	public String getTableSpace() {
		return tableSpace;
	}

	public void setTableSpace(String tableSpace) {
		this.tableSpace = tableSpace;
	}
	
	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}
	
	public String getTaskID() {
		return taskID;
	}

}
