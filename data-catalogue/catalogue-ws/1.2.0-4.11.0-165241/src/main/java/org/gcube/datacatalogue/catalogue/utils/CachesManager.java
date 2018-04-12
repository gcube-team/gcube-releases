package org.gcube.datacatalogue.catalogue.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

/**
 * Handle caches via Ehcache stuff.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CachesManager {

	private static CacheManager cacheManager;
	public static final CachesManager singleton = new CachesManager();

	// the following caches are declared within the ehcache.xml (no default is available, so pay attention)
	public static final String PROFILES_READERS_CACHE = "profile_readers";
	public static final String PROFILES_USERS_CACHE = "profile_users";

	private CachesManager(){
		cacheManager = CacheManager.newInstance();
	}

	public static Cache getCache(String name){
		return cacheManager.getCache(name);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		cacheManager.shutdown();
	}

}
