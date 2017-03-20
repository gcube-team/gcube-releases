package it.eng.rdlab.soa3.connector.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents a list of roles as a JAXB-bound object
 *  
 * @author Ermanno Travaglino
 * @version 1.0
 * 
 */
@XmlRootElement
public class RolesBean {
	
	private List<String> roles;
	
	public RolesBean() {
	}
	
	public RolesBean(List<String> roles){
		this.roles = roles;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	
	
}
