package org.gcube.rest.resourcemanager.memoization;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.cache.CacheStats;

public class SimpleCache implements Cache {
	Map<Object, Object> cache;

	public SimpleCache() {
		this.cache = new ConcurrentHashMap<Object, Object>();
	}

	public Object get(Object key) {
		return cache.get(key);
	}

	public void put(Object key, Object value) {
		cache.put(key, value);
	}

	@Override
	public CacheStats stats() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long size() {
		return cache.size();
	}
}