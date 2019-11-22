/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared.util;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 15, 2013
 *
 */
public class SearchTermValidator {
	
	/**
	 * 
	 */
	public static final String PRODUCT = " Product ";
	public static final String termOccurrence = "occurrence";
	
	/**
	 * 
	 * @param queryTerms
	 * @return the terms formatted correctly for SPQL
	 */
	public static String validateQueryTerm(String queryTerms){
		
		String validQueryTerms = "";
		
		
		if(queryTerms==null || queryTerms.isEmpty())
			return "";
		
		
		if(queryTerms.contains(",")){ //MANY TERMS
			
			String[] terms = queryTerms.split(",");

			for (String term : terms) 
				validQueryTerms+= validateTerm(term) +",";

//			if(validQueryTerms.length()>1) //REMOVE LAST ","
//				validQueryTerms = validQueryTerms.substring(0, validQueryTerms.length()-1);
			
			//SPQL ADDS THE CHAR ' IN HEAD AND TAIL 
			if(validQueryTerms.length()>3) {
				//REMOVE FIRST "'"
				validQueryTerms = validQueryTerms.substring(1);
				//REMOVE LAST "',"
				validQueryTerms = validQueryTerms.substring(0, validQueryTerms.length()-2);
			}
			
		}else{
			
//			validQueryTerms = validateTerm(queryTerms);
			
			validQueryTerms = queryTerms;
		}
		
		
		return validQueryTerms;
		
		
	}
	
	public static String validateTerm(String searchTerm){
		
		if(searchTerm==null || searchTerm.isEmpty())
			return "";
		
		searchTerm = searchTerm.trim(); //remove white space on head and tail
		
		if(!searchTerm.startsWith("'"))
			searchTerm = "'"+searchTerm;
		
		if(!searchTerm.endsWith("'"))
			searchTerm+= "'";
			
		return searchTerm;
		
		
	}
	
	public static String replaceOccurrenceTermWithProduct(String queryTerm){

		if(queryTerm==null || queryTerm.isEmpty())
			return "";
		
		queryTerm = queryTerm.trim();
		
		String originalQuery = queryTerm;
		
		int startOcc = queryTerm.toLowerCase().indexOf(termOccurrence);
		
		if(startOcc>0){
			
			int indexStart = startOcc-1;
			int indexEnd = startOcc+termOccurrence.length();
//			System.out.println("indexStart: "+indexStart);
//			System.out.println("indexEnd: "+indexEnd);
//			System.out.println("originalQuery length: "+originalQuery.length());
			
			return originalQuery.substring(0, indexStart) +PRODUCT + originalQuery.substring(indexEnd, originalQuery.length());
			
		}
		
		return originalQuery;

	}
	
	
	
	public static void main(String[] args) {
		
		String queryTerms = "solea solea', sarda, stock'";

//		System.out.println(validateQueryTerm(queryTerms));
		
		System.out.println(replaceOccurrenceTermWithProduct("SEARCH BY SN 'sarda sarda' RETURN occurr"));
		
	}

}
