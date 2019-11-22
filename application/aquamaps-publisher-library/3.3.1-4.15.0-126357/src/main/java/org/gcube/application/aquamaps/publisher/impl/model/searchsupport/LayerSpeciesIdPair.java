package org.gcube.application.aquamaps.publisher.impl.model.searchsupport;

import org.gcube.common.dbinterface.persistence.annotations.TableRootDefinition;

@TableRootDefinition
public class LayerSpeciesIdPair extends SpeciesIdLink{
		
	public LayerSpeciesIdPair(String speciesId, String layerId) {
		super(layerId, speciesId);
	}
	protected LayerSpeciesIdPair(){
		
	}
}
