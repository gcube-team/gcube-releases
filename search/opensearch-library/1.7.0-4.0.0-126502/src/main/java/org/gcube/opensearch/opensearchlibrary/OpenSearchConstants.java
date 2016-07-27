package org.gcube.opensearch.opensearchlibrary;

import java.net.URLEncoder;

/**
 * OpenSearch and library-related constants such as namespaces and qualified OpenSearch standard parameters
 * as used in the library
 * 
 * @author gerasimos.farantatos
 *
 */
public class OpenSearchConstants {
	
	/**
	 * The standard OpenSearch namespace
	 */
	public static final String OpenSearchNS = "http://a9.com/-/spec/opensearch/1.1/";
	/**
	 * The standard OpenSearch namespace in URL-encoded form
	 */
	public static final String encodedOpenSearchNS;
	
	public static final String OpenSearchConfigNS = "config";
	
	static {
		try {
			encodedOpenSearchNS = URLEncoder.encode(OpenSearchNS, "UTF-8");
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * The qualified name for the <code>searchTerms</code> standard OpenSearch parameter
	 */
	public static final String searchTermsQName = encodedOpenSearchNS + ":searchTerms";
	/**
	 * The qualified name for the <code>startPage</code> standard OpenSearch parameter
	 */
	public static final String startPageQName = encodedOpenSearchNS + ":startPage";
	/**
	 * The qualified name for the <code>startIndex</code> standard OpenSearch parameter
	 */
	public static final String startIndexQName = encodedOpenSearchNS + ":startIndex";
	/**
	 * The qualified name for the <code>count</code> standard OpenSearch parameter
	 */
	public static final String countQName = encodedOpenSearchNS + ":count";
	/**
	 * The qualified name for the <code>language</code> standard OpenSearch parameter
	 */
	public static final String languageQName = encodedOpenSearchNS + ":language";
	/**
	 * The qualified name for the <code>inputEncoding</code> standard OpenSearch parameter
	 */
	public static final String inputEncodingQName = encodedOpenSearchNS + ":inputEncoding";
	/**
	 * The qualified name for the <code>outputEncoding</code> standard OpenSearch parameter
	 */
	public static final String outputEncodingQName = encodedOpenSearchNS + ":outputEncoding";
	
	public static final String configNumOfResultsQName = OpenSearchConfigNS + ":numOfResults";
	
	public static final String configSequentialResultsQName = OpenSearchConfigNS + ":sequentialResults";
	/**
	 * The name of the unique identifier field
	 */
	public static final String objectIdFieldName = "ObjectID";
	
	/**
	 * Result estimation events
	 */
	public static final String RESULTSNO_EVENT = "resultsNumber";
	public static final String RESULTSNOFINAL_EVENT = "resultsNumberFinal";
	
	
	public static enum SupportedRelations {
		eq {
			public String toString() {
				return "=";
			}
		},
		exact {
			public String toString() {
				return "==";
			}
		}, any, all
	};
	
	public static final String EQUALS = "=";
}
