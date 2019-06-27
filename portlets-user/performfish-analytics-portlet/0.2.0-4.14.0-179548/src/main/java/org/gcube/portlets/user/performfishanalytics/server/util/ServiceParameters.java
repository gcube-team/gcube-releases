/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util;

import java.util.List;
import java.util.Map;


/**
 * The Class ServiceParameters.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 23, 2019
 */
public class ServiceParameters {

	protected String url;
	protected String user;
	protected String password;
	protected Map<String, List<String>> properties;


	/**
	 * Instantiates a new service parameters.
	 */
	public ServiceParameters(){}


	/**
	 * Instantiates a new service parameters.
	 *
	 * @param url the url
	 * @param user the user
	 * @param password the password
	 * @param properties the properties
	 */
	public ServiceParameters(String url, String user, String password, Map<String, List<String>> properties) {
		this.url = url;
		this.user = user;
		this.password = password;
		this.properties = properties;
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
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 *
	 * @param password the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the properties
	 */
	public Map<String, List<String>> getProperties() {

		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Map<String, List<String>> properties) {

		this.properties = properties;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("ServiceParameters [url=");
		builder.append(url);
		builder.append(", user=");
		builder.append(user);
		builder.append(", properties=");
		builder.append(properties);
		builder.append("]");
		return builder.toString();
	}

}
