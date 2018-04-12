package org.gcube.portlets.user.dataminermanager.server.util;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ServiceCredentials implements Serializable {

	private static final long serialVersionUID = 3560918948310315680L;
	private String userName;
	private String fullName;
	private String name;
	private String lastName;
	private String email;
	private String scope;
	private String groupId;
	private String groupName;
	private String userAvatarURL;
	private String token;

	public ServiceCredentials() {
		super();
	}

	public ServiceCredentials(String userName, String scope, String token) {
		super();
		this.userName = userName;
		this.scope = scope;
		this.token = token;
	}

	public ServiceCredentials(String userName, String fullName, String name,
			String lastName, String email, String scope, String groupId,
			String groupName, String userAvatarURL, String token) {
		super();
		this.userName = userName;
		this.fullName = fullName;
		this.name = name;
		this.lastName = lastName;
		this.email = email;
		this.scope = scope;
		this.groupId = groupId;
		this.groupName = groupName;
		this.userAvatarURL = userAvatarURL;
		this.token = token;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getUserAvatarURL() {
		return userAvatarURL;
	}

	public void setUserAvatarURL(String userAvatarURL) {
		this.userAvatarURL = userAvatarURL;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "ServiceCredentials [userName=" + userName + ", fullName="
				+ fullName + ", name=" + name + ", lastName=" + lastName
				+ ", email=" + email + ", scope=" + scope + ", groupId="
				+ groupId + ", groupName=" + groupName + ", userAvatarURL="
				+ userAvatarURL + ", token=" + token + "]";
	}

}
