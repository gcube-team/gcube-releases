/**
 * 
 */
package org.gcube.portlets.user.workspace.client.workspace.folder.item.gcube;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Federico De Faveri defaveriAtisti.cnr.it
 *
 */
public class GWTDocumentMetadata implements IsSerializable {

	protected String name;

	protected String htmlUrl;
	protected String xmlUrl;
	protected String xmlRawUrl;

	protected GWTDocumentMetadata() {
	}

	public GWTDocumentMetadata(String name, String htmlUrl, String xmlRawUrl, String xmlUrl) {
		this.name = name;
		this.htmlUrl = htmlUrl;
		this.xmlUrl = xmlUrl;
		this.xmlRawUrl = xmlRawUrl;
	}

	public String getName() {
		return name;
	}

	/**
	 * @return the htmlUrl
	 */
	public String getHtmlUrl() {
		return htmlUrl;
	}

	/**
	 * @return the xmlUrl
	 */
	public String getXmlUrl() {
		return xmlUrl;
	}

	/**
	 * @return the xmlRawUrl
	 */
	public String getXmlRawUrl() {
		return xmlRawUrl;
	}

}
