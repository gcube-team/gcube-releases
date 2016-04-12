package org.gcube.searchsystem.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

public class PlanCacheManager {
	
	private static CacheManager manager = null;
	private static String propertiesFile = "/plan_cache_config/default.properties";
	
	/**
	 * the logger for this class
	 */
	private static Logger logger = LoggerFactory.getLogger(PlanCacheManager.class.getName());
	
	/**
	 * default properties for cache
	 */
	private static int maxElementsInMemory;
	private static long tti;
	private static long ttl;
	private static boolean overflowToDisk;
	private static boolean eternal;
	private static boolean diskPersistent;
	private static long diskExpiryThreadIntervalSeconds;
	
	private static HashMap<String, PlanCache> caches = new HashMap<String, PlanCache>();
	
	private static List<String> nullProps = null;
	private static boolean initHasFailed = false;
	
	
	static {
		nullProps = new ArrayList<String>();
		initHasFailed = false;
		
		if (PlanCacheManager.class.getResource("/plan_cache_config/ehcache1.xml") == null)
			logger.info("ehcache1.xml could not be loaded");
		try {
			manager = CacheManager.create(PlanCacheManager.class.getResource("/plan_cache_config/ehcache1.xml"));
		} catch (Exception e) {
			logger.info("Error creating cachemanager. ", e);
		}
		
		loadProperties();
	}
	
	
	public static boolean checkInitializationError(){
		return (initHasFailed || nullProps.size() > 0);
	}
	
	public static String getInitializationError(){
		StringBuffer strBuf = new StringBuffer();
		
		strBuf.append("Error during initialization: ");
		if (initHasFailed)
			strBuf.append("Property file not found/loaded ");
		
		if (nullProps.size() > 0){
			strBuf.append("The following properties couldn't be loaded ");
			for (String s : nullProps)
				strBuf.append(s+" ");
		}
		return strBuf.toString();
	}
	
	
	private static void loadProperties() {
		Properties properties = null;
		String propertyStr = null;
		
		try {
			properties = new Properties();
			properties.load(PlanCacheManager.class.getResourceAsStream(propertiesFile ));
		} catch (FileNotFoundException ex) {
			logger.error(" *** File not found *** " + propertiesFile, ex);
			initHasFailed = true;
			return;
		}
		catch (IOException ex) {
			logger.error("*** Properties not loaded ***" + propertiesFile, ex);
			initHasFailed = true;
			return;
		}

		propertyStr = properties.getProperty("maxElementsInMemory");
		if (propertyStr == null)
			nullProps.add("maxElementsInMemory");
		else
			maxElementsInMemory = Integer.valueOf(propertyStr);
		
		propertyStr = properties.getProperty("timeToIdleSeconds");
		if (propertyStr == null)
			nullProps.add("timeToIdleSeconds");
		else
			tti = Long.valueOf(propertyStr);
		
		propertyStr = properties.getProperty("timeToLiveSeconds");
		if (propertyStr == null)
			nullProps.add("timeToLiveSeconds");
		else
			ttl = Long.valueOf(propertyStr);
		
		propertyStr = properties.getProperty("overflowToDisk");
		if (propertyStr == null)
			nullProps.add("overflowToDisk");
		else
			overflowToDisk = Boolean.valueOf(propertyStr);
		
		propertyStr = properties.getProperty("eternal");
		if (propertyStr == null)
			nullProps.add("eternal");
		else
			eternal = Boolean.valueOf(propertyStr);
		
		propertyStr = properties.getProperty("diskPersistent");
		if (propertyStr == null)
			nullProps.add("diskPersistent");
		else
			diskPersistent = Boolean.valueOf(propertyStr);
		
		propertyStr = properties.getProperty("diskExpiryThreadIntervalSeconds");
		if (propertyStr == null)
			nullProps.add("diskExpiryThreadIntervalSeconds");
		else
			diskExpiryThreadIntervalSeconds = Long.valueOf(propertyStr);
	}
	
	/**
	 * Get the cache with the specified name
	 * 
	 * @param cacheName - the specified name
	 * 
	 * @return the cache with the specified name. If there is 
	 * no such cache, it will create a new one with the specified
	 * name and return the new cache.
	 */
	public static PlanCache getCacheWithName(String cacheName) {
		
		logger.trace("getCacheWithName called with arg: " + cacheName);
		cacheName = cacheName.replaceAll("/", "_");
		logger.trace("getCacheWithName changed arg to: " + cacheName);
		
		//if there is cache with this name
		PlanCache planCache = caches.get(cacheName);
		if(planCache != null) {
			return planCache;
		}
		
		//first try to get a pre-existing ehcache 
		//for this name
		Cache cache = manager.getCache(cacheName);
		
		//if there is no ehcache with this name
		if(cache == null) {
			Cache newCache = new Cache(cacheName, 
					maxElementsInMemory, overflowToDisk, 
					eternal, ttl, tti, diskPersistent, 
					diskExpiryThreadIntervalSeconds);
			manager.addCache(newCache);
			cache = manager.getCache(cacheName);
		}
		
		planCache = new PlanCache(cache);
		
		caches.put(cacheName, planCache);
		
		return planCache;
	}

}
