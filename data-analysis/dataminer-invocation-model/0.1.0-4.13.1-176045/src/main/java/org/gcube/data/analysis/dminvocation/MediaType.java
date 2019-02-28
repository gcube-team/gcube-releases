/**
 *
 */
package org.gcube.data.analysis.dminvocation;


/**
 * The Enum MediaType.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Dec 10, 2018
 */
public enum MediaType {

	ApplicationJSON("application/json"),
	ApplicationXML("application/xml");

	private String mimeType;

	/**
	 * Instantiates a new media type.
	 *
	 * @param mimeType the mime type
	 */
	MediaType(String mimeType){
		this.mimeType = mimeType;
	}

	/**
	 * Gets the mime type.
	 *
	 * @return the mimeType
	 */
	public String getMimeType() {

		return mimeType;
	}
}
