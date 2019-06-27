package org.gcube.portlets.user.reportgenerator.shared;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Massimiliano Assante ISTI-CNR
 */
@SuppressWarnings("serial")
public class UserBean implements Serializable {

	public transient final static String USER_INFO_ATTR = "USER_INFO_ATTR";
	
	private String username;
	
	private String fullName;
	
	private String avatarId;
	
	private String emailaddress;
	
	public UserBean() {
		super();
	}

	public UserBean(String username, String fullName, String avatarId, String emailaddress) {
		super();
		this.username = username;
		this.fullName = fullName;
		this.avatarId = avatarId;
		this.emailaddress = emailaddress;
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

	public String getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(String avatarId) {
		this.avatarId = avatarId;
	}

	public String getEmailaddress() {
		return emailaddress;
	}

	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}

	@Override
	public String toString() {
		return "UserBean [username=" + username + ", fullName=" + fullName
				+ ", avatarId=" + avatarId + ", emailaddress=" + emailaddress + "]";
	}	
	
}
