package org.gcube.applicationsupportlayer.social.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SocialNetworkingUser implements Serializable{
	
	private String username;
	private String email;
	private String fullname;	
	private String userAvatarId;
	
	public SocialNetworkingUser() {
		super();
	}

	public SocialNetworkingUser(String username, String email, String fullname,
			String userAvatarId) {
		super();
		this.username = username;
		this.email = email;
		this.fullname = fullname;
		this.userAvatarId = userAvatarId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getUserAvatarId() {
		return userAvatarId;
	}

	public void setUserAvatarId(String userAvatarId) {
		this.userAvatarId = userAvatarId;
	}

	@Override
	public String toString() {
		return "SocialNetworkingUser [username=" + username + ", email=" + email
				+ ", fullname=" + fullname + ", userAvatarId=" + userAvatarId
				+ "]";
	}

	
}
