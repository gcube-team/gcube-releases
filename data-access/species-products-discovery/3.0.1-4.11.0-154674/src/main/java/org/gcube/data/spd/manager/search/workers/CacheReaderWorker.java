package org.gcube.data.spd.manager.search.workers;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.gcube.data.spd.caching.CacheKey;
import org.gcube.data.spd.caching.QueryCache;
import org.gcube.data.spd.manager.search.Worker;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.utils.Utils;

public class CacheReaderWorker<T> extends Worker<String, T> {

	private CacheManager cacheManager;
	private String propertiesAsString;
	private Class<?> handledClass;
	private String pluginName;
	
	public CacheReaderWorker(ClosableWriter<T> writer, CacheManager cacheManager, String pluginName,
			Condition[] properties, Class<?> handledClass) {
		super(writer);
		this.cacheManager = cacheManager;
		this.propertiesAsString = Utils.getPropsAsString(properties);
		this.handledClass = handledClass;
		this.pluginName = pluginName;
	}

	@Override
	protected void execute(String input, ObjectWriter<T> outputWriter) {
		logger.trace("starting cache reader worker for "+input);
		CacheKey key = new CacheKey(input, propertiesAsString,  handledClass);
		Cache cache = cacheManager.getCache(pluginName);	
		@SuppressWarnings("unchecked")
		QueryCache<T> cacheReader = ((QueryCache<T>)cache.get(key).getValue());
		cacheReader.getAll(outputWriter);
	}
	
}
