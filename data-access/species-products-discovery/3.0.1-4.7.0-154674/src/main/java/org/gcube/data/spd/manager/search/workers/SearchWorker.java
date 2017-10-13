package org.gcube.data.spd.manager.search.workers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.caching.CacheKey;
import org.gcube.data.spd.caching.CacheWriter;
import org.gcube.data.spd.caching.QueryCache;
import org.gcube.data.spd.caching.QueryCacheFactory;
import org.gcube.data.spd.exception.MaxRetriesReachedException;
import org.gcube.data.spd.manager.search.Worker;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.StreamNonBlockingException;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.plugin.fwk.Searchable;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.utils.QueryRetryCall;
import org.gcube.data.spd.utils.Utils;
import org.gcube.data.spd.utils.VOID;

public class SearchWorker<T extends ResultElement> extends Worker<String, T> {

	private Searchable<T> searchable;
	private String pluginName;
	private String propertiesAsString;
	private Condition[] properties;
	boolean cachable = false;
	private CacheManager cacheManager;
	private QueryCacheFactory<T> queryCacheFactory;
	Set<String> searchDone; 

	
	public SearchWorker(ClosableWriter<T> writer, String pluginName, boolean cachable, 
			Searchable<T> searchable, CacheManager cacheManager, QueryCacheFactory<T> queryCacheFactory, Condition ...properties) {
		super(writer);
		this.pluginName = pluginName;
		this.propertiesAsString = Utils.getPropsAsString(properties);
		this.properties = properties;
		this.searchable = searchable;
		this.cachable = cachable;
		this.cacheManager = cacheManager;
		searchDone = Collections.synchronizedSet(new HashSet<String>());
		this.queryCacheFactory = queryCacheFactory;
	}

	@Override
	protected void execute(final String input, final ObjectWriter<T> writer) {

		logger.debug("("+pluginName+") searching for "+input+" with outputWriter alive? "+writer.isAlive());
		
		logger.trace("("+pluginName+") searchDone.contains(input)?"+(searchDone.contains(input)));
		
		if (searchDone.contains(input))	return;
		else searchDone.add(input);

		try {
			new QueryRetryCall(){

				@Override
				protected VOID execute() throws ExternalRepositoryException {
					search(input, writer);
					return VOID.instance();
				}
				
			}.call();
		} catch (MaxRetriesReachedException e) {
			logger.error("max retries reached for "+pluginName,e);
			writer.write(new StreamNonBlockingException(pluginName, input));
		}
		
		
	}


	private void search(String input, ObjectWriter<T> writer) throws ExternalRepositoryException{
		//add cache search using pluginName
		logger.trace("("+pluginName+") scope in search worker is set as "+ScopeProvider.instance.get());
		
		if (cachable){
			logger.debug("("+pluginName+") using cache");
			CacheKey key = new CacheKey(input, propertiesAsString,  searchable.getHandledClass());
			Cache cache = cacheManager.getCache(pluginName);	
			//logger.trace("lock is null? "+(QueryCache.lock==null ));
			QueryCache.lock.lock();
			if((cache.isKeyInCache(key) && cache.get(key)!=null && ((QueryCache<?>)cache.get(key).getValue()).isError())
					|| !cache.isKeyInCache(key)){
				if (cache.isKeyInCache(key)){
					logger.trace("removing invalid entry in cache ...");
					try{
						logger.trace("acquiring write lock "+pluginName);
						cache.acquireWriteLockOnKey(key);
						logger.trace("acquired write lock "+pluginName);
						cache.remove(key);
					}catch (Exception e) {
						logger.warn("problem removing cache ",e);
					}finally{
						logger.trace("releasing write lock "+pluginName);
						cache.releaseWriteLockOnKey(key);
						logger.trace("released write lock "+pluginName);
					}
					logger.trace("cache removed ...");
				}
				QueryCache<T> queryCache = this.queryCacheFactory.create(pluginName);
				cache.put(new Element(key, queryCache));
				QueryCache.lock.unlock();
				CacheWriter<T> cacheWriter = new CacheWriter<T>(writer, queryCache);
				searchable.searchByScientificName(input, cacheWriter, properties);
				cacheWriter.close();
				cache.put(new Element(key, queryCache));
			}else{ //execute normal query (in case someone else is filling this cache)
				QueryCache.lock.unlock();
				logger.debug("("+pluginName+") executing normal query in cachable plugin");
				searchable.searchByScientificName(input, writer, properties);		
			}
		} else{ //execute normal query
			logger.debug("("+this.pluginName+") executing normal query for "+input);
			searchable.searchByScientificName(input, writer, properties);		
		}
		logger.debug("("+pluginName+") finished search for "+input);
	}

	@Override
	public String descriptor() {
		return super.descriptor()+" - "+pluginName;
	}
	
	
	
}
