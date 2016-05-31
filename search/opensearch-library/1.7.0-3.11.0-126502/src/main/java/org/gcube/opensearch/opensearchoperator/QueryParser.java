package org.gcube.opensearch.opensearchoperator;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gcube.opensearch.opensearchlibrary.OpenSearchConstants;

/**
 * An OpenSearch query parser, responsible for parsing and processing an OpenSearch query and a set of fixed parameters.
 * After processing, a mapping of parameter names and values both for query and fixed parameters, as well as a set of
 * all namespaces present in the parameters are available
 * 
 * @author gerasimos.farantatos, NKUA
 *
 */
public class QueryParser {
	
	private Logger logger = LoggerFactory.getLogger(QueryParser.class.getName());
	private String terms;
	private Map<String, String> params;
	private String fixedTerms;
	private Map<String, String> fixedParamsMap;
	private Set<String> namespaces;
	private Pattern queryPattern = Pattern.compile("[^=]*=\"[^\"]*\"");
	private Pattern paramPattern = Pattern.compile("[^:]*:[^=]*=");
	private Pattern valuePattern = Pattern.compile("\"[^\"]*\"$");
	
	/**
	 * Creates a new instance and processes the query and fixed parameter strings
	 * 
	 * @param queryString The OpenSearch operator query string
	 * @param fixedParams The fixed parameters array
	 * @throws UnsupportedEncodingException If the encoding used to decode the encoded namespace prefixes is not supported
	 */
	public QueryParser(String queryString, String[] fixedParams) throws UnsupportedEncodingException {
		terms = "";
		params = new HashMap<String, String>();
		namespaces = new HashSet<String>();
		
		List<String> paramList = new ArrayList<String>();
		Matcher queryMatcher = queryPattern.matcher(queryString);
		while(queryMatcher.find())
			paramList.add(queryMatcher.group().trim());
		
		logger.info("queryString : " + queryString);
		logger.info("fixedParams : " + Arrays.toString(fixedParams));
		 
		
		
		for(String param : paramList) {
			Matcher paramMatcher = paramPattern.matcher(param);
			List<String> keyValue = new ArrayList<String>();
			while(paramMatcher.find()) {
				String tmp = paramMatcher.group().trim();
				keyValue.add(tmp.substring(0, tmp.length()-1));
			}
			
			logger.info("     keyValue1 : " + keyValue);
			logger.info("     param     : " + param);
			
			if(keyValue.size() != 1) {
				if(!param.trim().equals(""))
					logger.warn("Ignored malformed query parameter: " + param);
				continue;
			}
			
			Matcher valueMatcher = valuePattern.matcher(param);
			while(valueMatcher.find())
				keyValue.add(valueMatcher.group().trim());
			
			logger.info("     keyValue2 : " + keyValue);
			
			if(keyValue.size() != 2) {
				if(!param.trim().equals(""))
					logger.warn("Ignored malformed query parameter: " + param);
				continue;
			}
			
			if(keyValue.get(1).charAt(0) != '\"'|| keyValue.get(1).charAt(keyValue.get(1).length()-1) != '\"') {
				logger.warn("In parameter: " + param + ": Parameter value not enclosed in double quotes. Ignoring parameter");
				continue;
			}
			if(keyValue.get(0).compareTo(OpenSearchConstants.searchTermsQName) != 0)
				params.put(keyValue.get(0), keyValue.get(1).substring(1, keyValue.get(1).length()-1));
			else
				terms = keyValue.get(1).substring(1, keyValue.get(1).length()-1);
			namespaces.add(URLDecoder.decode(keyValue.get(0).substring(0, keyValue.get(0).indexOf(':')), "UTF-8"));
				
		}
		
		fixedParamsMap = new HashMap<String, String>();
		fixedTerms = "";
		for(String fixedParam: fixedParams) {
			Matcher paramMatcher = paramPattern.matcher(fixedParam);
			List<String> keyValue = new ArrayList<String>();
			while(paramMatcher.find()) {
				String tmp = paramMatcher.group().trim();
				keyValue.add(tmp.substring(0, tmp.length()-1));
			}
			
			logger.info("     keyValue3 : " + keyValue);
			logger.info("     fixedParam     : " + fixedParam);
			
			if(keyValue.size() != 1) {
				if(!fixedParam.trim().equals(""))
					logger.warn("Ignored malformed fixed parameter: " + fixedParam);
				continue;
			}
			
			Matcher valueMatcher = valuePattern.matcher(fixedParam);
			while(valueMatcher.find())
				keyValue.add(valueMatcher.group().trim());
			
			logger.info("     keyValue4 : " + keyValue);
			logger.info("     fixedParam     : " + fixedParam);
			
			if(keyValue.size() != 2) {
				if(!fixedParam.trim().equals(""))
					logger.warn("Ignored malformed fixed parameter: " + fixedParam);
				continue;
			}
			
			if(keyValue.get(0).compareTo(OpenSearchConstants.searchTermsQName) != 0)
				fixedParamsMap.put(keyValue.get(0), keyValue.get(1).substring(1, keyValue.get(1).length()-1));
			else
				fixedTerms = keyValue.get(1).substring(1, keyValue.get(1).length()-1);
			namespaces.add(URLDecoder.decode(keyValue.get(0).substring(0, keyValue.get(0).indexOf(':')), "UTF-8"));
		}
	}
	
	/**
	 * Retrieves the search terms, as specified by the searchTerms standard OpenSearch parameter
	 * 
	 * @return The search terms
	 */
	public String getTerms() {
		return terms;
	}
	
	/**
	 * Retrieves all parameter name-value mappings
	 * 
	 * @return All parameters and their corresponding values
	 */
	public Map<String, String> getParams() {
		return params;
	}
	
	/**
	 * Retrieves the fixed parameter corresponding to the searchTerms standard OpenSearch parameter 
	 * 
	 * @return The fixed search terms
	 */
	public String getFixedTerms() {
		return fixedTerms;
	}
	
	/**
	 * Retrieves all fixed parameter name-value mappings
	 * 
	 * @return All fixed parameters and their corresponding values
	 */
	public Map<String, String> getFixedParamsMap() {
		return fixedParamsMap;
	}
	
	/**
	 * Retrieves all namespaces present in the OpenSearch query and the fixed parameters
	 * 
	 * @return The namespaces found
	 */
	public Set<String> getNamespaces() {
		return namespaces;
	}
}
