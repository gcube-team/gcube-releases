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
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class AccountingCache implements Serializable {
	private static final long serialVersionUID = -4352823042594405108L;

	private static Logger logger = LoggerFactory
			.getLogger(AccountingCache.class);

	private static final String ACCOUNTING_CACHE = "AccountingCache";
	private CacheManager cacheManager;
	private CacheConfiguration<String, SeriesResponse> cacheConfiguration;

	// private MBeanServer mBeanServer;

	public AccountingCache() throws ServiceException {
		super();
		try {

			// Resolve a cache manager
			CachingProvider cachingProvider = Caching.getCachingProvider();
			cacheManager = cachingProvider.getCacheManager();
			logger.info("AccountingCache URI: " + cacheManager.getURI());

			ResourcePoolsBuilder builder = ResourcePoolsBuilder
					.newResourcePoolsBuilder().heap(60L, MemoryUnit.MB)
					.offheap(240L, MemoryUnit.MB);

			// .disk(500L, MemoryUnit.KB)

			// .withSizeOfMaxObjectSize(1L, MemoryUnit.KB)
			cacheConfiguration = CacheConfigurationBuilder
					.newCacheConfigurationBuilder(String.class,
							SeriesResponse.class, builder)
					.withExpiry(
							Expirations
									.timeToLiveExpiration(org.ehcache.expiry.Duration
											.of(30L, TimeUnit.MINUTES)))
					.withSizeOfMaxObjectSize(50L, MemoryUnit.MB).build();

			// mBeanServer = ManagementFactory.getPlatformMBeanServer();

			/*
			 * PersistentCacheManager myCacheManager =
			 * CacheManagerBuilder.newCacheManagerBuilder()
			 * .with(CacheManagerBuilder.persistence("AccountingCache"))
			 * .build(true);
			 */

		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);

		}
	}

	private Cache<String, SeriesResponse> initCache() throws ServiceException {
		try {
			String cacheName = ACCOUNTING_CACHE;

			// create the cache
			Cache<String, SeriesResponse> cache = cacheManager.getCache(
					cacheName, String.class, SeriesResponse.class);

			if (cache == null) {
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

				cache = cacheManager.createCache(cacheName, Eh107Configuration
						.fromEhcacheCacheConfiguration(cacheConfiguration));
				cacheManager.enableManagement(cacheName, true);
				cacheManager.enableStatistics(cacheName, true);

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

			return cache;

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);

		}

	}

	public void put(String key, SeriesResponse value) throws ServiceException {
		logger.debug("Cache put: [" + key + ", " + value + "]");
		Cache<String, SeriesResponse> cache = initCache();
		cache.put(key, value);
		logger.debug("Cached: [" + key + ", " + value + "]");

	}

	public SeriesResponse get(String key) throws ServiceException {
		logger.debug("Cache get: [" + key + "]");
		Cache<String, SeriesResponse> cache = initCache();
		SeriesResponse value = cache.get(key);
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
				logger.error(
						"Error destroying the AccountingCache: "
								+ e.getLocalizedMessage(), e);
			}

			try {
				cacheManager.close();
			} catch (Throwable e) {
				logger.error(
						"Error closing AccountingCache manager: "
								+ e.getLocalizedMessage(), e);
			}
		}
		super.finalize();

	}

}
