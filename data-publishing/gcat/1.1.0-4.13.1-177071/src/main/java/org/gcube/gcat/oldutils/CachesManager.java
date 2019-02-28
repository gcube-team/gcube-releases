package org.gcube.gcat.oldutils;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;

// import org.gcube.datacatalogue.metadatadiscovery.DataCalogueMetadataFormatReader;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Costantino Perciante (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class CachesManager {
	
	// the following caches are declared within the ehcache.xml (no default is available, so pay attention)
	// public static final String PROFILES_READERS_CACHE = "profile_readers";
	public static final String PROFILES_USERS_CACHE = "profile_users";
	
	private static final CacheManager cacheManager;
	// private static final Cache<String, DataCalogueMetadataFormatReader> readerCache;
	private static final Cache<String, JsonNode> userCache;
	
	static {
		CachingProvider provider = Caching.getCachingProvider();  
		cacheManager = provider.getCacheManager();
		
		/*
		MutableConfiguration<String, DataCalogueMetadataFormatReader> readerConfiguration =
			    new MutableConfiguration<String, DataCalogueMetadataFormatReader>()  
			        .setTypes(String.class, DataCalogueMetadataFormatReader.class)   
			        .setStoreByValue(false)   
			        .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_MINUTE));  
		readerCache = cacheManager.createCache(PROFILES_READERS_CACHE, readerConfiguration);
		*/
		
		MutableConfiguration<String, JsonNode> userConfiguration =
			    new MutableConfiguration<String, JsonNode>()  
			        .setTypes(String.class, JsonNode.class)   
			        .setStoreByValue(false)   
			        .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ONE_MINUTE));  
		userCache = cacheManager.createCache(PROFILES_USERS_CACHE, userConfiguration);
	}
	
	private CachesManager() {}
	
	/*
	public static Cache<String, DataCalogueMetadataFormatReader> getReaderCache() {
		return readerCache;
	}
	*/
	
	public static Cache<String, JsonNode> getUserCache() {
		return userCache;
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		cacheManager.close();
	}
	
}
