package org.gcube.application.datamanagementfacilityportlet.client.rpc.fields;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum GroupGenerationRequestFields implements IsSerializable{

	author,
	generationname,
	id,
	description,
	phase,
	
	submissiontime,
	starttime,
	endtime,
	currentphasepercent,
	
	sourcehcafids,
	sourcehspenids,
	sourceoccurrencecellsids,
	generatedsourcesid,
	reportid,
	jobids,
	
	
	generationparameters,
	executionparameters,
	
	submissionbackend,
	executionenvironment, 
	backendurl,
	environmentconfiguration,
	logic,
	numpartitions, 
	algorithms,
	
	
	
	evaluatedcomputationcount,
	togeneratetablescount,
}
