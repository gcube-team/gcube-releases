/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author ceras
 *
 */
public class BufferedHashMap<K, V> {

	private int bufferSize;
	List<K> bufferQueue = new ArrayList<K>();
	HashMap<K, V> map = new HashMap<K, V>();

	
	public BufferedHashMap(int buffersSize) {
		this.bufferSize = buffersSize;
	}	

	public V put(K key, V value) {
		System.out.println("BufferedHashMap PUT new key " + key.toString());
		
		// check if the key doesn't exists in the map
		if (map.get(key)==null) {
			// check if there's overflow
			if (map.size()==this.bufferSize) {		
				// remove the head element
				K keyToRemove = bufferQueue.remove(0);
				System.out.println("	Overflow, delete element "+keyToRemove.toString());
				map.remove(keyToRemove);
			}
		} else
			// the key moves to the tail
			bufferQueue.remove(key);
		
		bufferQueue.add(key);
		
		return map.put(key, value);
	};	
	
	public V get(K key) {
		return map.get(key);
	}
	
	public V remove(Object key) {
		V value = map.remove(key);
		if (value!=null)
			bufferQueue.remove(key);
			
		return value;
	}

	public Set<Entry<K, V>> entrySet() {
		return map.entrySet();
	}
	
	public HashMap<K, V> getMap() {
		return map;
	}
	
	public Collection<V> values() {
		return map.values();
	}
}
