package gr.uoa.di.madgik.workflow.adaptor.search.utils;



/**
 * This class wraps the functionality needed to process 
 * a gCQL query and produce data that can be used by a 
 * resource that is implemented in a specific technology
 * @author bill
 *
 */
abstract public class GcqlProcessor {
	
	protected RRadaptor adaptor = null;
	
	
	abstract public GcqlQueryContainer processQuery(String gCQLQuery, RRadaptor adaptor) throws Exception;

	
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
