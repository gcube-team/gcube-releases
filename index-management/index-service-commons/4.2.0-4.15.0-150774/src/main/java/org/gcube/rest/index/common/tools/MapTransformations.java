package org.gcube.rest.index.common.tools;

import java.util.ArrayList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * 
 * @author efthimis
 *
 */
public class MapTransformations {

	public static Map<String,List<String>> flatMap(Map<String,Map<String,String>> map)
	{
		Map<String,List<String>> flatMap = new HashMap<String, List<String>>();
		Iterator<String> keySetIteratorOuter = map.keySet().iterator();
		
		while(keySetIteratorOuter.hasNext())
		{ 
			String keyOuter =  keySetIteratorOuter.next(); 
			Iterator<String> keySetIteratorInner = map.get(keyOuter).keySet().iterator();
			while(keySetIteratorInner.hasNext())
			{ 
				String keyInner =  keySetIteratorInner.next();
				String valueInner = map.get(keyOuter).get(keyInner);
				if(flatMap.containsKey(keyInner) == false)
				{
					flatMap.put(keyInner, new ArrayList<String>());
					flatMap.get(keyInner).add(valueInner);
				}else{
					flatMap.get(keyInner).add(valueInner);
				}
			}
		}
		return flatMap;
	}
}
