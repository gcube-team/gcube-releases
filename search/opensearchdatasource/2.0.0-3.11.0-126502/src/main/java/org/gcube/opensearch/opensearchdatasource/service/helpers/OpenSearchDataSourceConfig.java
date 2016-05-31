package org.gcube.opensearch.opensearchdatasource.service.helpers;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.gcube.opensearch.opensearchlibrary.utils.FactoryClassNamePair;
import org.gcube.rest.opensearch.common.Constants;
//import org.globus.wsrf.jndi.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.io.Resources;

/**
 * The configuration parameters of the {@link OpenSearchDataSource}
 * 
 * @author NKUA
 *
 */
public class OpenSearchDataSourceConfig /*implements Initializable*/ {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenSearchDataSourceConfig.class);
    
    
    private Boolean clearCacheOnStartup;
    private Long cacheRefreshIntervalMillis;
    private Map<String, FactoryClassNamePair> factories = new HashMap<String, FactoryClassNamePair>();
    
    /**
     * Whether cache clear when first loading resources is enabled
     * @return true if cache clear on startup is enabled, false otherwise
     */
    public Boolean getClearCacheOnStartup() {
    	return clearCacheOnStartup;
    }
    
    /**
     * The time interval between cache refresh cycles
     * @return The time interval in milliseconds
     */
    public Long getCacheRefreshIntervalMillis() {
    	return cacheRefreshIntervalMillis;
    }
    
    /**
     * The namespace-to-factory class name mappings that the OpenSearch Library will use
     * @return A {@link Map} containing all the mappings from OpenSearch extension namespaces to factory class name pairs
     */
    public Map<String, FactoryClassNamePair> getFactories() {
    	return factories;
    }
    
    /**
     * Enables or disables cache clearing on startup
     * @param clearCacheOnStartup true if cache clearing should be enabled, false otherwise
     */
    public void setClearCacheOnStartup(Boolean clearCacheOnStartup) {
    	this.clearCacheOnStartup = clearCacheOnStartup;
    }
    
    /**
     * Sets the time interval between cache refresh cycles
     * @param cacheRefreshIntervalMillis The time interval in millisecods
     */
    public void setCacheRefreshIntervalMillis(Long cacheRefreshIntervalMillis) {
    	this.cacheRefreshIntervalMillis = cacheRefreshIntervalMillis;
    }
    
    /**
     * Called on initialization. Parses openSearchLibraryFactories and prints the current configutation
     * @throws Exception 
     */
    
    public void initFromPropertiesFile() throws Exception{
    	final Properties properties = new Properties();
    	try (InputStream is = Resources.getResource(Constants.PROPERTIES_FILE).openStream()) {
			properties.load(is);
		} catch (Exception e) {
			throw new Exception("could not load property file  : " + Constants.PROPERTIES_FILE);
		}
    	
    	Long cacheRefreshIntervalMillis = Long.valueOf(properties.getProperty(PropertiesFileConstants.CACHE_REFRESH_INTERVALMILLIS_PROP));
    	
    	this.cacheRefreshIntervalMillis = cacheRefreshIntervalMillis;

    	String factoriesString = properties.getProperty(PropertiesFileConstants.FACTORIES_PROP);
    	for (String mapEntryStr : Splitter.on(PropertiesFileConstants.MAP_DELIM).trimResults().omitEmptyStrings().splitToList(factoriesString)){
    		List<String> mapEntryList = Splitter.on(PropertiesFileConstants.MAP_EQ).trimResults().omitEmptyStrings().splitToList(mapEntryStr);
    		String key = mapEntryList.get(0);
    		String value = mapEntryList.get(1);
    		
    		factories.put(key, new FactoryClassNamePair(value));
    	}
    	
    	logger.debug("Initialized OpenSearchDataSource Config:" +
                "\n   clearCacheOnStartup: " + clearCacheOnStartup +
                "\n   cacheRefreshIntervalMillis: " + cacheRefreshIntervalMillis + 
                "\n   factories: " + factories
        );
    }
    
    
//    public void initialize() throws Exception {
//    	Pattern factoriesPattern = Pattern.compile("\\[[^=]*=\\([^,]*,[^\\)]*\\)\\]");
//		Matcher factoriesMatcher = factoriesPattern.matcher(openSearchLibraryFactories);
//		while(factoriesMatcher.find()) {
//			String factoryEntry = factoriesMatcher.group().trim();
//			factoryEntry = factoryEntry.substring(1).substring(0, factoryEntry.length()-2); //trim enclosing braces
//			String keyValue[] = factoryEntry.split("=");
//			if(keyValue.length != 2) {
//				System.out.println("Failed to parse factory entry: " + factoryEntry + ". Ignoring entry");
//				continue;
//			}
//			
//			try {
//				factories.put(keyValue[0].trim(), new FactoryClassNamePair(keyValue[1]));
//			}catch(Exception e) {
//				logger.debug("Failed to parse factory pair: " + keyValue[1] + ". Ignoring entry");
//				continue;
//			}
//		}
//  //  	factories.put(OpenSearchConstants.OpenSearchNS, new FactoryClassNamePair("org.gcube.opensearch.opensearchlibrary.urlelements.BasicURLElementFactory", "org.gcube.opensearch.opensearchlibrary.queryelements.BasicQueryElementFactory"));
//  //  	factories.put(TimeConstants.TimeExtensionsNS, new FactoryClassNamePair("org.gcube.opensearch.opensearchlibrary.urlelements.extensions.time.TimeURLElementFactory", "org.gcube.opensearch.opensearchlibrary.queryelements.extensions.time.TimeQueryElementFactory"));
//  //  	factories.put(GeoConstants.GeoExtensionsNS, new FactoryClassNamePair("org.gcube.opensearch.opensearchlibrary.urlelements.extensions.geo.GeoURLElementFactory", "org.gcube.opensearch.opensearchlibrary.queryelements.extensions.geo.GeoQueryElementFactory"));
//    	
//    	logger.debug("Initialized OpenSearchDataSource Config:" +
//                "\n   clearCacheOnStartup: " + clearCacheOnStartup +
//                "\n   cacheRefreshIntervalMillis: " + cacheRefreshIntervalMillis + 
//                "\n   factories: " + factories
//        );
//    }

}
