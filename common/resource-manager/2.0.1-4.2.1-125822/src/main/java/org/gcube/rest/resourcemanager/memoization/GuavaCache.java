package org.gcube.rest.resourcemanager.memoization;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheStats;

public class GuavaCache implements Cache{
	private final com.google.common.cache.Cache<Object, Object> cache;

	private GuavaCache(final com.google.common.cache.Cache<Object, Object> cache) {
		this.cache = cache;
	}

	public Object get(Object key) {
		return cache.getIfPresent(key);
	}

	public void put(Object key, Object value) {
		cache.put(key, value);
	}

	public static class CacheBuilder {
		private Long maximumSize;
		private Long expirationDuration;
		private TimeUnit unit;

		public CacheBuilder() {
		}

		public CacheBuilder maximumSize(Long maximumSize) {
			this.maximumSize = maximumSize;
			return this;
		}

		public CacheBuilder expireAfterWrite(Long duration, TimeUnit unit) {
			this.expirationDuration = duration;
			this.unit = unit;
			return this;
		}

		public GuavaCache build() {
			com.google.common.cache.CacheBuilder<Object, Object> cacheBuilder = com.google.common.cache.CacheBuilder.newBuilder();
			if (maximumSize != null)
				cacheBuilder.maximumSize(maximumSize);
			if (expirationDuration != null && unit != null)
				cacheBuilder.expireAfterWrite(expirationDuration, unit);

			return new GuavaCache(cacheBuilder.recordStats().build());
		}
	}

	@Override
	public CacheStats stats() {
		return cache.stats();
	}

	@Override
	public long size() {
		return cache.size();
	}
}