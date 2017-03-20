package org.gcube.portlets.user.td.gwtservice.shared.share;

import java.io.Serializable;

public class Contacts implements Serializable {

	private static final long serialVersionUID = 8727398755200080050L;
	
	private String id;
	private String login;
	private boolean isGroup;
	
	public Contacts(){
		
	}
	
	
	/**
	 * @param id
	 * @param login
	 * @param isGroup
	 */
	public Contacts(String id, String login, boolean isGroup) {
		this.id = id;
		this.login = login;
		this.isGroup = isGroup;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getLogin() {
		return login;
	}


	public void setLogin(String login) {
		this.login = login;
	}


	public boolean isGroup() {
		return isGroup;
	}


	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}


	@Override
	public String toString() {
		return "Contacts [id=" + id + ", login=" + login + ", isGroup="
				+ isGroup + "]";
	}
	
	

	

}
