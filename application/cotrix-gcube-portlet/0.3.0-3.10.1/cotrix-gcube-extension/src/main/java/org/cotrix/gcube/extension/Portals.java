/**
 * 
 */
package org.cotrix.gcube.extension;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author "Federico De Faveri federico.defaveri@fao.org"
 *
 */
public class Portals {
	
	private Map<String, Set<String>> cache = new HashMap<String, Set<String>>();
	
	public void add(String scope, String url) {
		Set<String> urls = cache.get(scope);
		
		if (urls == null) {
			urls = new HashSet<>();
			cache.put(scope, urls);
		}
		
		urls.add(url);
	}
	
	public Set<String> in(String scope) {
		Set<String> urls = cache.get(scope);
		return urls==null?Collections.<String>emptySet():Collections.unmodifiableSet(urls);
	}

}
