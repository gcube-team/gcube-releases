package org.gcube.portlets.user.takecourse.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TrainerDTO  implements Serializable {

	private String username;
	private String fullName;
	private String headline;
	private String avatarURL;
	private String emailaddress;
	private String accountURL;
	
	public TrainerDTO(String username, String fullName, String headline, String avatarURL, String emailaddress,
			String accountURL) {
		super();
		this.username = username;
		this.fullName = fullName;
		this.headline = headline;
		this.avatarURL = avatarURL;
		this.emailaddress = emailaddress;
		this.accountURL = accountURL;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
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

	public String getEmailaddress() {
		return emailaddress;
	}

	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}

	public String getAccountURL() {
		return accountURL;
	}

	public void setAccountURL(String accountURL) {
		this.accountURL = accountURL;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrainerDTO [username=");
		builder.append(username);
		builder.append(", fullName=");
		builder.append(fullName);
		builder.append(", avatarId=");
		builder.append(avatarURL);
		builder.append(", emailaddress=");
		builder.append(emailaddress);
		builder.append(", accountURL=");
		builder.append(accountURL);
		builder.append("]");
		return builder.toString();
	}
	
}
