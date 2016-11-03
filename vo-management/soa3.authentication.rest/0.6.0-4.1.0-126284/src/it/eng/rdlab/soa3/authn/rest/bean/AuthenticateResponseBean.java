package it.eng.rdlab.soa3.authn.rest.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Utility bean returned as authentication response
 * @author Kanchanna Ramasamy Balraj
 * @author Ermanno Travaglino
 *
 */

@XmlRootElement
public class AuthenticateResponseBean 
{
	private String userName;

	private List<String> roles;
	

	public AuthenticateResponseBean() 
	{
		this.roles = new ArrayList<String>();
	}


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
