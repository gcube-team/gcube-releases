package org.gcube.portal.databook.shared;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Massimiliano Assante ISTI-CNR
 * 
 * @version 1.0 July 12th 2012
 */
@SuppressWarnings("serial")
public class UserInfo implements Serializable {

	public transient final static String USER_INFO_ATTR = "USER_INFO_ATTR";
	
	private String username;
	
	private String fullName;
	
	private String avatarId;
	
	private String emailaddress;
	
	private String accountURL;
	
	private boolean male;
	
	private boolean admin;
	
	private HashMap<String, String> ownVREs;
	
	public UserInfo() {
		super();
	}

	public UserInfo(String username, String fullName, String avatarId,
			String emailaddress, String accountURL, boolean male,
			boolean admin, HashMap<String, String> ownVREs) {
		super();
		this.username = username;
		this.fullName = fullName;
		this.avatarId = avatarId;
		this.emailaddress = emailaddress;
		this.accountURL = accountURL;
		this.male = male;
		this.admin = admin;
		this.ownVREs = ownVREs;
	}

	public String getAccountURL() {
		return accountURL;
	}

	public void setAccountURL(String accountURL) {
		this.accountURL = accountURL;
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

	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}	
	
	public HashMap<String, String> getOwnVREs() {
		return ownVREs;
	}
	
	public void setOwnVREs(HashMap<String, String> vreMap) {
		this.ownVREs = vreMap;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	
	
}
