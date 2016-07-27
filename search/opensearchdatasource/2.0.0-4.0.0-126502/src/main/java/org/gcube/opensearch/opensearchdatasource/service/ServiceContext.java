package org.gcube.opensearch.opensearchdatasource.service;

import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;
import gr.uoa.di.madgik.rr.ResourceRegistry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.gcube.opensearch.opensearchdatasource.service.helpers.CacheRefresher;
import org.gcube.opensearch.opensearchdatasource.service.helpers.OpenSearchDataSourceConfig;
import org.gcube.opensearch.opensearchdatasource.service.helpers.PropertiesFileConstants;
import org.gcube.rest.opensearch.common.Constants;
import org.gcube.rest.opensearch.common.discover.OpenSearchDataSourceDiscoverer;
import org.gcube.rest.opensearch.common.discover.OpenSearchDiscovererAPI;
import org.gcube.rest.opensearch.common.resources.OpenSearchDataSourceResource;
import org.gcube.rest.resourcemanager.harvester.ResourceHarvester;
import org.gcube.rest.resourcemanager.is.discoverer.ri.icclient.RIDiscovererISimpl;
import org.gcube.rest.resourcemanager.is.discovery.ISInformationCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;
import com.google.inject.Scope;

public class ServiceContext {

	static final Logger logger = LoggerFactory.getLogger(OpenSearchService.class);
	
	private final OpenSearchOperator operator;// = new OpenSearchOperator(new ISInformationCollector());
	private final OpenSearchDiscovererAPI<OpenSearchDataSourceResource> discoverer;// = new OpenSearchDataSourceDiscoverer(new RIDiscovererISimpl() , new ResourceHarvester<OpenSearchDataSourceResource>());
	
	public ServiceContext(OpenSearchOperator operator, OpenSearchDiscovererAPI<OpenSearchDataSourceResource> discoverer) {
		this.operator = operator;
		this.discoverer = discoverer;
	}
	
	
	public void initialize() {
		try {
			Map<String, String> properties = readProperties();
			String hostname = properties.get(PropertiesFileConstants.HOSTNAME_PROP);
			String scope = properties.get(PropertiesFileConstants.SCOPE_PROP);
			
			logger.debug("Now in resource home onInitialisation");

			TCPConnectionManager.Init(
					new TCPConnectionManagerConfig(hostname, new ArrayList<PortRange>(), true));
			
			TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
			TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
			logger.debug("Connection manager for gRS2 Initialized");

			
			Boolean useRR = true; 
			
			try {
				useRR = Boolean.valueOf(properties.get(PropertiesFileConstants.USE_RR_ADAPTOR_PROP));
			} catch (Exception e) {
				logger.warn("error parsing useRR property", e);
			}
			
			
			
//			EnvHintCollection envHints = new EnvHintCollection();
//			envHints.AddHint(new NamedEnvHint("retryOnErrorTimes", new EnvHint(
//					"5")));
//			envHints.AddHint(new NamedEnvHint("retryOnErrorInterval",
//					new EnvHint("200")));
//
//			InformationSystem
//					.Init("gr.uoa.di.madgik.environment.gcube.GCubeInformationSystemProvider",
//							envHints);
			
//			logger.debug("Information system initialized");
			
			if (useRR){
				ResourceRegistry.startBridging();
				logger.debug("Registry bridging initiated");
			}
			CacheRefresher refresher = null;
			
			
//			OpenSearchOperator operator = new OpenSearchOperator(new ISInformationCollector());
//			OpenSearchDataSourceDiscoverer discoverer = new OpenSearchDataSourceDiscoverer(new RIDiscovererISimpl() , new ResourceHarvester<OpenSearchDataSourceResource>());
			
			
			
			
			OpenSearchDataSourceConfig config = new OpenSearchDataSourceConfig();//(OpenSearchDataSourceConfig) StatefulContext.getPortTypeContext().getProperty("config", false);
			
			try {
				config.initFromPropertiesFile();
			} catch (Exception e) {
				logger.warn("error while reading from properties file", e);
				config = null;
			}
			
			if(config != null){
				logger.debug("OpenSearch DataSource Config:" +
						"\n   clearCacheOnStartup: " + config.getClearCacheOnStartup() +
						"\n   cacheRefreshIntervalMillis: " + config.getCacheRefreshIntervalMillis());
				refresher = new CacheRefresher(config.getCacheRefreshIntervalMillis(), operator, discoverer, hostname, scope);
			}else
				refresher = new CacheRefresher(0, operator, discoverer, hostname, scope);
				
		
			Thread cacheRefresher = new Thread(refresher);
			cacheRefresher.setDaemon(true);
			cacheRefresher.start();
		} catch (Exception e) {
			logger.error("Could not initialize Information System", e);
		}
	}
	
	
	private static Map<String, String> readProperties() throws FileNotFoundException, IOException {
		Map<String, String> map = new HashMap<String, String>();

		Properties prop = new Properties();
		
		try (InputStream is = Resources.getResource(Constants.PROPERTIES_FILE).openStream()) {
			prop.load(is);
		} catch (Exception e) {
			throw new IllegalArgumentException("could not load property file  : " + Constants.PROPERTIES_FILE);
		}
		
		for (String key : prop.stringPropertyNames()) {
			String value = prop.getProperty(key);
			if (value != null)
				map.put(key, value);
		}
		logger.info("properties read : " + map);
		return map;
	}
	
}
