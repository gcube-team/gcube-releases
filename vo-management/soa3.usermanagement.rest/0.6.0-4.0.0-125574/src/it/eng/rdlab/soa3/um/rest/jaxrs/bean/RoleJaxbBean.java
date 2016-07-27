package it.eng.rdlab.soa3.um.rest.jaxrs.bean;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents a role's information as a JAXB-bound object
 *  
 * @author Ermanno Travaglino
 * @version 1.0
 * 
 */
@XmlRootElement
public class RoleJaxbBean {

	private String roleName;

	public RoleJaxbBean() {
	}
	
	public RoleJaxbBean(String roleName){
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	
		
}
