package org.gcube.rest.commons.db.model.app;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.gcube.rest.commons.db.dao.core.ConverterRecord;
import org.gcube.rest.commons.resourceawareservice.resources.GeneralResource;

@Entity
//@SequenceGenerator(name = "SEQ_STORE", sequenceName = "general_resource_model_id_seq", allocationSize = 1)
@Table(name = "general_resource_model")
public class GeneralResourceModel extends ConverterRecord<GeneralResource> {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "resourceID")
	private String resourceID;

	public GeneralResourceModel() {
		super();
	}
	
	public GeneralResourceModel(GeneralResource base){
		this.copyFrom(base);
	}
	
	public String getResourceID() {
		return this.resourceID;
	}
	
	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}

	@Override
	public final void copyFrom(GeneralResource generalResource){
		this.resourceID = generalResource.getResourceID();
	}
	
	@Override
	public final GeneralResource copyTo() throws IllegalStateException {
		GeneralResource generalResource = new GeneralResource();
		
		generalResource.setResourceID(this.resourceID); 
		
		return generalResource;
	}
	
}
