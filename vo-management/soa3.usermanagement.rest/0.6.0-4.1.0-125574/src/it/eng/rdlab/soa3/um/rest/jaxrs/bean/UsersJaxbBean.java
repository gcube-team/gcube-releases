package it.eng.rdlab.soa3.um.rest.jaxrs.bean;

import java.util.List;


/**
 * This class represents a list of users as a JAXB-bound object
 * 
 * @author Ermanno Travaglino
 * @version 1.0
 * 
 */
public class UsersJaxbBean {
	
	private List<String> users;
	public UsersJaxbBean() {
		// TODO Auto-generated constructor stub
	}
	
	public UsersJaxbBean(List<String> users) {
		this.users = users;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}
	
	
	
	

}
