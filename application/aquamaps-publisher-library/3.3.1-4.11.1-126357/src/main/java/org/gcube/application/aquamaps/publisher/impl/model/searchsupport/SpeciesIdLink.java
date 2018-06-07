package org.gcube.application.aquamaps.publisher.impl.model.searchsupport;

import org.gcube.common.dbinterface.Specification;
import org.gcube.common.dbinterface.persistence.annotations.FieldDefinition;
import org.gcube.common.dbinterface.persistence.annotations.TableRootDefinition;

@TableRootDefinition
public abstract class SpeciesIdLink {

	@FieldDefinition(precision={40}, specifications={Specification.NOT_NULL})
	protected String id;
	@FieldDefinition(precision={250}, specifications={Specification.NOT_NULL})
	protected String speciesId;
	
	protected SpeciesIdLink(){
		
	}
	
	public SpeciesIdLink(String id, String speciesId) {
		super();
		this.id = id;
		this.speciesId = speciesId;
	}
	
	/**
	 * @return the layerId
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the scientificNameId
	 */
	public String getSpeciesId() {
		return speciesId;
	}
}
