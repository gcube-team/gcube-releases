package org.gcube.data_catalogue.grsf_publish_ws.utils.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;

/**
 * Cache implementation.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class  CacheImpl <K, V> implements CacheInterface<K, V> {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CacheImpl.class);

	/**
	 * The hashmap
	 */
	private Map<K, CacheValueBean<V>> cache;

	/**
	 * Cache entry expires after EXPIRED_AFTER ms
	 */
	private long ttl;

	public CacheImpl(long timeout){
		ttl = timeout;
		cache = new ConcurrentHashMap<>();
	}

	@Override
	public V get(K key) {

		if(cache.containsKey(key)){
			CacheValueBean<V> bean = cache.get(key);
			if(CacheUtilities.expired(bean.getTTL(), ttl)){
				cache.remove(key);
				logger.debug("Amount of space in the infrastructure used expired for key " + key + ", returning null");
			}
			else 
				return bean.getValue();
		}
		return null;
	}

	@Override
	public boolean insert(K key, V obj) {
		CacheValueBean<V> newBean = new CacheValueBean<V>(obj, System.currentTimeMillis());
		cache.put(key, newBean);
		return true;
	}
}
