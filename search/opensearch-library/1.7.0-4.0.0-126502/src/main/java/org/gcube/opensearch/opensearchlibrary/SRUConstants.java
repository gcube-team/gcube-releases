package org.gcube.opensearch.opensearchlibrary;

public class SRUConstants {
	/**
	 * The namespace of OpenSearch SRU Extensions
	 */
	public static final String SRUExtensionsNS = "http://a9.com/-/opensearch/extensions/sru/2.0/";
	/**
	 * The namespace of OpenSearch SRU Extensions in URL-encoded form
	 */
	public static final String encodedSRUExtensionsNS;
	
	static {
		try {
			encodedSRUExtensionsNS = java.net.URLEncoder.encode(SRUExtensionsNS, "UTF-8");
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * The qualified name for the <code>queryType</code> parameter of SRU OpenSearch extensions
	 */
	public static final String queryTypeQname = encodedSRUExtensionsNS + ":queryType";
	/**
	 * The qualified name for the <code>query</code> parameter of SRU OpenSearch extensions
	 */
	public static final String queryQname = encodedSRUExtensionsNS + ":query";
	/**
	 * The qualified name for the <code>startRecord</code> parameter of SRU OpenSearch extensions
	 */
	public static final String startRecordQname = encodedSRUExtensionsNS + ":startRecord";
	/**
	 * The qualified name for the <code>maximumRecords</code> parameter of SRU OpenSearch extensions
	 */
	public static final String maximumRecordsQname = encodedSRUExtensionsNS + ":maximumRecords";
	/**
	 * The qualified name for the <code>recordPacking</code> parameter of SRU OpenSearch extensions
	 */
	public static final String recordPackingQname = encodedSRUExtensionsNS + ":recordPacking";
	/**
	 * The qualified name for the <code>recordSchema</code> parameter of SRU OpenSearch extensions
	 */
	public static final String recordSchemaQname = encodedSRUExtensionsNS + ":recordSchema";
	/**
	 * The qualified name for the <code>resultSetTTL</code> parameter of SRU OpenSearch extensions
	 */
	public static final String resultSetTTLQname = encodedSRUExtensionsNS + ":resultSetTTL";
	/**
	 * The qualified name for the <code>sortKeys</code> parameter of SRU OpenSearch extensions
	 */
	public static final String sortKeysQname = encodedSRUExtensionsNS + ":sortKeys";
	/**
	 * The qualified name for the <code>stylesheet</code> parameter of SRU OpenSearch extensions
	 */
	public static final String stylesheetQname = encodedSRUExtensionsNS + ":stylesheet";
	/**
	 * The qualified name for the <code>rendering</code> parameter of SRU OpenSearch extensions
	 */
	public static final String renderingQname = encodedSRUExtensionsNS + ":rendering";
	
	/**
	 * The qualified name for the <code>httpAccept</code> parameter of SRU OpenSearch extensions
	 */
	public static final String httpAcceptQname = encodedSRUExtensionsNS + ":httpAccept";
	/**
	 * The qualified name for the <code>httpAcceptCharset</code> parameter of SRU OpenSearch extensions
	 */
	public static final String httpAcceptCharsetQname = encodedSRUExtensionsNS + ":httpAcceptCharset";
	/**
	 * The qualified name for the <code>httpAcceptEncoding</code> parameter of SRU OpenSearch extensions
	 */
	public static final String httpAcceptEncodingQname = encodedSRUExtensionsNS + ":httpAcceptEncoding";
	/**
	 * The qualified name for the <code>httpAcceptLanguage</code> parameter of SRU OpenSearch extensions
	 */
	public static final String httpAcceptLanguageQname = encodedSRUExtensionsNS + ":httpAcceptLanguage";
	/**
	 * The qualified name for the <code>httpAcceptRanges</code> parameter of SRU OpenSearch extensions
	 */
	public static final String httpAcceptRangesQname = encodedSRUExtensionsNS + ":httpAcceptRanges";
	
	/**
	 * The qualified name for the <code>facetLimit</code> parameter of SRU OpenSearch extensions
	 */
	public static final String facetLimitQname = encodedSRUExtensionsNS + ":facetLimit";
	/**
	 * The qualified name for the <code>facetStart</code> parameter of SRU OpenSearch extensions
	 */
	public static final String facetStartQname = encodedSRUExtensionsNS + ":facetStart";
	/**
	 * The qualified name for the <code>facetSort</code> parameter of SRU OpenSearch extensions
	 */
	public static final String facetSortQname = encodedSRUExtensionsNS + ":facetSort";
	/**
	 * The qualified name for the <code>facetRangeField</code> parameter of SRU OpenSearch extensions
	 */
	public static final String facetRangeFieldQname = encodedSRUExtensionsNS + ":facetRangeField";
	/**
	 * The qualified name for the <code>facetLowValue</code> parameter of SRU OpenSearch extensions
	 */
	public static final String facetLowValueQname = encodedSRUExtensionsNS + ":facetLowValue";
	/**
	 * The qualified name for the <code>facetHighValue</code> parameter of SRU OpenSearch extensions
	 */
	public static final String facetHighValueQname = encodedSRUExtensionsNS + ":facetHighValue";
	/**
	 * The qualified name for the <code>facetCount</code> parameter of SRU OpenSearch extensions
	 */
	public static final String facetCountQname = encodedSRUExtensionsNS + ":facetCount";
	
}
