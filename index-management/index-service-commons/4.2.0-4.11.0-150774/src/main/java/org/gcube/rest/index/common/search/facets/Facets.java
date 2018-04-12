package org.gcube.rest.index.common.search.facets;

import java.util.HashMap;
import java.util.Map;

public class Facets {

	private Map<String,Facet> facets;
	
	public Facets(){
		facets = new HashMap<>();
	}
	
	public void addFacet(String fieldName, Facet facet){
		facets.put(fieldName, facet);
	}
	
	public Map<String,Facet> getFacets(){
		return facets;
	}
	
	@Override
	public String toString(){
		return "\nFacets: "+ facets.toString();
	}
	
	
}
