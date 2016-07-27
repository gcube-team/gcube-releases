package org.gcube.portlets.user.td.gwtservice.shared.tr.resources;

import java.io.Serializable;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ResourceTDDescriptor implements Serializable {

	private static final long serialVersionUID = -6324769323093791963L;
	private long id;
	private String name;
	private String description;
	private String creationDate;
	private long creatorId;
	private ResourceTDType resourceType;
	private ResourceTD resourceTD;

	public ResourceTDDescriptor() {

	}
	
	/**
	 * 
	 * @param id
	 * @param name
	 * @param description
	 * @param creationDate
	 * @param creatorId
	 * @param resourceType
	 * @param resourceTD
	 */
	public ResourceTDDescriptor(long id, String name, String description,
			String creationDate, long creatorId, ResourceTDType resourceType,
			ResourceTD resourceTD) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.creationDate = creationDate;
		this.creatorId = creatorId;
		this.resourceType = resourceType;
		this.resourceTD = resourceTD;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(long creatorId) {
		this.creatorId = creatorId;
	}

	public ResourceTDType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceTDType resourceType) {
		this.resourceType = resourceType;
	}

	public ResourceTD getResourceTD() {
		return resourceTD;
	}

	public void setResourceTD(ResourceTD resourceTD) {
		this.resourceTD = resourceTD;
	}

	@Override
	public String toString() {
		return "ResourceDescriptorTD [id=" + id + ", name=" + name
				+ ", description=" + description + ", creationDate="
				+ creationDate + ", creatorId=" + creatorId + ", resourceType="
				+ resourceType + ", resourceTD=" + resourceTD + "]";
	}

	
	
	
}
