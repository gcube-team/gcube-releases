package org.gcube.data.spd.manager.search.workers;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.gcube.data.spd.caching.CacheKey;
import org.gcube.data.spd.caching.QueryCache;
import org.gcube.data.spd.manager.search.EventDispatcher;
import org.gcube.data.spd.manager.search.writers.ConsumerEventHandler;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchCachingEventDispatcher<T extends ResultElement> extends EventDispatcher<String> {

	private CacheManager cacheManager;
	private String propertiesAsString;
	private Class<?> handledClass;
	private String pluginName;
		
	private Logger logger = LoggerFactory.getLogger(SearchCachingEventDispatcher.class);
	
	public SearchCachingEventDispatcher(ConsumerEventHandler<String> standardWorker,
			ConsumerEventHandler<String> cacheReaderWorker, CacheManager cacheManager, String pluginName, 
			Condition[] properties, Class<?> handledClass) {
		super(standardWorker, cacheReaderWorker);
		this.cacheManager = cacheManager;
		this.propertiesAsString = Utils.getPropsAsString(properties);
		this.handledClass = handledClass;
		this.pluginName = pluginName;
	}

	@Override
	public synchronized boolean sendToStandardWriter(String input) {
		CacheKey key = new CacheKey(input, propertiesAsString,  handledClass);
		Cache cache = cacheManager.getCache(pluginName);
		logger.trace("is key in cache? "+cache.isKeyInCache(key));
		logger.trace("QueryCacheEntry is "+key);
		boolean toReturn = !(cache.isKeyInCache(key) && cache.get(key)!=null && ((QueryCache<?>)cache.get(key).getValue()).isValid());
		logger.trace("sending it to the "+(toReturn?"standard":"secondary")+" worker");
		return toReturn;
	}

	
}
