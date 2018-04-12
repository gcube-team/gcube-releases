package org.gcube.data.analysis.tabulardata.commons.webservice.types;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.utils.EntityList;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TemplateDescription {
	
	private long id;
	
	@XmlElement
	private List<String> sharedWithUser = new ArrayList<String>();
	
	@XmlElement
	private List<String> sharedWithGroup = new ArrayList<String>();
	
	private String owner;
	
	private String name;
	
	private String description;
	
	private String agency;
	
	private Calendar creationdDate;
	
	private Template template;

	@SuppressWarnings("unused")
	private TemplateDescription(){}
	
	public TemplateDescription(long id, String owner, String name, String description,
			String agency, Calendar creationdDate, Template template, List<String> sharedWith) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.agency = agency;
		this.template = template;
		this.owner = owner;
		this.sharedWithUser = EntityList.getUserList(sharedWith);
		this.sharedWithGroup = EntityList.getGroupList(sharedWith);
		this.creationdDate = creationdDate;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the agency
	 */
	public String getAgency() {
		return agency;
	}

	/**
	 * @param agency the agency to set
	 */
	public void setAgency(String agency) {
		this.agency = agency;
	}

	/**
	 * @return the template
	 */
	public Template getTemplate() {
		return template;
	}

	/**
	 * @param template the template to set
	 */
	public void setTemplate(Template template) {
		this.template = template;
	}
	
	public String getOwner() {
		return owner;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	public List<String> getSharedWithUsers() {
		return sharedWithUser;
	}

	public List<String> getSharedWithGroups() {
		return sharedWithGroup;
	}

	public Calendar getCreationdDate() {
		return creationdDate;
	}
	
	
	
}
