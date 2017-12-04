package org.gcube.rest.index.client.cache;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.google.common.cache.CacheBuilder;

@Configuration
@EnableCaching
@ComponentScan(basePackages = "org.gcube.rest.index.client.cache")
public class CacheConfig implements CachingConfigurer {

	public final static TimeUnit CACHE_TIME_UNIT = TimeUnit.SECONDS;
	public final static long CACHE_TIME = 60L;
	
	public final static String ENDPOINTS = "ENDPOINTS";
	public final static String COLLECTION_NAMES = "COLLECTION_NAMES";
	public final static String COLLECTIONS_FIELDS = "COLLECTIONS_FIELDS";
	public final static String COLLECTIONS_FIELDS_ALIASES = "COLLECTIONS_FIELDS_ALIASES";
	public final static String JSON_TRANSFORMERS = "JSON_TRANSFORMERS";
	public final static String COMPLETE_COLLECTION_INFOS = "COMPLETE_COLLECTION_INFOS";
	
	@Bean
	public IndexClient indexClient() {
		return new IndexClient();
	}

	@Bean
	@Override
	public CacheManager cacheManager() {
		
		SimpleCacheManager simpleCacheManager = new SimpleCacheManager();

		simpleCacheManager.setCaches(Arrays.asList(
				new GuavaCache(ENDPOINTS, CacheBuilder.newBuilder().expireAfterWrite(CACHE_TIME, CACHE_TIME_UNIT).build()),
				new GuavaCache(COLLECTION_NAMES, CacheBuilder.newBuilder().expireAfterWrite(CACHE_TIME, CACHE_TIME_UNIT).build()),
				new GuavaCache(COLLECTIONS_FIELDS, CacheBuilder.newBuilder().expireAfterWrite(CACHE_TIME, CACHE_TIME_UNIT).build()),
				new GuavaCache(COLLECTIONS_FIELDS_ALIASES, CacheBuilder.newBuilder().expireAfterWrite(CACHE_TIME, CACHE_TIME_UNIT).build()),
				new GuavaCache(JSON_TRANSFORMERS, CacheBuilder.newBuilder().expireAfterWrite(CACHE_TIME, CACHE_TIME_UNIT).build()),
				new GuavaCache(COMPLETE_COLLECTION_INFOS, CacheBuilder.newBuilder().expireAfterWrite(CACHE_TIME, CACHE_TIME_UNIT).build())
		));

		return simpleCacheManager;
	}

	@Override
	public CacheResolver cacheResolver() {
		return null;
	}

	@Override
	public CacheErrorHandler errorHandler() {
		return null;
	}

	@Override
	public KeyGenerator keyGenerator() {
		return null;
	}

}
