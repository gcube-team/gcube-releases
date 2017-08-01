package org.gcube.data_catalogue.grsf_publish_ws.utils.cache;

/**
 * Generic interface
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 * @param <K> the key type
 * @param <V> the value type
 */
public interface CacheInterface <K,V>{

	/**
	 * Retrieve a value V from the cache
	 * @param key
	 * @return
	 */
	public V get(K key);
	
	/**
	 * Insert an object V with key K into the cache
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean insert(K key, V value);
	
}
