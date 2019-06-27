package org.gcube.application.perform.service.engine.model.anagraphic;

import java.util.UUID;

public class Farm {

	
	
public Farm(Long id, Long companyId, Long associationId, java.util.UUID uUID, java.util.UUID companyUUID,
			java.util.UUID associationUUID) {
		super();
		this.id = id;
		this.companyId = companyId;
		this.associationId = associationId;
		UUID = uUID;
		this.companyUUID = companyUUID;
		this.associationUUID = associationUUID;
	}
public Farm() {
	// TODO Auto-generated constructor stub
}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getAssociationId() {
		return associationId;
	}
	public void setAssociationId(Long associationId) {
		this.associationId = associationId;
	}
	public UUID getUUID() {
		return UUID;
	}
	public void setUUID(UUID uUID) {
		UUID = uUID;
	}
	public UUID getCompanyUUID() {
		return companyUUID;
	}
	public void setCompanyUUID(UUID companyUUID) {
		this.companyUUID = companyUUID;
	}
	public UUID getAssociationUUID() {
		return associationUUID;
	}
	public void setAssociationUUID(UUID associationUUID) {
		this.associationUUID = associationUUID;
	}
	private Long id;
	private Long companyId;
	private Long associationId;
	private UUID UUID;
	private UUID companyUUID;
	private UUID associationUUID;
}
