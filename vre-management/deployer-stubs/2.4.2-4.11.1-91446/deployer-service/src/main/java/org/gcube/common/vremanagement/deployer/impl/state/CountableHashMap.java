/**
 * 
 */
package org.gcube.common.vremanagement.deployer.impl.state;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * An extension of the standard {@link HashMap} handling multiple instances of the same key, all mapped on the same value.
 * This extension behaves as follows:
 * <ul>
 * <li> when {@link #put(Object, Object)} or a {@link #putAll(Map)} are invoked, if the given key exists, 
 * a counter for the key is increased, otherwise the element is added to the <em>CountableHashMap</em> object and a new counter is created.
 * <li> when {@link #remove(Object)} is invoked, the key is removed only if the related counter value is 1.
 * </ul>
 * 
 * @param <K> the key type
 * @param <V> the value type
 * 
 * @author Manuele Simi (ISTI-CNR)
 * @see HashMap
 * @see	Map
 *
 */
public class CountableHashMap<K,V> extends HashMap<K,V> implements Serializable {

	/**
	 * Serialization number
	 */
	private static final long serialVersionUID = -4631590796792866098L;

	/** key counter */
	private Map<K, Integer> keycounter = new HashMap<K, Integer>();
	
	/**
	 * {@inheritDoc}
	 */
	public CountableHashMap() {}

	/**
	 * {@inheritDoc}
	 */
	public CountableHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * {@inheritDoc}
	 */
	public CountableHashMap(Map<K,V> m) {
		super(m);
	}

	/**
	 * {@inheritDoc}
	 */
	public CountableHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		this.keycounter.clear();
		super.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V put(K key, V value) {
		V v;
		if (this.keycounter.containsKey(key)){
			Integer i = this.keycounter.get(key);
			i++;
			v = super.get(key);
		} else {
			v = super.put(key, value);
			this.keycounter.put(key, 1);
		}
		return v;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		int numKeysToBeAdded = m.size();
        if (numKeysToBeAdded == 0)
            return;
        for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry<? extends K, ? extends V> e = i.next();
            put(e.getKey(), e.getValue());
        }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V remove(Object key) {
		V v;
		if (this.keycounter.containsKey(key)){
			Integer i = this.keycounter.get(key);
			if (1 == i) {
				v = super.remove(key);
				this.keycounter.remove(key);
			} else {
				i--;
				v = super.get(key);
			}
		} else 
			v = super.remove(key);
		return v;
	}

	
	/**
	 * Removes the mapping for this key from this <em> CountableHashMap</em> if present
	 * 
	 * @param key key whose mapping is to be removed from the map
	 * @return the remaining number of instances of the key still in the map 
	 */
	public int countableRemove(Object key) {
		this.remove(key);
		if (this.keycounter.containsKey(key))
			return this.keycounter.get(key);
		else 
			return 0;
	}
	
	/**
	 * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for this key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key 
	 * @return the number of instances of the key in the map
	 */
	public int countablePut(K key, V value) {
		this.put(key, value);
		return this.keycounter.get(key);		
	}
	
	/**
     * Copies all of the mappings from the specified map to this map
     * These mappings will replace any mappings that
     * this map had for any of the keys currently in the specified map.
     *
     * @param m mappings to be stored in this map.
     * @return the number of instances of each key in the map
     * @throws NullPointerException if the specified map is null.
     */
	public Map<? extends K, Integer> countablePutAll(Map<? extends K, ? extends V> m) {
		 this.putAll(m);
		 Map<K, Integer> counters = new HashMap <K, Integer>();
		 for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m.entrySet().iterator(); i.hasNext(); ) {
	            Map.Entry<? extends K, ? extends V> e = i.next();
	            counters.put(e.getKey(), this.keycounter.get(e.getKey()));
	     }
		 return counters;		
	}
	
	/**
	 * Gets the key counter of the given element
	 * 
	 * @param key the key to count
	 * @return the key counter
	 */
	public int getCounter(Object key) {
		if (this.keycounter.containsKey(key))
			return this.keycounter.get(key);
		else 
			return 0;
	}
	
}
