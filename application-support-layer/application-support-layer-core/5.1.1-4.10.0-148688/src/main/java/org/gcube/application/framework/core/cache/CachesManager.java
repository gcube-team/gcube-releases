package org.gcube.application.framework.core.cache;

import java.util.HashMap;

import org.gcube.application.framework.core.cache.factories.GenericResourceCacheEntryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

/**
 * @author Valia Tsagkalidou (KNUA)
 * 
 * This class is a singleton that manages the available caches
 *
 */
public class CachesManager {
	protected static CachesManager cacheManager = new CachesManager();
	protected CacheManager manager;
//	protected Ehcache profileCache;
//	protected Ehcache genericResourceCache;
//	protected Ehcache searchConfigCache;
//	protected Ehcache collectionCache;
//	protected Ehcache contentCache;
//	protected Ehcache thumbnailCache;
//	protected Ehcache schemataCache;
//	protected Ehcache metadataCache;
	
	protected HashMap<String, Ehcache> caches;
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(CachesManager.class);
	
	/**
	 * The constructor
	 */
	protected CachesManager() {
		
		manager = CacheManager.create(CachesManager.class.getResource("/ehcache.xml"));
		
		caches = new HashMap<String, Ehcache>();
		
//		profileCache = manager.getEhcache("profiles");
		Ehcache genericResourceCache = manager.getEhcache("genericResources");
//		searchConfigCache = manager.getEhcache("searchConfiguration");
//		collectionCache = manager.getEhcache("collections");
//		contentCache = manager.getEhcache("content");
//		thumbnailCache = manager.getEhcache("thumbnail");
//		schemataCache = manager.getEhcache("schemata");
//		metadataCache = manager.getEhcache("metadata");
		
//		profileCache = new SelfPopulatingCache(profileCache, new ProfileCacheEntryFactory());
		genericResourceCache = new SelfPopulatingCache(genericResourceCache, new GenericResourceCacheEntryFactory());
		caches.put("genericResourceCache", genericResourceCache);
//		searchConfigCache = new SelfPopulatingCache(searchConfigCache, new SearchConfigCacheEntryFactory());
//		collectionCache = new SelfPopulatingCache(collectionCache, new CollectionCacheEntryFactory());
//		contentCache = new SelfPopulatingCache(contentCache, new ContentInfoCacheEntryFactory());
//		thumbnailCache = new SelfPopulatingCache(thumbnailCache, new ThumbnailCacheEntryFactory());
//		schemataCache = new SelfPopulatingCache(schemataCache, new SchemaInfoCacheEntryFactory());
//		metadataCache = new SelfPopulatingCache(metadataCache, new MetadataCacheEntryFactory());
	}

	/**
	 * @return the sigleton of CachesManager
	 */
	public static CachesManager getInstance() {
		return cacheManager;
	}
	
	public Ehcache getEhcache (String cacheName, CacheEntryFactory cacheFactory) {
		if (caches.get(cacheName) == null) {
			logger.debug("Didn't find any previous cache for "+cacheName+". Creating a new and returning that.");
			Ehcache newCache = manager.getEhcache(cacheName);
			newCache = new SelfPopulatingCache(newCache, cacheFactory);
			caches.put(cacheName, newCache);
		}
		return caches.get(cacheName);
	}
	
	
	

	/**
	 * @return the cache that contains the user profiles
	 */
//	public Ehcache getProfileCache() {
//		return profileCache;
//	}

	/**
	 * @return the cache that contains the generic resources
	 */
	public Ehcache getGenericResourceCache() {
		return caches.get("genericResourceCache");
	}
	
	

	/**
	 * @return  the cache that contains the search configurations
	 */
//	public Ehcache getSearchConfigCache() {
//		return searchConfigCache;
//	}

	/**
	 * @return the cache that contains the collections per VRE
	 */
//	public Ehcache getCollectionCache() {
//		return collectionCache;
//	}

	/**
	 * @return the cache that contains information about digital objects
	 */
//	public Ehcache getContentCache() {
//		return contentCache;
//	}

	/**
	 * @return the cache that contains thumbnails
	 */
//	public Ehcache getThumbnailCache() {
//		return thumbnailCache;
//	}

	/**
	 * @return the cache that contains the searchable fields for each metadata schema
	 */
//	public Ehcache getSchemataCache() {
//		return schemataCache;
//	}

	/**
	 * @return the cache that contains the metadata objects
	 */
//	public Ehcache getMetadataCache() {
//		return metadataCache;
//	}
	
}
