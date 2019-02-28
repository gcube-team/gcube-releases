package org.gcube.application.aquamaps.publisher.impl.model.searchsupport;

import org.gcube.common.dbinterface.persistence.annotations.TableRootDefinition;

@TableRootDefinition
public class FileSetSpeciesIdPair extends SpeciesIdLink {

	public FileSetSpeciesIdPair(String speciesId, String id) {
		super(id, speciesId);
	}
	protected FileSetSpeciesIdPair(){
		
	}
	
	
}
