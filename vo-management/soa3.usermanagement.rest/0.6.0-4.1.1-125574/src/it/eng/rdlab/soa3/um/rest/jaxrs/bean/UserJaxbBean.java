package it.eng.rdlab.soa3.um.rest.jaxrs.bean;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents user's information as a JAXB-bound object 
 * 
 * @author Ermanno Travaglino
 * @version 1.0
 * 
 */
@XmlRootElement
public class UserJaxbBean {
	
	private String firstname;
	private String password;
	private String username;
	private String lastname;
	private String organizationName;
	private String certificateDN;
 
	
	public UserJaxbBean() {
		// TODO Auto-generated constructor stub
	}
	
	
	public UserJaxbBean(String username, String firstname, String lastname, String organizationName, String password ){
		this.username = username;
		this.lastname = lastname;
		this.organizationName = organizationName;
		this.password = password;
		this.firstname = firstname;
	}
	
	public UserJaxbBean(String username, String firstname, String lastname, String password ){
		this.username = username;
		this.lastname = lastname;
		this.password = password;
		this.firstname = firstname;
	}


	public String getFirstname() {
		return firstname;
	}


	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getLastname() {
		return lastname;
	}


	public void setLastname(String lastname) {
		this.lastname = lastname;
	}


	public String getOrganizationName() {
		return organizationName;
	}


	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}


	public String getCertificateDN() {
		return certificateDN;
	}


	public void setCertificateDN(String certificateDN) {
		this.certificateDN = certificateDN;
	}
	
	
	
	
}
