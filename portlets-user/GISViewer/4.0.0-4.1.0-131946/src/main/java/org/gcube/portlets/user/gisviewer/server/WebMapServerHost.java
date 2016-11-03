/**
 *
 */
package org.gcube.portlets.user.gisviewer.server;


/**
 * The Class GeoserverBaseUri.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 28, 2016
 */
public class WebMapServerHost {

	private String host = "";
	private String scope = "";

	/**
	 * Instantiates a new geoserver base uri.
	 */
	public WebMapServerHost() {
	}

	/**
	 * Instantiates a new geoserver base uri.
	 *
	 * @param host the base url
	 * @param scope the scope
	 */
	public WebMapServerHost(String host, String scope) {
		this.host = host;
		this.scope = scope;
	}


	/**
	 * @return the host
	 */
	public String getHost() {

		return host;
	}


	/**
	 * @return the scope
	 */
	public String getScope() {

		return scope;
	}


	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {

		this.host = host;
	}


	/**
	 * @param scope the scope to set
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
		builder.append("WebMapServerHost [host=");
		builder.append(host);
		builder.append(", scope=");
		builder.append(scope);
		builder.append("]");
		return builder.toString();
	}



}
