package org.gcube.common.homelibrary.home.workspace.search.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
 
public class SearchQuery {
 
	private Set<String> types = new HashSet<String>();
 
	private Map<String, String> propertiesValues= new HashMap<>();
 
	private Set<String> hasProperties = new HashSet<>();
 
	protected SearchQuery(){}
 
	public Set<String> getTypes() {
		return Collections.unmodifiableSet(types);
	}
 
	public Map<String, String> getPropertiesValues() {
		return Collections.unmodifiableMap(propertiesValues);
	}
 
	public Set<String> getHasProperties() {
		return Collections.unmodifiableSet(hasProperties);
	}
 
	protected void addTypes(List<String> types) {
		this.types.addAll(types);
	}
 
	protected void addPropertiesValues(String key, String value) {
		this.propertiesValues.put(key, value);
	}
 
	protected void addHasProperties(List<String> hasProperties) {
		this.hasProperties.addAll(hasProperties);
	}
 
 
 
}