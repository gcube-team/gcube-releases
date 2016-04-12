package org.gcube.rest.index.service;

import gr.uoa.di.madgik.rr.ResourceRegistryException;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import org.gcube.elasticsearch.FullTextNode;
import org.gcube.rest.index.common.Constants;
import org.gcube.rest.index.common.discover.IndexDiscovererAPI;
import org.gcube.rest.index.common.resources.IndexResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;


public class IndexClientWrapper implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String MAXIMUM_FRAGMENT_CNT = "maxFragmentCnt";
	private static final String MAXIMUM_FRAGMENT_SIZE = "maxFragmentSize";
	private static final String NO_REPLICAS = "noReplicas";
	private static final String NO_SHARDS = "noShards";
	private static final String ELASTICSEARCH_PORT = "elasticSearchPort";
	private static final String USE_RRADAPTOR = "useRRAdaptor";
	private static final String DATA_DIRECTORY = "dataDir";
	private static final String MAX_RESULTS = "maxResults";
	private static final String HOSTNAME = "hostname";
	
	private FullTextNode ftn;
	
	private static final Logger logger = LoggerFactory.getLogger(IndexClientWrapper.class);
	
	private IndexDiscovererAPI<IndexResource> indexDiscoverer;
	
	//@Inject
	public IndexClientWrapper(IndexDiscovererAPI<IndexResource> indexDiscoverer) {
		logger.info("Creating a new FullTextNodeClient with indexDiscoverer : " + indexDiscoverer);
		this.indexDiscoverer = indexDiscoverer;
	}
	
	public void initialize(String transformedClusterName, String clusterID, String scope) throws ResourceRegistryException, InterruptedException {
		
		try {
			final Properties properties = new Properties();
			try (InputStream is = Resources.getResource(Constants.PROPERTIES_FILE).openStream()) {
				properties.load(is);
			} catch (Exception e) {
				throw new IllegalArgumentException("could not load property file  : " + Constants.PROPERTIES_FILE);
			}
			
			Integer fragm_cnt = Integer.valueOf(properties.getProperty(MAXIMUM_FRAGMENT_CNT).trim());
			Integer fragm_size = Integer.valueOf(properties.getProperty(MAXIMUM_FRAGMENT_SIZE).trim());
			Integer noReplicas = Integer.valueOf(properties.getProperty(NO_REPLICAS).trim());
			Integer noShards = Integer.valueOf(properties.getProperty(NO_SHARDS).trim());
			Integer esPort = Integer.valueOf(properties.getProperty(ELASTICSEARCH_PORT).trim());
			Integer maxResults = null;
			
			try {
				maxResults = Integer.valueOf(properties.getProperty(MAX_RESULTS).trim());
			} catch (Exception e) {
				logger.info("maxResults not given");
			}
			
			String dataDirectory = properties.getProperty(DATA_DIRECTORY);
			Boolean useRRAdaptor = Boolean.valueOf(properties.getProperty(USE_RRADAPTOR));
			String hostname = properties.getProperty(HOSTNAME);
			
			logger.info("Data read from jndi");
			logger.info("fragm_cnt           : " + fragm_cnt);
			logger.info("fragm_size          : " + fragm_size);
			logger.info("noReplicas          : " + noReplicas);
			logger.info("noShards            : " + noShards);
			logger.info("esPort              : " + esPort);
			logger.info("dataDirectory       : " + dataDirectory);
			logger.info("useRRAdaptor        : " + useRRAdaptor);
			logger.info("maxResults          : " + maxResults);
			logger.info("hostname            : " + hostname);
			
			String dataDir = "./indexData/elasticsearch/";
			
			if (dataDirectory != null)
				dataDir = dataDirectory;
			
			String configDir = getConfigDirectory();
			logger.info("configDir            :  " + configDir);
			
			logger.info("Setting index scope to " + scope);
			
			
			logger.info("initializing fulltextnode...");
			
			//this.ftn = new FullTextNode(hostname, dataDir, transformedClusterName, "main-index", noReplicas, noShards, scope, fragm_cnt, fragm_size, useRRAdaptor);
			FullTextNode.Builder builder = new FullTextNode.Builder()
				.scope(scope)
				.hostname(hostname)
				.dataDir(dataDir)
				.configDir(configDir)
				.clusterName(transformedClusterName)
				.indexName("main-index")
				.noOfReplicas(noReplicas)
				.noOfShards(noShards)
				.maxFragmentCnt(fragm_cnt)
				.maxFragmentSize(fragm_size)
				.useResourceRegistry(useRRAdaptor);

			if (maxResults != null)
				builder.maxResults(maxResults);
			 
			 this.ftn = builder.build();
			
			logger.info("initializing fulltextnode...OK");
			
			
			logger.info("discovering fulltextindex nodes...");
			//TODO: discover resources and get elasticsearch port (es port should be updated when a resource is created/loaded
			Set<IndexResource> resources = this.indexDiscoverer.discoverFulltextIndexNodeResources(clusterID, null, null, scope);
			
			Set<String> knownNodes = Sets.newHashSet();
			if (resources == null || resources.size() == 0) {
				logger.warn("No other resources found. is this the first one?");
			} else {
				for (IndexResource resource : resources){
					if (resource.getEsTransportAddress() != null)
						knownNodes.add(resource.getEsTransportAddress());
				}
			}
			
			logger.info("discovering fulltextindex nodes...OK");
			logger.info("knownNodes : " + knownNodes);
			
			if(knownNodes.size()>0)
				this.ftn.joinCluster(Lists.newArrayList(knownNodes));
			else
				this.ftn.createOrJoinCluster();
			
			logger.info("recreating meta index from data to resolve inconsistencies");
			this.ftn.recreateMetaIndex();

			this.ftn.refreshIndexTypesOfIndex();
			
		} catch (ResourceRegistryException e) {
			throw e;
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception e) {
			logger.error("error while initializing the index client", e);
		}
	}
	
	static String getConfigDirectory(){
		logger.info("getting config dir");
		
		URL url = Resources.getResource("config/scripts");
		
		logger.info("config/scripts : " + url.toString());
		
		File f;
		try {
			f = new File(url.toURI());
			String configDir = f.getParent();
			
			logger.info("configDir : " + configDir);
			
			return configDir;

		} catch (URISyntaxException e) {
			logger.error("config/scripts not found. it should have been in the war");
			return null;
		}
		
	}
	
	public static void main(String[] args) {
		System.out.println(getConfigDirectory());
	}

	public FullTextNode getFullTextNode()
	{
		return this.ftn;
	}
	
	public String getScope()
	{
		return this.ftn.getScope();
	}
	
	public String getClusterName()
	{
		return this.ftn.getClusterName();
	}
	
}