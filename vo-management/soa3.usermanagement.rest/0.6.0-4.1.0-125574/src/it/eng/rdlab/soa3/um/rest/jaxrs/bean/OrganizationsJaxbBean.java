package it.eng.rdlab.soa3.um.rest.jaxrs.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class represents a list of organization as a JAXB-bound object
 *  
 * @author Ermanno Travaglino
 * @version 1.0
 * 
 */
@XmlRootElement
@XmlSeeAlso(OrganizationJaxbBean.class)
public class OrganizationsJaxbBean {
	
	private List<OrganizationJaxbBean> organizations;
	
	public OrganizationsJaxbBean() {
	}

	public OrganizationsJaxbBean(List<OrganizationJaxbBean> organizations) {
		super();
		this.organizations = organizations;
	}

	
	public List<OrganizationJaxbBean> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(List<OrganizationJaxbBean> organizations) {
		this.organizations = organizations;
	}
	
	
	
	
}
