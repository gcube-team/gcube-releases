package org.gcube.opensearch.opensearchdatasource.processor;

import java.util.ArrayList;
import java.util.List;

import search.library.util.cql.query.tree.GCQLNode;

/**
 * This class wraps the functionality needed to process 
 * a gCQL query and produce data that can be used by a 
 * resource that is implemented in a specific technology
 *
 */
abstract public class GcqlProcessor {
	
	protected List<String> presentableFields = new ArrayList<String>();
	protected List<String> searchableFields = new ArrayList<String>();
	
	abstract public GCQLNode parseQuery(String gCQLQuery) throws Exception;
	abstract public GcqlQueryContainer processQuery(List<String> presentableFields, List<String> searchableFields) throws Exception;

	protected String findPresentable(String proj) {
		for(String field : presentableFields)
			if(field.equalsIgnoreCase(proj))
				return field;
		
		return null;
	}
	
	public static String[] splitTerms(String term) {
		//remove the first " (if any)
		if(term.charAt(0) == '"')
			term = term.substring(1);
		//remove the last " (if any)
		if(term.charAt(term.length()-1) == '"')
			term = term.substring(0, term.length()-1);
		return term.trim().split("\\s+");
	}
	
	public static String removeQuotes(String term) {
		//remove the first " (if any)
		if(term.charAt(0) == '"')
			term = term.substring(1);
		//remove the last " (if any)
		if(term.charAt(term.length()-1) == '"')
			term = term.substring(0, term.length()-1);
		return term;
	}
}
