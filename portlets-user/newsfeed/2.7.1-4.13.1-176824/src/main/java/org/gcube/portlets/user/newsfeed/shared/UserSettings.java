package org.gcube.portlets.user.newsfeed.shared;

import org.gcube.portal.databook.shared.UserInfo;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class UserSettings implements IsSerializable {
	private UserInfo userInfo;
	private int refreshingTimeInMillis;
	private String currentScope;
	//indicate the 
	private String vreLabel;
	//indicate whether the webapp is running at infrasturcture level
	boolean isInfrastructure;
	//indicate whether to indicate the feed timeline source or not (From whicn VRE/Channel this feed come from)
	boolean showTimelineSourceLabel;
	public UserSettings() { 
		super();
	}

	
	public UserSettings(UserInfo userInfo, int refreshingTimeInMillis,
			String currentScope, String vreLabel, boolean isInfrastructure,
			boolean showTimelineSourceLabel) {
		super();
		this.userInfo = userInfo;
		this.refreshingTimeInMillis = refreshingTimeInMillis;
		this.currentScope = currentScope;
		this.vreLabel = vreLabel;
		this.isInfrastructure = isInfrastructure;
		this.showTimelineSourceLabel = showTimelineSourceLabel;
	}


	public boolean isShowTimelineSourceLabel() {
		return showTimelineSourceLabel;
	}


	public void setShowTimelineSourceLabel(boolean showTimelineSourceLabel) {
		this.showTimelineSourceLabel = showTimelineSourceLabel;
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
	
	public String getVreLabel() {
		return vreLabel;
	}

	public void setVreLabel(String channelName) {
		this.vreLabel = channelName;
	}

	@Override
	public String toString() {
		return "UserSettings [userInfo=" + userInfo
				+ ", refreshingTimeInMillis=" + refreshingTimeInMillis
				+ ", currentScope=" + currentScope + ", vreLabel=" + vreLabel
				+ ", isInfrastructure=" + isInfrastructure
				+ ", showTimelineSourceLabel=" + showTimelineSourceLabel + "]";
	}


	
}
