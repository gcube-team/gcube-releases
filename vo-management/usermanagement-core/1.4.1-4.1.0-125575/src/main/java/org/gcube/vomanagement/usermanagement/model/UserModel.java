package org.gcube.vomanagement.usermanagement.model;

import java.util.HashMap;


/**
 * Liferay User Object Model
 * 
 */
public class UserModel{

	private String fullname;

	private String email;

	private String screenName;

	private String firstname;
	
	private String lastname;
	
	private String userId;
	
	private long registrationDate;
	
	private HashMap<String,String> customAttrsMap;

	public HashMap<String, String> getCustomAttrsMap() {
		return customAttrsMap;
	}
	public void setCustomAttrsMap(HashMap<String, String> customAttrsMap) {
		this.customAttrsMap = customAttrsMap;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}


	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public UserModel() {

	}
	

	public long getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(long registrationDate) {
		this.registrationDate = registrationDate;
	}
	public UserModel(String userId, String firstname, String lastname, String fullname,String email,String screenname,long registrationDate,HashMap<String,String> customAttrsMap) {
		this.userId = userId;
		this.firstname=firstname;
		this.lastname=lastname;
		this.fullname= fullname;
		this.email = email;
		this.screenName = screenname;
		this.customAttrsMap = customAttrsMap;
		this.registrationDate = registrationDate;
		
	}
}