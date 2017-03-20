package it.eng.rdlab.soa3.connector.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Utility bean returned as authentication response
 * @author Kanchanna Ramasamy Balraj
 * @author Ermanno Travaglino
 * @author Ciro Formisano
 *
 */

@XmlRootElement
public class UserBean {
	private String userName;

	private List<String> roles;
	


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
	
	

}
