package org.gcube.data.spd.caching;

public class QueryCacheFactory<T> {

	String persistencePath;
	
	public QueryCacheFactory(String persistencePath){
		this.persistencePath = persistencePath;
	}
	
	public QueryCache<T> create(String pluginName){
		return new QueryCache<T>(pluginName, persistencePath);
	}
	
}
