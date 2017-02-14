package it.eng.rdlab.soa3.um.rest.jaxrs.bean;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents credentials (old and new password) of an user as a JAXB-bound object
 * 
 * @author Ermanno Travaglino
 * @version 1.0
 * 
 */
@XmlRootElement
public class CredentialsJaxbBean {
	
	private String oldpassword;
	private String newpassword;
	
	public CredentialsJaxbBean() {
	}
	
	public CredentialsJaxbBean(String oldpw , String newpw) {
		this.oldpassword = oldpw;
		this.newpassword = newpw;
	}

	public String getOldpassword() {
		return oldpassword;
	}

	public void setOldpassword(String oldpassword) {
		this.oldpassword = oldpassword;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}


	
	
	
	
	

}

