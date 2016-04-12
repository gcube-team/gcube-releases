package org.gcube.portlets.admin.wfdocslibrary.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserInfo implements Serializable {

	private String id;
	private String displayName;
	private String userName;
	private String email;
	
	public UserInfo() {	}

	public UserInfo(String id, String displayName, String userName, String email) {
		this.id = id;
		this.displayName = displayName;
		this.userName = userName;
		this.email = email;
	}

	public String getId() {	return id;	}

	public void setId(String id) {	this.id = id;	}

	public String getDisplayName() {return displayName;	}

	public void setDisplayName(String displayName) {	this.displayName = displayName;	}

	public String getUserName() {	return userName;	}

	public void setUserName(String userName) {	this.userName = userName;	}

	public String getEmail() {	return email;	}

	public void setEmail(String email) {this.email = email;}	
	
	public String toString() {
		if (displayName != null)
			return displayName + "("+ userName + ")";
		else return "noInfo";
	}
}
