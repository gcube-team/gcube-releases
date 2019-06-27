/**
 *
 */
package org.gcube.datatransfer.resolver.catalogue.resource;

import java.io.Serializable;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 23, 2017
 */
public class GatewayCKANCatalogueReference implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String privatePortletURL;
	private String publicPortletURL;
	private String scope;
	private String ckanURL;

	/**
	 *
	 */
	public GatewayCKANCatalogueReference() {

	}

	/**
	 * @param privatePortletURL
	 * @param publicPortletURL
	 * @param ckanURL
	 * @param scope
	 */
	public GatewayCKANCatalogueReference(String scope,
		String privatePortletURL, String publicPortletURL, String ckanURL) {
		this.scope = scope;
		this.privatePortletURL = privatePortletURL;
		this.publicPortletURL = publicPortletURL;
		this.ckanURL = ckanURL;
	}


	/**
	 * @return the ckanURL
	 */
	public String getCkanURL() {

		return ckanURL;
	}


	/**
	 * @param ckanURL the ckanURL to set
	 */
	public void setCkanURL(String ckanURL) {

		this.ckanURL = ckanURL;
	}

	/**
	 * @return the privatePortletURL
	 */
	public String getPrivatePortletURL() {

		return privatePortletURL;
	}


	/**
	 * @return the publicPortletURL
	 */
	public String getPublicPortletURL() {

		return publicPortletURL;
	}


	/**
	 * @return the scope
	 */
	public String getScope() {

		return scope;
	}


	/**
	 * @param privatePortletURL the privatePortletURL to set
	 */
	public void setPrivatePortletURL(String privatePortletURL) {

		this.privatePortletURL = privatePortletURL;
	}


	/**
	 * @param publicPortletURL the publicPortletURL to set
	 */
	public void setPublicPortletURL(String publicPortletURL) {

		this.publicPortletURL = publicPortletURL;
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
		builder.append("GatewayCatalogueReference [privatePortletURL=");
		builder.append(privatePortletURL);
		builder.append(", publicPortletURL=");
		builder.append(publicPortletURL);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", ckanURL=");
		builder.append(ckanURL);
		builder.append("]");
		return builder.toString();
	}


}
