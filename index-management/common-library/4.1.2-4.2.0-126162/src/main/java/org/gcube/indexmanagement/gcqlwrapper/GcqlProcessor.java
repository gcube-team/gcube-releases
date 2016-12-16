package org.gcube.indexmanagement.gcqlwrapper;

import java.util.ArrayList;

import org.gcube.indexmanagement.resourceregistry.RRadaptor;

/**
 * This class wraps the functionality needed to process 
 * a gCQL query and produce data that can be used by a 
 * resource that is implemented in a specific technology
 * @author bill
 *
 */
abstract public class GcqlProcessor {
	
	protected ArrayList<String> presentableFields = new ArrayList<String>();
	protected ArrayList<String> searchableFields = new ArrayList<String>();
	protected RRadaptor adaptor = null;
	
	
	abstract public GcqlQueryContainer processQuery(ArrayList<String> presentableFields, ArrayList<String> searchableFields, String gCQLQuery, RRadaptor adaptor) throws Exception;

	protected String findPresentable(String proj) {
		for(String field : presentableFields)
			if(field.equalsIgnoreCase(proj))
				return field;
		
		return null;
	}
	
	protected static String[] splitTerms(String term) {
		//remove the first " (if any)
		if(term.charAt(0) == '"')
			term = term.substring(1);
		//remove the last " (if any)
		if(term.charAt(term.length()-1) == '"')
			term = term.substring(0, term.length()-1);
		return term.trim().split("\\s+");
	}
	
	protected static String removeQuotes(String term) {
		//remove the first " (if any)
		if(term.charAt(0) == '"')
			term = term.substring(1);
		//remove the last " (if any)
		if(term.charAt(term.length()-1) == '"')
			term = term.substring(0, term.length()-1);
		return term;
	}
}
