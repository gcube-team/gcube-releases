package org.gcube.portlets.user.shareupdates.shared;

import java.io.Serializable;

import org.gcube.portal.databook.shared.UserInfo;

@SuppressWarnings("serial")
public class UserSettings implements Serializable {
	private UserInfo userInfo;
	private int refreshingTimeInMillis;
	private String currentScope;
	boolean isInfrastructure;
	boolean isNotificationViaEmailEnabled;
	public UserSettings() { 
		super();
	}
	
	public UserSettings(UserInfo userInfo, int refreshingTimeInMillis,
			String currentScope, boolean isInfrastructure,
			boolean isNotificationViaEmailEnabled) {
		super();
		this.userInfo = userInfo;
		this.refreshingTimeInMillis = refreshingTimeInMillis;
		this.currentScope = currentScope;
		this.isInfrastructure = isInfrastructure;
		this.isNotificationViaEmailEnabled = isNotificationViaEmailEnabled;
	}

	public boolean isNotificationViaEmailEnabled() {
		return isNotificationViaEmailEnabled;
	}

	public void setNotificationViaEmailEnabled(boolean isNotificationViaEmailEnabled) {
		this.isNotificationViaEmailEnabled = isNotificationViaEmailEnabled;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public int getRefreshingTimeInMillis() {
		return refreshingTimeInMillis;
	}
	public void setRefreshingTimeInMillis(int refreshingTimeInMillis) {
		this.refreshingTimeInMillis = refreshingTimeInMillis;
	}
	public String getCurrentScope() {
		return currentScope;
	}
	public void setCurrentScope(String currentScope) {
		this.currentScope = currentScope;
	}
	public boolean isInfrastructure() {
		return isInfrastructure;
	}
	public void setInfrastructure(boolean isInfrastructure) {
		this.isInfrastructure = isInfrastructure;
	}
	@Override
	public String toString() {
		return "UserSettings [userInfo=" + userInfo
				+ ", refreshingTimeInMillis=" + refreshingTimeInMillis
				+ ", currentScope=" + currentScope + ", isInfrastructure="
				+ isInfrastructure + "]";
	}
	
}
