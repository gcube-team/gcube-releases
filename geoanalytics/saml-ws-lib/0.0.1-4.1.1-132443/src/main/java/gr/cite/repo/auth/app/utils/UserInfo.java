package gr.cite.repo.auth.app.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class UserInfo {
	public static enum USER_ROLE {
		VISITOR, USER, ADMIN;

		public static USER_ROLE valueOf(int value) {
			switch (value) {
			case 3:
				return ADMIN;
			case 2:
				return USER;
			default:
				return VISITOR;
			}
		}
	}
	@JsonProperty
	private String username;
	@JsonProperty
	private String mail;
	@JsonProperty
	private USER_ROLE role;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public USER_ROLE getRole() {
		return role;
	}

	public void setRole(USER_ROLE role) {
		this.role = role;
	}

}
