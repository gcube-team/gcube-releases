package org.gcube.portets.user.message_conversations.shared;

import com.google.gwt.user.client.rpc.IsSerializable;


public class MessageUserModel implements IsSerializable {
	private long userId;
	private String username;
	private String fullName;
	private String avatarURL;
	private String accountURL;
	private String email;

	//this constructor is used only for development purposes
	public MessageUserModel(String username) {
		super();
		this.userId = username.length();
		this.username = username;
		this.fullName = username.toUpperCase();
		this.email = "test@gmail.com";
	}

	public MessageUserModel() {
		super();
	}

	/**
	 * 
	 * @param userId
	 * @param username
	 * @param fullName
	 * @param email
	 */
	public MessageUserModel(long userId, String username, String fullName, String email) {
		super();
		this.userId = userId;
		this.username = username;
		this.fullName = fullName;
		this.avatarURL = "";
		this.accountURL = "";
		this.email = email;
	}
	/**
	 * 
	 * @param userId
	 * @param username
	 * @param fullName
	 * @param avatarURL
	 * @param accountURL
	 * @param email
	 */
	public MessageUserModel(long userId, String username, String fullName, String avatarURL, String accountURL, String email) {
		super();
		this.userId = userId;
		this.username = username;
		this.fullName = fullName;
		this.avatarURL = avatarURL;
		this.accountURL = accountURL;
		this.email = email;
	}


	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getAvatarURL() {
		return avatarURL;
	}

	public void setAvatarURL(String avatarURL) {
		this.avatarURL = avatarURL;
	}

	public String getAccountURL() {
		return accountURL;
	}

	public void setAccountURL(String accountURL) {
		this.accountURL = accountURL;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "MessageUserModel [userId=" + userId + ", username=" + username + ", fullName=" + fullName
				+ ", avatarURL=" + avatarURL + ", accountURL=" + accountURL + ", email=" + email + "]";
	}

	
	
}
