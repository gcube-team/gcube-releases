package org.gcube.portal.auth;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RequestingApp implements Serializable {
	
	
	private String applicationId;
	private String description;
	private String logoURL;
	
	public RequestingApp() {
		super();
		// TODO Auto-generated constructor stub
	}
	public RequestingApp(String applicationId, String description, String logoURL) {
		super();
		this.applicationId = applicationId;
		this.description = description;
		this.logoURL = logoURL;
	}
	public String getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLogoURL() {
		return logoURL;
	}
	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;
	}
	@Override
	public String toString() {
		return "RequestingApp [applicationId=" + applicationId + ", description=" + description + ", logoURL=" + logoURL
				+ "]";
	}
	
}
