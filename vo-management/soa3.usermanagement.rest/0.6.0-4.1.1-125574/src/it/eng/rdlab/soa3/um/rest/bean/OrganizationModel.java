package it.eng.rdlab.soa3.um.rest.bean;

/**
 * This class models organization information
 * 
 * @author Ermanno Travaglino
 * @version 1.0
 * 
 */
public class OrganizationModel {
	
	private String organizationId;
	private String organizationName;
	private String parentOrganizationId;
	private String description;
	
	
	
	
	public OrganizationModel() {
		super();
	}

	public OrganizationModel(String organizationId, String organizationName,
			String parentOrganizationId, String description) {
		super();
		this.organizationId = organizationId;
		this.organizationName = organizationName;
		this.parentOrganizationId = parentOrganizationId;
		this.description = description;
	}
	
	public String getOrganizationId() {
		return organizationId;
	}
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public String getParentOrganizationId() {
		return parentOrganizationId;
	}
	public void setParentOrganizationId(String parentOrganizationId) {
		this.parentOrganizationId = parentOrganizationId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
