package org.gcube.rest.index.service.elements;

import java.util.List;

import org.elasticsearch.search.aggregations.bucket.global.GlobalBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;

public class IndexFacet {
	
	List<TermsBuilder> listOfAggregations;
	GlobalBuilder globalbuilder;
	boolean noneFacetType;
	
	
	public IndexFacet() {
		super();
	}
	
	public List<TermsBuilder> getListOfAggregations() {
		return listOfAggregations;
	}

	public void setListOfAggregations(List<TermsBuilder> listOfAggregations) {
		this.listOfAggregations = listOfAggregations;
	}
	public GlobalBuilder getGlobalbuilder() {
		return globalbuilder;
	}
	public void setGlobalbuilder(GlobalBuilder globalbuilder) {
		this.globalbuilder = globalbuilder;
	}
	public boolean isNoneFacetType() {
		return noneFacetType;
	}
	public void setNoneFacetType(boolean noneFacetType) {
		this.noneFacetType = noneFacetType;
	}
	
	

}
