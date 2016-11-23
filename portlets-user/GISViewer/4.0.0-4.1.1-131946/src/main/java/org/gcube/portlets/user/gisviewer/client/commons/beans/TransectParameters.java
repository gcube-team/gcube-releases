/**
 * 
 */
package org.gcube.portlets.user.gisviewer.client.commons.beans;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * The Class TransectParameters.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 3, 2015
 */
public class TransectParameters implements IsSerializable{
	
	private String url;
	private String scope;
	
	/**
	 * Instantiates a new transect parameters.
	 */
	public TransectParameters(){
		
	}

	/**
	 * Instantiates a new transect parameters.
	 *
	 * @param url the url
	 * @param scope the scope
	 */
	public TransectParameters(String url, String scope) {
		this.url = url;
		this.scope = scope;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
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
	 * Sets the url.
	 *
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Sets the scope.
	 *
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
		builder.append("TransectParameters [url=");
		builder.append(url);
		builder.append(", scope=");
		builder.append(scope);
		builder.append("]");
		return builder.toString();
	}
}
