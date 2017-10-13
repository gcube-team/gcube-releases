/**
 *
 */
package org.gcube.datatransfer.resolver.gis.entity;

import java.io.Serializable;



/**
 * The Class GeoserverBaseUri.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 16, 2017
 */
public class GeoserverBaseUri implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -3321571622161066198L;
	private String baseUrl = "";
	private String scope = "";

	/**
	 * Instantiates a new geoserver base uri.
	 */
	public GeoserverBaseUri() {
	}

	/**
	 * Instantiates a new geoserver base uri.
	 *
	 * @param baseUrl the base url
	 * @param scope the scope
	 */
	public GeoserverBaseUri(String baseUrl, String scope) {
		this.baseUrl = baseUrl;
		this.scope = scope;
	}

	/**
	 * Gets the base url.
	 *
	 * @return the base url
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * Sets the base url.
	 *
	 * @param baseUrl the new base url
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * Sets the scope.
	 *
	 * @param scope the new scope
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeoserverBaseUri [baseUrl=");
		builder.append(baseUrl);
		builder.append(", scope=");
		builder.append(scope);
		builder.append("]");
		return builder.toString();
	}

}
