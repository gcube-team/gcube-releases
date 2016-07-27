/**
 * 
 */
package org.gcube.portlets.user.workspace.shared;

import java.io.Serializable;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Jul 3, 2015
 */
public class PublicLink implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8157172818802297440L;

	private String completeURL;
	private String shortURL;

	public PublicLink() {

	}

	/**
	 * @param completeURL
	 * @param shortURL
	 */
	public PublicLink(String completeURL, String shortURL) {
		super();
		this.completeURL = completeURL;
		this.shortURL = shortURL;
	}

	/**
	 * @return the completeURL
	 */
	public String getCompleteURL() {
		return completeURL;
	}

	/**
	 * @return the shortURL
	 */
	public String getShortURL() {
		return shortURL;
	}

	/**
	 * @param completeURL
	 *            the completeURL to set
	 */
	public void setCompleteURL(String completeURL) {
		this.completeURL = completeURL;
	}

	/**
	 * @param shortURL
	 *            the shortURL to set
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
