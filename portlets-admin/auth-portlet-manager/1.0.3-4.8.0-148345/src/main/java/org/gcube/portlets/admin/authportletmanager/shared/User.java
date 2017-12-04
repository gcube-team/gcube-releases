package org.gcube.portlets.admin.authportletmanager.shared;

import java.io.Serializable;

public class User implements Serializable{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String login;
	private String name;
	private String lastname;
	
	
	
	public User() {
	
	}
	public User(String login, String name, String lastname) {
		super();
		this.login = login;
		this.name = name;
		this.lastname = lastname;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
}
