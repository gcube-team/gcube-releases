package org.gcube.datacatalogue.ckanutillibrary.server;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Please invoke this method to retrieve an object of this kind per scope.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class DataCatalogueFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(DataCatalogueFactory.class);
	private static final long MAX_LIFETIME = 1000 * 60 * 30; // 30 MINUTES
	private static DataCatalogueFactory instance = new DataCatalogueFactory();
	private static ConcurrentHashMap<String, CacheBean> cache;
	
	/**
	 * Private constructor for the hashmap's object values
	 * @author Costantino Perciante at ISTI-CNR 
	 * (costantino.perciante@isti.cnr.it)
	 *
	 */
	private class CacheBean{
		DataCatalogueImpl utils;
		long ttl;
		
		public CacheBean(long ttl, DataCatalogueImpl utils){
			this.ttl = ttl;
			this.utils = utils;
		}
	}
	
	/**
	 * Private constructor
	 */
	private DataCatalogueFactory(){
		
		logger.debug("Ckan factory object build");
		cache = new ConcurrentHashMap<String, CacheBean>(); 
		
	}
	
	/**
	 * Get the factory instance
	 * @return
	 */
	public static DataCatalogueFactory getFactory(){
		logger.debug("Factory requested");
		return instance;
	} 
	
	/**
	 * Retrieve the ckan utils information for the given scope
	 * @param scope
	 * @return
	 * @throws Exception
	 */
	public DataCatalogueImpl getUtilsPerScope(String scope) throws Exception{
		if(scope == null || scope.isEmpty())
			throw new IllegalArgumentException("Invalid scope given!");
		
		if(cache.containsKey(scope) && !expired(cache.get(scope))){
			return cache.get(scope).utils;
		}
		else{
			logger.info("Creating CKAN LIB utils for scope " + scope);
			DataCatalogueImpl utils = new DataCatalogueImpl(scope);
			cache.put(scope, new CacheBean(System.currentTimeMillis(), utils));
			return utils;
		}
	}

	/**
	 * Check if the ckan information must be retrieved again.
	 * @param cacheBean
	 * @return
	 */
	private boolean expired(CacheBean cacheBean) {
		boolean expired = (cacheBean.ttl + MAX_LIFETIME <= System.currentTimeMillis());
		logger.debug("expired is " + expired);
		return expired;
	}
}
