/**
 *
 */
package org.gcube.portlets.user.workspace.shared;

import java.io.Serializable;


/**
 * The Class PublicLink.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Sep 13, 2016
 */
public class PublicLink implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -8157172818802297440L;

	private String completeURL;
	private String shortURL;

	/**
	 * Instantiates a new public link.
	 */
	public PublicLink() {

	}

	/**
	 * Instantiates a new public link.
	 *
	 * @param completeURL the complete url
	 * @param shortURL the short url
	 */
	public PublicLink(String completeURL, String shortURL) {
		super();
		this.completeURL = completeURL;
		this.shortURL = shortURL;
	}

	/**
	 * Gets the complete url.
	 *
	 * @return the completeURL
	 */
	public String getCompleteURL() {
		return completeURL;
	}

	/**
	 * Gets the short url.
	 *
	 * @return the shortURL
	 */
	public String getShortURL() {
		return shortURL;
	}

	/**
	 * Sets the complete url.
	 *
	 * @param completeURL            the completeURL to set
	 */
	public void setCompleteURL(String completeURL) {
		this.completeURL = completeURL;
	}

	/**
	 * Sets the short url.
	 *
	 * @param shortURL            the shortURL to set
	 */
	public void setShortURL(String shortURL) {
		this.shortURL = shortURL;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PublicLink [completeURL=");
		builder.append(completeURL);
		builder.append(", shortURL=");
		builder.append(shortURL);
		builder.append("]");
		return builder.toString();
	}
}
