package org.gcube.portal.social.networking.caches;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

/**
 * Handle caches via Ehcache
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CachesManager {

	private static CacheManager cacheManager;
	public static final CachesManager singleton = new CachesManager();

	// the following caches are declared within the ehcache.xml (no default is available)
	public static final String SOCIAL_NETWORKING_SITES_CACHE = "social_networking_site_cache";
	public static final String USERS_CACHE = "users_cache";
	public static final String GROUPS_CACHE = "groups_cache";
	
	private CachesManager(){
		cacheManager = CacheManager.newInstance();
	}

	public static Ehcache getCache(String name){
		return cacheManager.getEhcache(name);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		cacheManager.shutdown();
	}

}
