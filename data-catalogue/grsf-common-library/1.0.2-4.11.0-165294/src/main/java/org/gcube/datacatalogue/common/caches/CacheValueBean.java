package org.gcube.datacatalogue.common.caches;

/**
 * A bean object to be used as value within the cache. It contains a TTL value too.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 * @param <V> the value type
 */
public class CacheValueBean <V>{

	private V value;
	private long TTL;
	
	/**
	 * @param value
	 * @param TTL 
	 */
	public CacheValueBean(V value, long ttl) {
		super();
		this.value = value;
		this.TTL = ttl;
	}
	public V getValue() {
		return value;
	}
	public void setValue(V value) {
		this.value = value;
	}
	public long getTTL() {
		return TTL;
	}
	public void setTTL(long ttl) {
		this.TTL = ttl;
	}
	@Override
	public String toString() {
		return "CacheValueBean [value=" + value + ", TTL=" + TTL + "]";
	}
}
