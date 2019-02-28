package org.gcube.portlets.user.socialprofile.shared;

import java.io.Serializable;

import org.gcube.portal.databook.shared.UserInfo;

@SuppressWarnings("serial")
public class UserContext implements Serializable {
	private UserInfo userInfo;
	private String headline;
	private String institution;
	private String summary;
	private String currentScope;
	private boolean isInfrastructure;
	private boolean isOwner;
	private String sendMessageURL;
	
	public UserContext() { 
		super();
	}

	

	public UserContext(UserInfo userInfo, String headline, String institution, String summary, String currentScope,
			boolean isInfrastructure, boolean isOwner, String sendMessageURL) {
		super();
		this.userInfo = userInfo;
		this.headline = headline;
		this.institution = institution;
		this.summary = summary;
		this.currentScope = currentScope;
		this.isInfrastructure = isInfrastructure;
		this.isOwner = isOwner;
		this.sendMessageURL = sendMessageURL;
	}



	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public String getHeadline() {
		return headline;
	}
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	public String getInstitution() {
		return institution;
	}
	public void setInstitution(String institution) {
		this.institution = institution;
	}
	public String getCurrentScope() {
		return currentScope;
	}
	public void setCurrentScope(String currentScope) {
		this.currentScope = currentScope;
	}
	public boolean isOwner() {
		return isOwner;
	}
	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public boolean isInfrastructure() {
		return isInfrastructure;
	}

	public void setInfrastructure(boolean isInfrastructure) {
		this.isInfrastructure = isInfrastructure;
	}



	public String getSendMessageURL() {
		return sendMessageURL;
	}



	public void setSendMessageURL(String sendMessageURL) {
		this.sendMessageURL = sendMessageURL;
	}



	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserContext [userInfo=");
		builder.append(userInfo);
		builder.append(", headline=");
		builder.append(headline);
		builder.append(", institution=");
		builder.append(institution);
		builder.append(", summary=");
		builder.append(summary);
		builder.append(", currentScope=");
		builder.append(currentScope);
		builder.append(", isInfrastructure=");
		builder.append(isInfrastructure);
		builder.append(", isOwner=");
		builder.append(isOwner);
		builder.append(", sendMessageURL=");
		builder.append(sendMessageURL);
		builder.append("]");
		return builder.toString();
	}
	
	
}
