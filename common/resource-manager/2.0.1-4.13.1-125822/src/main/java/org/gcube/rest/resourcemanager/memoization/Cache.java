package org.gcube.rest.resourcemanager.memoization;

import com.google.common.cache.CacheStats;

public interface Cache {
	public Object get(Object key);
	public void put(Object key, Object value);
	public CacheStats stats();
	public long size();
}
