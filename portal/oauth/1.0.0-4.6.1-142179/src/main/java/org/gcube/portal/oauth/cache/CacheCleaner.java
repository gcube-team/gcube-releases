package org.gcube.portal.oauth.cache;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.LoggerFactory;


/**
 * This thread cleans a cache by removing expired entries.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CacheCleaner extends Thread {

	private Map<String, CacheBean> cacheReference;
	private static final int CHECK_AFTER_MS = 1000 * 60 * 10;

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CacheCleaner.class);

	/**
	 * Build a cleaner thread.
	 * @param cache
	 */
	public CacheCleaner(Map<String, CacheBean> cache) {
		this.cacheReference = cache;
	}

	@Override
	public void run() {

		while (!isInterrupted()) {

			try {

				sleep(CHECK_AFTER_MS);
				logger.info("Going to clean up cache and old codes [" + new Date() + "]");
				
				int removedEntries = 0;
				
				Iterator<Entry<String, CacheBean>> iterator = cacheReference.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<java.lang.String, org.gcube.portal.oauth.cache.CacheBean> entry = (Map.Entry<java.lang.String, org.gcube.portal.oauth.cache.CacheBean>) iterator
							.next();
					if(CacheBean.isExpired(entry.getValue())){
						logger.debug("Removing entry " + entry.getValue());
						removedEntries ++;
						iterator.remove();
					}
				}

				logger.info("Going to sleep. Number of removed entries is " + removedEntries + " [" + new Date() + "]");

			} catch (InterruptedException e) {
				logger.warn("Exception was " + e.getMessage());
				continue;
			}

		}
	}

}
