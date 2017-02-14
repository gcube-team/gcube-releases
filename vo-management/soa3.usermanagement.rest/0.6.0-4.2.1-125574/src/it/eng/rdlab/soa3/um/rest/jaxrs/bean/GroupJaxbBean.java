package it.eng.rdlab.soa3.um.rest.jaxrs.bean;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents group information as a JAXB-bound object
 * 
 * @author Ermanno Travaglino
 * @version 1.0
 * 
 */
@XmlRootElement
public class GroupJaxbBean {

	private String groupName;
	private String organizationName;
	private String description;
	
	public GroupJaxbBean() {
	}
	
	public GroupJaxbBean(String groupName){
		this.groupName = groupName;
	}
	
	public GroupJaxbBean(String groupName, String organizationName){
		this.groupName = groupName;
		this.organizationName = organizationName;
	}
	
	public GroupJaxbBean(String groupName, String organizationName,
			String description) {
		super();
		this.groupName = groupName;
		this.organizationName = organizationName;
		this.description = description;
	}

	public String getDescription() 
	{	if (description != null) return description;
		else return "";
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	
	

	
		
}
