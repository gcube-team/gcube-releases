package org.gcube.spatial.data.sdi.engine.impl.is;

import java.util.concurrent.ConcurrentHashMap;

import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;
import org.gcube.spatial.data.sdi.model.service.GeoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Cache<T> {

	private long objectsTTL;
	private ConcurrentHashMap<String,CachedObject<T>> theCache;
	private ISModule<T> retriever;
	private String cacheName;
	
	
	
	
	private Cache(long objectsTTL, ISModule<T> retriever, String cacheName) {
		super();
		this.objectsTTL = objectsTTL;
		this.retriever = retriever;
		this.cacheName=cacheName;
		theCache=new ConcurrentHashMap<>();
		
	}
	
	public synchronized T get() throws ConfigurationNotFoundException{
		String key=ScopeUtils.getCurrentScope();
		log.info("Getting object from cache{} , key is {} ",cacheName,key);
		if((!theCache.containsKey(key))||(!theCache.get(key).isValid(objectsTTL)))
			theCache.put(key, new CachedObject<T>(retriever.getObject()));
		return theCache.get(key).getTheObject();
	}
	
	public void invalidate(){
		String key=ScopeUtils.getCurrentScope();
		log.info("Invalidating cache {} under scope {} ",cacheName,key);
		if(theCache.containsKey(key))theCache.get(key).invalidate();
	}
	
	public void invalidateAll(){
		for(CachedObject<?> obj:theCache.values())obj.invalidate();
	}
	
	public static<T> Cache<T> getCache(ISModule<T> retriever, long objectsTTL,String cacheName){
		return new Cache<T>(objectsTTL,retriever,cacheName);
	}
	
}
