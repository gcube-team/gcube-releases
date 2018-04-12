package org.gcube.rest.index.common.search.facets;

import java.util.ArrayList;
import java.util.List;

public class Facet {

	List<Pair> facetPairs;
	
	public Facet(){
		facetPairs = new ArrayList<Pair>();
	}
	
	public void addPair(String term, long occurrences){
		facetPairs.add(new Pair(term, occurrences));
	}
	
	
	public List<Pair> getFacetPairs() {
		return facetPairs;
	}
	
	@Override
	public String toString() {
		return facetPairs.toString();
	}
	
	
	class Pair{
		
		private String term;
		private long occurrences;
		
		public Pair(String term, long occurrences){
			this.term = term;
			this.occurrences = occurrences;
		}

		public String getTerm() {
			return term;
		}

		public long getOccurrences() {
			return occurrences;
		}
		
		@Override
		public String toString() {
			return "\tterm: "+term +"\toccurrences: "+occurrences;
		}
		
	}
	
}
