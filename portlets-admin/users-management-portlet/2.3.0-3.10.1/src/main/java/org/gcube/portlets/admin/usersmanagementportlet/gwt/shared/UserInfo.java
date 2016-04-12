package org.gcube.portlets.admin.usersmanagementportlet.gwt.shared;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * UserInfo class represents a user ant all his/her information
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class UserInfo implements IsSerializable, Comparable<UserInfo>{

	private String username;

	private String fullname;

	private String email;
	
	private String userComment;
	
	private Date registrationDate;

	private ArrayList<String> assignedRoles;

	public UserInfo() {

	}

	public UserInfo(String username, String fullname, String email, ArrayList<String> roles, String userComment, Date regDate) {
		this.username = username;
		this.fullname = fullname;
		this.email = email;		
		this.assignedRoles = roles;
		this.userComment = userComment;
		this.registrationDate = regDate;
	}

	public String getUserComment() {
		return userComment;
	}

	public ArrayList<String> getAssignedRoles() {
		return assignedRoles;
	}

	public String getUsername() {
		return username;
	}

	public String getFullname() {
		return fullname;
	}

	public String getEmail() {
		return email;
	}
	
//	public Date getRegistrationDate() {
//		return registrationDate;
//	}
	
	public void setAssignedRoles(ArrayList<String> assignedRoles) {
		this.assignedRoles = assignedRoles;
	}


	public int compareTo(UserInfo arg0) {
		return this.username.compareTo(arg0.username);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UserInfo) 
			return this.username.equals(((UserInfo) obj).username);
		return false;
	}

	@Override
	public int hashCode() {
		return this.username.hashCode();
	}
}
