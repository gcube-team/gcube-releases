/**
 * 
 */
package org.gcube.portlets.user.transect.server.readers.entity;

import java.util.HashMap;


/**
 * The Class ServiceAccessPoint.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 28, 2015
 */
public class ServiceAccessPoint {

	private String entryName;
	private String serviceUrl;
	private String username;
	private String pwd;
	private HashMap<String, RuntimeProperty> runtimeProperties;
	
	/**
	 * Instantiates a new service access point.
	 */
	public ServiceAccessPoint() {
	}

	/**
	 * Instantiates a new service access point.
	 *
	 * @param entryName the entry name
	 * @param serviceUrl the service url
	 * @param runtimeProperties the runtime properties
	 */
	public ServiceAccessPoint(String entryName, String serviceUrl, String username, String pwd,
			HashMap<String, RuntimeProperty> runtimeProperties) {
		super();
		this.entryName = entryName;
		this.serviceUrl = serviceUrl;
		this.username = username;
		this.pwd = pwd;
		this.runtimeProperties = runtimeProperties;
	}

	/**
	 * Gets the service url.
	 *
	 * @return the serviceUrl
	 */
	public String getServiceUrl() {
		return serviceUrl;
	}

	/**
	 * Gets the entry name.
	 *
	 * @return the entryName
	 */
	public String getEntryName() {
		return entryName;
	}

	/**
	 * Gets the runtime properties.
	 *
	 * @return the runtimeProperties
	 */
	public HashMap<String, RuntimeProperty> getRuntimeProperties() {
		return runtimeProperties;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the pwd
	 */
	public String getPwd() {
		return pwd;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @param pwd the pwd to set
	 */
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	/**
	 * Sets the service url.
	 *
	 * @param serviceUrl the serviceUrl to set
	 */
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	/**
	 * Sets the entry name.
	 *
	 * @param entryName the entryName to set
	 */
	public void setEntryName(String entryName) {
		this.entryName = entryName;
	}

	/**
	 * Sets the runtime properties.
	 *
	 * @param runtimeProperties the runtimeProperties to set
	 */
	public void setRuntimeProperties(
			HashMap<String, RuntimeProperty> runtimeProperties) {
		this.runtimeProperties = runtimeProperties;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServiceAccessPoint [entryName=");
		builder.append(entryName);
		builder.append(", serviceUrl=");
		builder.append(serviceUrl);
		builder.append(", username=");
		builder.append(username);
		builder.append(", pwd=");
		builder.append(pwd);
		builder.append(", runtimeProperties=");
		builder.append(runtimeProperties);
		builder.append("]");
		return builder.toString();
	}

}
