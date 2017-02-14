package it.eng.rdlab.soa3.um.rest.jaxrs.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents a list of groups as a JAXB-bound object
 * 
 * @author Ermanno Travaglino
 * @version 1.0
 * 
 */
@XmlRootElement
public class GroupsJaxbBean {
	
	private List<String> groups;
	
	public GroupsJaxbBean() {
		// TODO Auto-generated constructor stub
	}
	
	public GroupsJaxbBean(List<String> groups){
		this.groups = groups;
	}
	
	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	
	
}
