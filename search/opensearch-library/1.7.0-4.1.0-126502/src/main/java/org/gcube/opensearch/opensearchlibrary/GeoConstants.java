package org.gcube.opensearch.opensearchlibrary;

public class GeoConstants {
	/**
	 * The namespace of OpenSearch Geo Extensions
	 */
	public static final String GeoExtensionsNS = "http://a9.com/-/opensearch/extensions/geo/1.0/";
	/**
	 * The namespace of OpenSearch Geo Extensions in URL-encoded form
	 */
	public static final String encodedGeoExtensionsNS;
	
	static {
		try {
			encodedGeoExtensionsNS = java.net.URLEncoder.encode(GeoExtensionsNS, "UTF-8");
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * The qualified name for the <code>box</code> parameter of Geo OpenSearch extensions
	 */
	public static final String boxQname = encodedGeoExtensionsNS + ":box";
	/**
	 * The qualified name for the <code>polygon</code> parameter of Geo OpenSearch extensions
	 */
	public static final String polygonQname = encodedGeoExtensionsNS + ":polygon";
	/**
	 * The qualified name for the <code>geometry</code> parameter of Geo OpenSearch extensions
	 */
	public static final String geometryQname = encodedGeoExtensionsNS + ":geometry";
	/**
	 * The qualified name for the <code>locationString</code> parameter of Geo OpenSearch extensions
	 */
	public static final String locationStringQname = encodedGeoExtensionsNS + ":locationString";
	/**
	 * The qualified name for the <code>name</code> parameter of Geo OpenSearch extensions
	 */
	public static final String nameQname = encodedGeoExtensionsNS + ":name";
	/**
	 * The qualified name for the <code>uid</code> parameter of Geo OpenSearch extensions
	 */
	public static final String uidQname = encodedGeoExtensionsNS + ":uid";
	/**
	 * The qualified name for the <code>lat</code> parameter of Geo OpenSearch extensions
	 */
	public static final String latQname = encodedGeoExtensionsNS + ":lat";
	/**
	 * The qualified name for the <code>lon</code> parameter of Geo OpenSearch extensions
	 */
	public static final String lonQname = encodedGeoExtensionsNS + ":lon";
	/**
	 * The qualified name for the <code>radius</code> parameter of Geo OpenSearch extensions
	 */
	public static final String radiusQname = encodedGeoExtensionsNS + ":radius";
	
	
	
}
