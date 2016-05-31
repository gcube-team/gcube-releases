package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.session;

import java.io.Serializable;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class UserInfo implements Serializable {

	private static final long serialVersionUID = -2826549639677017234L;

	private String username;
	private long groupId;
	private String groupName;
	private String scope;
	private String scopeName;
	private String userEmailAddress;
	private String userFullName;

	public UserInfo() {
		super();
	}

	/**
	 * 
	 * @param username
	 * @param groupId
	 * @param groupName
	 * @param scope
	 * @param scopeName
	 * @param userEmailAddress
	 * @param userFullName
	 */
	public UserInfo(String username, long groupId, String groupName,
			String scope, String scopeName, String userEmailAddress,
			String userFullName) {
		super();
		this.username = username;
		this.groupId = groupId;
		this.groupName = groupName;
		this.scope = scope;
		this.scopeName = scopeName;
		this.userEmailAddress = userEmailAddress;
		this.userFullName = userFullName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getScopeName() {
		return scopeName;
	}

	public void setScopeName(String scopeName) {
		this.scopeName = scopeName;
	}

	public String getUserEmailAddress() {
		return userEmailAddress;
	}

	public void setUserEmailAddress(String userEmailAddress) {
		this.userEmailAddress = userEmailAddress;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	@Override
	public String toString() {
		return "UserInfo [username=" + username + ", groupId=" + groupId
				+ ", groupName=" + groupName + ", scope=" + scope
				+ ", scopeName=" + scopeName + ", userEmailAddress="
				+ userEmailAddress + ", userFullName=" + userFullName + "]";
	}

}
