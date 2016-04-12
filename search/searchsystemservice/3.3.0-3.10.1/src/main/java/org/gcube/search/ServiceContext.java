package org.gcube.search;



import gr.uoa.di.madgik.commons.channel.proxy.tcp.ChannelTCPConnManagerEntry;
import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.environment.accounting.AccountingSystem;
import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;
import gr.uoa.di.madgik.environment.hint.EnvHint;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.hint.NamedEnvHint;
import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
import gr.uoa.di.madgik.execution.engine.ExecutionEngineConfig;
import gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver.ExecEngCallbackTCPConnManagerEntry;
import gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver.ExecEngTCPConnManagerEntry;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.gcube.searchsystem.environmentadaptor.ResourceRegistryAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;

public class ServiceContext {

	private static final int MAXATTEMPTS = 10;
	
	private static final Logger logger = LoggerFactory
			.getLogger(ServiceContext.class);

	public static final String PROPERTIES_FILE = "deploy.properties";
	public static final String ServiceClass = "Search";
	public static final String ServiceName = "SearchSystemService";
	
	EnvHintCollection adaptorHints = null;
	String scope = null;
	
	public ServiceContext() throws Exception {
		initialize();
	}
	
	EnvHintCollection getHints(){
		return adaptorHints;
	}
	
	String getScope(){
		return scope;
	}
	
	
	void initialize() throws Exception {

		adaptorHints=new EnvHintCollection();
		adaptorHints.AddHint(new NamedEnvHint("InformationSystemRIContainerServiceClass",new EnvHint(ServiceClass)));
		adaptorHints.AddHint(new NamedEnvHint("InformationSystemRIContainerServiceName",new EnvHint(ServiceName)));

		
		adaptorHints = new EnvHintCollection();
		adaptorHints.AddHint(new NamedEnvHint(
				"InformationSystemRIContainerServiceClass", new EnvHint(
						ServiceClass)));
		adaptorHints.AddHint(new NamedEnvHint(
				"InformationSystemRIContainerServiceName", new EnvHint(
						ServiceName)));
		
		Map<String, String> map = readProperties();
		this.scope = map.get("scope");

		initializeTCPManager(map);
		initializeExecutionEngineEnvironment();
		initializeExecutionEngine(map);
		initializeAccounting(map);
		initializeRR();
		
		
		
		this.setAdaptorHint("MaxCollocationCost", map);
		this.setAdaptorHint("OperatorNodeSelectorThreshold", map);
		this.setAdaptorHint("ExcludeLocal", map);
		this.setAdaptorHint("DataSourceNodeSelector", map);
		this.setAdaptorHint("DataSourceNodeSelectorTieBreaker", map);
		this.setAdaptorHint("NodeAssignmentPolicy", map);
		this.setAdaptorHint("ComplexPlanLevels", map);
		this.setAdaptorHint("ComplexPlanNumNodes", map);
		
	}
	
	
	
	
	void setAdaptorHint(String propName, Map<String, String> map){
		if (map.get(propName) == null){
			logger.info("property : " + propName + " not in map");
			return;
		} 
		this.adaptorHints.AddHint(new NamedEnvHint(propName, new EnvHint(map.get(propName).trim())));
	}
	

	private static Map<String, String> readProperties() throws FileNotFoundException, IOException {
		Map<String, String> map = new HashMap<String, String>();

		Properties prop = new Properties();
		
		try (InputStream is = Resources.getResource(PROPERTIES_FILE).openStream()) {
			prop.load(is);
		} catch (Exception e) {
			throw new IllegalArgumentException("could not load property file  : " + PROPERTIES_FILE);
		}
		
		for (String key : prop.stringPropertyNames()) {
			String value = prop.getProperty(key);
			if (value != null)
				map.put(key, value);
		}
		logger.info("properties read : " + map);
		return map;
	}

	static void addMapToHints(Map<String, String> map, EnvHintCollection hints) {
		for (Entry<String, String> en : map.entrySet()) {
			hints.AddHint(new NamedEnvHint(en.getKey(), new EnvHint(en
					.getValue())));
		}
	}

	static private String getHostName(Map<String, String> map) {
		return map.get("hostname").trim();

	}
	
	static private int getPort(Map<String, String> map) {
		try {
			int port = Integer.parseInt(map.get("port").trim());
			return port;
		} catch (Exception e) {
			logger.warn("error while parsing port from propery file", e);
			return 0;
		}
	}

	private static void initializeRR() throws InterruptedException, Exception {
		// initialize the ResourceRegistryAdapter which will be used in the
		// search operations
		int attempts = 0;
		while (attempts < MAXATTEMPTS) {
			if (ResourceRegistryAdapter.initializeAdapter()) {
				return;
			} else {
				Thread.sleep(1000);
				attempts++;
			}
		}
	}

	private static void initializeTCPManager(Map<String, String> props) {
		logger.info("Initalizing TCPManager...");
		TCPConnectionManager.Init(new TCPConnectionManagerConfig(getHostName(props), Arrays.asList(new PortRange(4000, 4100)), true));
		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
		logger.info("Initalizing TCPManager...OK");
	}

	private static void initializeExecutionEngineEnvironment() {
		logger.info("Initalizing Execution Engine Environment...");
		logger.info("Registering entries");
		TCPConnectionManager.RegisterEntry(new ExecEngTCPConnManagerEntry());
		TCPConnectionManager
				.RegisterEntry(new ExecEngCallbackTCPConnManagerEntry());
		TCPConnectionManager.RegisterEntry(new ChannelTCPConnManagerEntry());
		logger.info("Initalizing Execution Engine Environment...OK");
	}

	private static void initializeExecutionEngine(Map<String, String> props) {
		logger.info("Initializing Execution Engine...");
		ExecutionEngine.Init(new ExecutionEngineConfig(
				ExecutionEngineConfig.InfinitePlans, getHostName(props), getPort(props)));
		logger.info("Initializing Execution Engine...OK");
	}
	
	private static void initializeAccounting(Map<String, String> props){
		String providerName = props.get("providerAccountingName");
		try {
			AccountingSystem.init(providerName);
		} catch (EnvironmentValidationException e) {
			logger.warn("problem while initializing accounting");
		}
	}

}