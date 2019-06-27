package org.gcube.data.analysis.tabulardata.commons.webservice.types.resources;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.resources.Resource;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceDescriptor {

	private long id;
	
	private String name;
	
	private String description;
	
	private Calendar creationDate;
	
	private long creatorId;
	
	private ResourceType resourceType;

	private Resource resource;
	
	public ResourceDescriptor(long id, String name, String description, Calendar creationDate, long creatorId, Resource resource, ResourceType type) {
		super();
		this.id = id;
		this.description = description;
		this.creatorId = creatorId;
		this.resource = resource;
		this.name = name;
		this.creationDate = creationDate;
		this.resourceType = type;
	}

	protected ResourceDescriptor(){}
	
	public long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(long creatorId) {
		this.creatorId = creatorId;
	}


	public Resource getResource() {
		return resource;
	}
	
	public String getName() {
		return name;
	}

	public Calendar getCreationDate() {
		return creationDate;
	}
	
	public ResourceType getResourceType() {
		return resourceType;
	}
	
	@Override
	public String toString() {
		return "ResourceDescriptor [name=" + name + ", description="
				+ description + ", creatorId=" + creatorId + ", resourceType="
				+ resourceType + "]";
	}

	
	
	
}
