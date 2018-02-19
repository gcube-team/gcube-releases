package org.gcube.vremanagement.resourcemanager.impl.resources.types;
	
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 
 * A thread-safe Map with multiple keys allowing multiple values per key
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class MultiKeysMap<K1, K2, V> implements Iterable<V> {	
	
	private List<V> values = Collections.synchronizedList(new LinkedList<V>());
	
	private List<WrappedValue> wrappedValues = Collections.synchronizedList(new LinkedList<WrappedValue>());
	
	private Map<K1, HashSet<V>> k1values = Collections.synchronizedMap(new HashMap<K1, HashSet<V>>());
	
	private Map<K2, HashSet<V>> k2values = Collections.synchronizedMap(new HashMap<K2, HashSet<V>>());
	
	
	/**
	 * {@inheritDoc}
	 */
	public Iterator<V> iterator() {
		return values.iterator();
	}
	
	/**
	 * Associates the specified value with the two keys 
	 * 
	 * @param key1 the value's primary key
	 * @param key2 the value's secondary key
	 * @param value the value
	 */
	public void put(K1 key1, K2 key2, V value) {
		WrappedValue wrappedValue = this.new WrappedValue(value);
		wrappedValue.addKey1(key1);
		wrappedValue.addKey2(key2);
		if (wrappedValues.contains(wrappedValue))
			wrappedValues.remove(wrappedValue);
		wrappedValues.add(wrappedValue);		
		if (values.contains(value)) 
			values.remove(value);
		values.add(value);
		//add the value index to k1values
		if (!k1values.containsKey(key1)) 		
			k1values.put(key1, new HashSet<V>());
		k1values.get(key1).add(value);
		//add the value index to k2values
		if (!k2values.containsKey(key2)) 		
			k2values.put(key2, new HashSet<V>());
		k2values.get(key2).add(value);			
	}
	
	/**
	 * removes the given values and its keys from the map
	 * @param value
	 */
	public void removeValue(V value) {
		values.remove(value);		
		WrappedValue wvalue = wrappedValues.get(this.wrappedIndexOf(value));
		
		k1values.get(wvalue.key1).remove(value);
		if (k1values.get(wvalue.key1).isEmpty())
			k1values.remove(wvalue.key1);
		
		k2values.get(wvalue.key2).remove(value);
		if (k2values.get(wvalue.key2).isEmpty())
			k2values.remove(wvalue.key2);
				
		wrappedValues.remove(wvalue);

	}
	
	/**
	 * Removes all the values associated to the primary key
	 * 
	 * @param key the key of type K1
	 */
	public void removeValuesByPrimaryKey(K1 key) {		
		Set<V> valuesToRemove = k1values.get(key);
		if (valuesToRemove == null)
			return;
		Iterator<V> iterator = valuesToRemove.iterator();
		while (iterator.hasNext()) {						
			V value = iterator.next();
			values.remove(value);
			if (value == null) {
				continue;
			} 				
			WrappedValue wvalue = wrappedValues.get(this.wrappedIndexOf(value));
			k2values.get(wvalue.key2).remove(value);
			if (k2values.get(wvalue.key2).isEmpty())
				k2values.remove(wvalue.key2);
			wrappedValues.remove(wvalue);						
		}
		
		k1values.remove(key);
	}
	
	
	/**
	 * Removes all the values associated to the secondary key
	 * 
	 * @param key the key of type K2
	 */
	public void removeValuesBySecondaryKey(K2 key) {
		Set<V> valuesToRemove = k2values.get(key);
		if (valuesToRemove == null)
			return;
		Iterator<V> iterator = valuesToRemove.iterator();
		while (iterator.hasNext()) {
			V value = iterator.next();
			values.remove(value);
			if (value == null) {
				return;
			} 				
			WrappedValue wvalue = wrappedValues.get(this.wrappedIndexOf(value));
			k1values.get(wvalue.key1).remove(value);
			if (k1values.get(wvalue.key1).isEmpty())
				k1values.remove(wvalue.key1);
			wrappedValues.remove(wvalue);
		}

		k2values.remove(key);
	}
	
	

	private int wrappedIndexOf(V value) {
	    int index = 0;
	    if (value==null) 
	      	return -1;	        
	    for (WrappedValue wvalue : wrappedValues) {
	         if (wvalue.equals(value))
	                return index;
	         index++;
	    }	        
	    return -1;
	}
	
	
	/**
	 * Returns the values to which this map maps the specified primary key
	 *  
	 * @param key key whose associated values are to be returned
	 * @return the values to which this map maps the specified primary key
	 */
	@SuppressWarnings("unchecked")
	public Set<V> getValuesByPrimaryKey(K1 key) {
		return k1values.get(key) == null? (Set<V>)Collections.emptySet(): k1values.get(key);
	}
	
	/**
	 * Returns the values to which this map maps the specified primary key
	 * @param key key whose associated values are to be returned
	 * @return @return the values to which this map maps the specified secondary key
	 */
	@SuppressWarnings("unchecked")
	public Set<V>  getValuesBySecondaryKey(K2 key) {		
		return k2values.get(key) == null? (Set<V>)Collections.emptySet(): k2values.get(key);
	}

	/**
	 * Returns a set view of the primary keys contained in this map
	 * 
	 * @return a set view of the primary keys contained in this map
	 */
	@SuppressWarnings("unchecked")
	public Set<K1> primaryKeySet() {		
		return k1values.keySet() == null? (Set<K1>)Collections.emptySet(): k1values.keySet();
	}
	
	/**
	 * Returns a set view of the secondary keys contained in this map
	 * 
	 * @return a set view of the secondary keys contained in this map
	 */
	@SuppressWarnings("unchecked")
	public Set<K2> secondaryKeySet() {		
		return k2values.keySet() == null? (Set<K2>)Collections.emptySet(): k2values.keySet();
	}
	
	/**
	 * Returns a collection view of the values contained in this map
	 * 
	 * @return a collection view of the values contained in this map
	 */
	public Collection<V> values() {
		return Collections.unmodifiableCollection(values);
	}
	
	/**
	 * Removes all mapping from this maps
	 */
	public void clean () {
		values.clear();
		wrappedValues.clear();
		k1values.clear();
		k2values.clear();
	}
	
	/**
	 * 
	 * Internal Map object
	 * 
	 * It maps a value V to both its primary and secondary key
	 *
	 * @author Manuele Simi (ISTI-CNR)
	 *
	 */
	class WrappedValue {
		
		V value;
		K1 key1;
		K2 key2;		
		WrappedValue(V value) { this.value = value;}		
		void addKey1(K1 key) {this.key1=key;}
		void addKey2(K2 key) {this.key2=key;}
				
		@Override
		public int hashCode() {			
			return ((value == null) ? 0 : value.hashCode());			
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() == obj.getClass()) {				
				@SuppressWarnings("unchecked")
				WrappedValue other = (WrappedValue) obj;
				if ((this.value == null) && (other.value == null)) {
					return true;
				} else if (this.value.equals(other.value)) {
					return true;
				}
			} else { //check if obj is the wrapped object				
				if (this.value.equals(obj)) {
					return true;			
				}
			}			
			return false;
		}
	}

}
