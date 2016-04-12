package it.eng.rdlab.soa3.um.rest.jaxrs.bean;

import it.eng.rdlab.soa3.um.rest.bean.OrganizationModel;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents organization information as a JAXB-bound object
 * 
 * @author Ermanno Travaglino
 * @version 1.0
 * 
 */
@XmlRootElement
public class OrganizationJaxbBean {

	private String organizationName;
	private String organizationId;
	private String organizationDescription;
	
	public OrganizationJaxbBean() {
	}
	
	public OrganizationJaxbBean(String organizationName){
		this.organizationName = organizationName;
	}
	

	public OrganizationJaxbBean(OrganizationModel organization){
		this.organizationName = organization.getOrganizationName();
		this.organizationDescription = organization.getDescription();
		this.organizationId = organization.getOrganizationId();
	}
	
	public OrganizationJaxbBean(String organizationName, String organizationDescription){
		this.organizationName = organizationName;
		this.organizationDescription = organizationDescription;
	}

	@XmlElement(name="name")
	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	
	@XmlElement(name="description")
	public String getOrganizationDescription() {
		return organizationDescription;
	}

	public void setOrganizationDescription(String organizationDescription) {
		this.organizationDescription = organizationDescription;
	}
	


	

	
		
}
