package org.gcube.indexmanagement.lucenewrapper;

import java.util.ArrayList;
import java.util.HashMap;

public class QuerySnippetTermsPair {
	
	/**
	 * package visibility
	 */
	public String query;	
	HashMap<String, ArrayList<String>> snippetTerms = new HashMap<String, ArrayList<String>>();
	HashMap<String, ArrayList<String>> snippetNotTerms = new HashMap<String, ArrayList<String>>();

}
