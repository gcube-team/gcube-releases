/**
 * 
 */
package org.gcube.portlets.user.uriresolvermanager.entity;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 29, 2015
 */
public class ApplicationType {

	String resourceName;
	ServiceAccessPoint accessPoint;
	
	/**
	 * @param resourceName
	 * @param accessPoint
	 */
	public ApplicationType(String resourceName, ServiceAccessPoint accessPoint) {
		this.resourceName = resourceName;
		this.accessPoint = accessPoint;
	}

	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}

	/**
	 * @return the accessPoint
	 */
	public ServiceAccessPoint getAccessPoint() {
		return accessPoint;
	}

	/**
	 * @param resourceName the resourceName to set
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	/**
	 * @param accessPoint the accessPoint to set
	 */
	public void setAccessPoint(ServiceAccessPoint accessPoint) {
		this.accessPoint = accessPoint;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ApplicationType [resourceName=");
		builder.append(resourceName);
		builder.append(", accessPoint=");
		builder.append(accessPoint);
		builder.append("]");
		return builder.toString();
	}
}
