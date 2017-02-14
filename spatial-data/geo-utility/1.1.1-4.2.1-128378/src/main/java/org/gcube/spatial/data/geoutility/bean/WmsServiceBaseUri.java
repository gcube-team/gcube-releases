/**
 *
 */
package org.gcube.spatial.data.geoutility.bean;

import java.io.Serializable;

/**
 * The Class WmsServiceBaseUri.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 22, 2016
 */
public class WmsServiceBaseUri implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -5557778286412179122L;

	private String baseUrl = "";
	private String scope = "";

	/**
	 * Instantiates a new geoserver base uri.
	 */
	public WmsServiceBaseUri() {
	}

	/**
	 * Instantiates a new geoserver base uri.
	 *
	 * @param baseUrl the base url
	 * @param scope the scope
	 */
	public WmsServiceBaseUri(String baseUrl, String scope) {
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
