package org.gcube.portlets.admin.accountingmanager.server.amservice.cache;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.expiry.Expirations;
import org.ehcache.jsr107.Eh107Configuration;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValuesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class AccountingCache implements Serializable {
	private static final long serialVersionUID = -4352823042594405108L;

	private static Logger logger = LoggerFactory.getLogger(AccountingCache.class);

	private static final String ACCOUNTING_SERIES_CACHE = "AccountingSeriesCache";
	private static final String ACCOUNTING_FILTER_VALUES_CACHE = "AccountingFilterValuesCache";
	private CacheManager cacheManager;
	private CacheConfiguration<String, SeriesResponse> cacheSeriesConfiguration;
	private CacheConfiguration<String, FilterValuesResponse> cacheFilterValuesConfiguration;

	// private MBeanServer mBeanServer;

	public AccountingCache() throws ServiceException {
		super();
		try {

			// Resolve a cache manager
			CachingProvider cachingProvider = Caching.getCachingProvider();
			cacheManager = cachingProvider.getCacheManager();
			logger.info("AccountingCache URI: " + cacheManager.getURI());

			ResourcePoolsBuilder builderSeries = ResourcePoolsBuilder.newResourcePoolsBuilder().heap(60L, MemoryUnit.MB)
					.offheap(240L, MemoryUnit.MB);

			// .disk(500L, MemoryUnit.KB)

			// .withSizeOfMaxObjectSize(1L, MemoryUnit.KB)
			cacheSeriesConfiguration = CacheConfigurationBuilder
					.newCacheConfigurationBuilder(String.class, SeriesResponse.class, builderSeries)
					.withExpiry(Expirations.timeToLiveExpiration(org.ehcache.expiry.Duration.of(30L, TimeUnit.MINUTES)))
					.withSizeOfMaxObjectSize(50L, MemoryUnit.MB).build();

			// mBeanServer = ManagementFactory.getPlatformMBeanServer();

			/*
			 * PersistentCacheManager myCacheManager =
			 * CacheManagerBuilder.newCacheManagerBuilder()
			 * .with(CacheManagerBuilder.persistence("AccountingCache"))
			 * .build(true);
			 */

			ResourcePoolsBuilder builderFilterValues = ResourcePoolsBuilder.newResourcePoolsBuilder()
					.heap(10L, MemoryUnit.MB).offheap(30L, MemoryUnit.MB);

			// .disk(500L, MemoryUnit.KB)

			// .withSizeOfMaxObjectSize(1L, MemoryUnit.KB)
			cacheFilterValuesConfiguration = CacheConfigurationBuilder
					.newCacheConfigurationBuilder(String.class, FilterValuesResponse.class, builderFilterValues)
					.withExpiry(Expirations.timeToLiveExpiration(org.ehcache.expiry.Duration.of(30L, TimeUnit.MINUTES)))
					.withSizeOfMaxObjectSize(4L, MemoryUnit.MB).build();

		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);

		}
	}

	private Cache<String, SeriesResponse> initCacheSeries() throws ServiceException {
		try {
			String cacheSeriesName = ACCOUNTING_SERIES_CACHE;

			// create the cache
			Cache<String, SeriesResponse> cacheSeries = cacheManager.getCache(cacheSeriesName, String.class,
					SeriesResponse.class);

			if (cacheSeries == null) {
				/*
				 * MutableConfiguration<String, SeriesResponse> config = new
				 * MutableConfiguration<String, SeriesResponse>()
				 * .setTypes(String.class, SeriesResponse.class)
				 * .setExpiryPolicyFactory( CreatedExpiryPolicy
				 * .factoryOf(Duration.THIRTY_MINUTES))
				 * .setStatisticsEnabled(true);
				 * 
				 * cache = cacheManager.createCache(cacheName, config);
				 */

				cacheSeries = cacheManager.createCache(cacheSeriesName,
						Eh107Configuration.fromEhcacheCacheConfiguration(cacheSeriesConfiguration));
				cacheManager.enableManagement(cacheSeriesName, true);
				cacheManager.enableStatistics(cacheSeriesName, true);

				/*
				 * ObjectName objectName = new
				 * ObjectName("javax.cache:type=CacheStatistics" +
				 * " , CacheManager=" +
				 * (cache.getCacheManager().getURI().toString()) + " , Cache=" +
				 * cache.getName());
				 * 
				 * mBeanServer.registerMBean(cache, objectName);
				 */

			}

			return cacheSeries;

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);

		}

	}

	private Cache<String, FilterValuesResponse> initCacheFilterValues() throws ServiceException {
		try {
			String cacheFilterValuesName = ACCOUNTING_FILTER_VALUES_CACHE;

			// create the cache
			Cache<String, FilterValuesResponse> cacheFilterValues = cacheManager.getCache(cacheFilterValuesName,
					String.class, FilterValuesResponse.class);

			if (cacheFilterValues == null) {

				cacheFilterValues = cacheManager.createCache(cacheFilterValuesName,
						Eh107Configuration.fromEhcacheCacheConfiguration(cacheFilterValuesConfiguration));
				cacheManager.enableManagement(cacheFilterValuesName, true);
				cacheManager.enableStatistics(cacheFilterValuesName, true);

			}

			return cacheFilterValues;

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);

		}

	}

	public void putSeries(String key, SeriesResponse value) throws ServiceException {
		logger.debug("Cache put: [" + key + ", " + value + "]");
		Cache<String, SeriesResponse> cacheSeries = initCacheSeries();
		cacheSeries.put(key, value);
		logger.debug("Cached: [" + key + ", " + value + "]");

	}

	/**
	 * 
	 * @param key
	 *            key
	 * @return series response
	 * @throws ServiceException
	 *             service exception
	 */
	public SeriesResponse getSeries(String key) throws ServiceException {
		logger.debug("Cache get: [" + key + "]");
		Cache<String, SeriesResponse> cacheSeries = initCacheSeries();
		SeriesResponse value = cacheSeries.get(key);
		logger.debug("Cached value: [" + key + ", " + value + "]");
		return value;
	}

	/**
	 * 
	 * @param key
	 *            key
	 * @param value
	 *            value
	 * @throws ServiceException
	 *             service exception
	 */
	public void putFilterValues(String key, FilterValuesResponse value) throws ServiceException {
		logger.debug("Cache put: [" + key + ", " + value + "]");
		Cache<String, FilterValuesResponse> cacheFilterValues = initCacheFilterValues();
		cacheFilterValues.put(key, value);
		logger.debug("Cached: [" + key + ", " + value + "]");

	}

	/**
	 * 
	 * @param key
	 *            key
	 * @return filter values
	 * @throws ServiceException
	 *             service exception
	 */
	public FilterValuesResponse getFilterValues(String key) throws ServiceException {
		logger.debug("Cache get: [" + key + "]");
		Cache<String, FilterValuesResponse> cacheFilterValues = initCacheFilterValues();
		FilterValuesResponse value = cacheFilterValues.get(key);
		logger.debug("Cached value: [" + key + ", " + value + "]");
		return value;
	}

	@Override
	public void finalize() throws Throwable {
		logger.debug("Release the cache resources");
		if (cacheManager != null) {
			try {
				for (String cacheName : cacheManager.getCacheNames()) {
					cacheManager.destroyCache(cacheName);
				}
			} catch (Throwable e) {
				logger.error("Error destroying the AccountingCache: " + e.getLocalizedMessage(), e);
			}

			try {
				cacheManager.close();
			} catch (Throwable e) {
				logger.error("Error closing AccountingCache manager: " + e.getLocalizedMessage(), e);
			}
		}
		super.finalize();

	}

}
