package org.gcube.security.soa3.cache;

/**
 * 
 * EHCache based cache wrapper
 * 
 * @author Ciro Formisano (ENG)
 *
 * @param <K>
 * @param <V>
 */
public interface CacheWrapper<K, V>
{
	 void put(K key, V value);

	  V get(K key);


}
