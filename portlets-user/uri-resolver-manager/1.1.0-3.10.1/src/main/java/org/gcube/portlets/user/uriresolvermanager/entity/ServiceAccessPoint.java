/**
 * 
 */
package org.gcube.portlets.user.uriresolvermanager.entity;

import java.util.List;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 29, 2015
 */
public class ServiceAccessPoint {
	
	private String serviceUrl;
	private String entryName;
	private List<ServiceParameter> serviceParameters;
	
	/**
	 * 
	 */
	public ServiceAccessPoint() {
	}

	/**
	 * @param entryName
	 * @param serviceUrl
	 * @param serviceParameters
	 */
	public ServiceAccessPoint(String entryName, String serviceUrl,
			List<ServiceParameter> serviceParameters) {
		super();
		this.entryName = entryName;
		this.serviceUrl = serviceUrl;
		this.serviceParameters = serviceParameters;
	}

	/**
	 * @return the entryName
	 */
	public String getEntryName() {
		return entryName;
	}

	/**
	 * @return the serviceUrl
	 */
	public String getServiceUrl() {
		return serviceUrl;
	}

	/**
	 * @return the serviceParameters
	 */
	public List<ServiceParameter> getServiceParameters() {
		return serviceParameters;
	}

	/**
	 * @param entryName the entryName to set
	 */
	public void setEntryName(String entryName) {
		this.entryName = entryName;
	}

	/**
	 * @param serviceUrl the serviceUrl to set
	 */
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	/**
	 * @param serviceParameters the serviceParameters to set
	 */
	public void setServiceParameters(List<ServiceParameter> serviceParameters) {
		this.serviceParameters = serviceParameters;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccessPoint [entryName=");
		builder.append(entryName);
		builder.append(", serviceUrl=");
		builder.append(serviceUrl);
		builder.append(", serviceParameters=");
		builder.append(serviceParameters);
		builder.append("]");
		return builder.toString();
	}
}
