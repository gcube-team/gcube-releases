/**
 * 
 */
package org.gcube.data.spd.obisplugin.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class Cache<K,V> {
	
	protected int limit;
	protected Map<K, V> cache;
	
	protected boolean enableStatistics;
	protected long hints;
	protected long requests;
	protected long removed;
	
	@SuppressWarnings("serial")
	public Cache(final int limit)
	{
		this.limit = limit;
		this.hints = 0;
		
		this.cache = new LinkedHashMap<K, V>() {

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected boolean removeEldestEntry(Entry<K, V> eldest) {
				boolean remove = size() > limit;
				if (remove) removed++;
				return remove;
			}
			
		};
	}
	
	/**
	 * @return the enableStatistics
	 */
	public boolean isEnableStatistics() {
		return enableStatistics;
	}

	/**
	 * @param enableStatistics the enableStatistics to set
	 */
	public void setEnableStatistics(boolean enableStatistics) {
		this.enableStatistics = enableStatistics;
	}

	/**
	 * @return the requests
	 */
	public long getRequests() {
		return requests;
	}

	/**
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * @return the hints
	 */
	public long getHints() {
		return hints;
	}

	public V get(K key)
	{
		V value = cache.get(key);
		if (enableStatistics) {
			requests++;
			if (value!=null) hints++;
		}
		return value;
	}
	
	public void put(K key, V value)
	{
		cache.put(key, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Cache [limit=");
		builder.append(limit);
		builder.append(", enableStatistics=");
		builder.append(enableStatistics);
		builder.append(", hints=");
		builder.append(hints);
		builder.append(", requests=");
		builder.append(requests);
		builder.append(", removed=");
		builder.append(removed);
		builder.append("]");
		return builder.toString();
	}
}
