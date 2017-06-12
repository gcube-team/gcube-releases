/**
 * 
 */
package org.gcube.vremanagement.executor.utils;

import java.util.Map;
import java.util.Set;


/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class MapCompare<M extends Map<K, V>, K, V> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int compareMaps(Map<K, V> map1, Map<K, V> map2){
		int compareResult = 0;
		
		Set<K> map1Set = map1.keySet();
		Set<K> map2Set = map2.keySet();
		
		compareResult = new Integer(map1Set.size()).compareTo(new Integer(map2Set.size()));
		if(compareResult != 0){
			return compareResult;
		}
		
		if(!map1Set.containsAll(map2Set) || !map2Set.containsAll(map1Set)){
			return -1;
		}
		
		for(K key : map1Set){
			V v1 = map1.get(key);
			V v2 = map2.get(key);
			if(!(v1 instanceof Comparable<?>)){
				return -1;
			}
			
			if(!(v2 instanceof Comparable<?>)){
				return -1;
			}
			
			compareResult = ((Comparable) v1).compareTo((Comparable<?>) v2);
			if(compareResult != 0){
				return compareResult;
			}
		}
		
		return 0;
	}

}


