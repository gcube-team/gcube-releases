package org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.fields;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum SpeciesFields implements IsSerializable {

	genus,
	species,
	fbname ,
	speciesid,
	speccode,
	
	scientific_name,
	english_name,
	french_name,
	spanish_name,
	kingdom,
	phylum,
	classcolumn,
	ordercolumn,
	familycolumn,
	
	deepwater,
	m_mammals,
	angling,
	diving,
	dangerous,
	m_invertebrates,
	algae,
	seabirds,
	freshwater,
	pelagic      ,
	picname,
	
	authname,
	
	customized;
	private SpeciesFields() {
		// TODO Auto-generated constructor stub
	}
}
