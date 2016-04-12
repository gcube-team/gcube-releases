package org.gcube.opensearch.opensearchlibrary;

public class TimeConstants {
	/**
	 * The namespace of OpenSearch Time Extensions
	 */
	public static final String TimeExtensionsNS = "http://a9.com/-/opensearch/extensions/time/1.0/";
	/**
	 * The namespace of OpenSearch Time Extensions in URL-encoded form
	 */
	public static final String encodedTimeExtensionsNS;
	
	static {
		try {
			encodedTimeExtensionsNS = java.net.URLEncoder.encode(TimeExtensionsNS, "UTF-8");
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * The qualified name for the <code>start</code> parameter of Time OpenSearch extensions
	 */
	public static final String startQname = encodedTimeExtensionsNS + ":start";
	/**
	 * The qualified name for the <code>end</code> parameter of Time OpenSearch extensions
	 */
	public static final String endQname = encodedTimeExtensionsNS + ":end";
	
}
