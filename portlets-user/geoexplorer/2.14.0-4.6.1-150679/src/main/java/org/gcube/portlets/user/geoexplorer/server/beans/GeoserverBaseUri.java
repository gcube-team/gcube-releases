/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.server.beans;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 21, 2013
 * 
 */
public class GeoserverBaseUri {

	private String baseUrl = "";
	private String scope = "";

	/**
	 * 
	 */
	public GeoserverBaseUri() {
	}

	/**
	 * @param baseUrl
	 * @param scope
	 */
	public GeoserverBaseUri(String baseUrl, String scope) {
		this.baseUrl = baseUrl;
		this.scope = scope;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

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
