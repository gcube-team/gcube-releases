/**
 * 
 */
package org.gcube.portlets.user.workspaceapplicationhandler.entity;

import java.io.Serializable;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 1, 2013
 * 
 */
public class ApplicationEndPoint implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3810556807484909347L;

	private String objectType;
	private String scope;
	private String applicationUrl;

	/**
	 * 
	 */
	public ApplicationEndPoint() {
	}
	
	/**
	 * @param objectType
	 * @param scope
	 * @param applicationUrl
	 */
	public ApplicationEndPoint(String objectType, String scope,
			String applicationUrl) {
		this.objectType = objectType;
		this.scope = scope;
		this.applicationUrl = applicationUrl;
	}

	public String getObjectType() {
		return objectType;
	}

	public String getScope() {
		return scope;
	}

	public String getApplicationUrl() {
		return applicationUrl;
	}


	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public void setApplicationUrl(String applicationUrl) {
		this.applicationUrl = applicationUrl;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ApplicationEndPoint [objectType=");
		builder.append(objectType);
		builder.append(", scope=");
		builder.append(scope);
		builder.append(", applicationUrl=");
		builder.append(applicationUrl);
		builder.append("]");
		return builder.toString();
	}

}
