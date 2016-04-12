package org.gcube.security.soa3.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * 
 * Utility class that wraps the cache functionalities for SOA3 Responses
 * 
 * @author Ciro Formisano
 *
 */
public class SOA3EhcacheWrapper implements CacheWrapper<String, String> 
{
    private final String CACHE_NAME = "AssertionCache";
    private final CacheManager cacheManager;
    private static  SOA3EhcacheWrapper instance;

    /**
     * 
     * @return an instance of the Cache Wrapper
     */
    public static SOA3EhcacheWrapper getInstance ()
    {
    	if (instance == null) instance = new SOA3EhcacheWrapper();
    	
    	return instance;
    }
    
    private SOA3EhcacheWrapper()
    {
        this.cacheManager = CacheManager.create();
        Cache cache = new Cache(this.CACHE_NAME,0,true,false,300,150,false,0);
        cacheManager.addCache(cache);
    }
	/**
	 * {@inheritDoc}
	 */
    public void put(final String key, final String value)
    {
        getCache().put(new Element(key, value));
    }


    /**
     * 
     * @return the cache
     */
    public Ehcache getCache() 
    {
        return cacheManager.getEhcache(CACHE_NAME);
    }

	/**
	 * {@inheritDoc}
	 */
	public String get(String key) {
	     Element element = getCache().get(key);
	        if (element != null) {
	            return ((String) element.getValue());
	        }
	        return null;

	}
	
	/**
	 * 
	 */
	public void close ()
	{
		this.cacheManager.shutdown();
	}
}
