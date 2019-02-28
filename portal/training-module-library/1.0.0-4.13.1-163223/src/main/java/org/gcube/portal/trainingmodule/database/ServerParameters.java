package org.gcube.portal.trainingmodule.database;


import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class ServerParameters.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 20, 2015
 */
public class ServerParameters {
	
	/** The url. */
	protected String url;
	
	/** The user. */
	protected String user;
	
	/** The password. */
	protected String password;
	
	/** The properties. */
	protected Map<String, String> properties;
	
	/**
	 * Instantiates a new server parameters.
	 */
	public ServerParameters(){}
	
	
	/**
	 * Instantiates a new server parameters.
	 *
	 * @param url the url
	 * @param user the user
	 * @param password the password
	 * @param properties the properties
	 */
	public ServerParameters(String url, String user, String password, Map<String, String> properties) {
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
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	public Map<String, String> getProperties() {
		return properties;
	}


	/**
	 * Sets the properties.
	 *
	 * @param properties the properties
	 */
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServerParameters [url=");
		builder.append(url);
		builder.append(", user=");
		builder.append(user);
		builder.append(", password=");
		builder.append("XXX");
		builder.append(", properties=");
		builder.append(properties);
		builder.append("]");
		return builder.toString();
	}

}
