package org.gcube.portlets.user.speciesdiscovery.shared;


public enum SpeciesCapability {
	
	//Filters
	FROMDATE("FROMDATE", "Date From"), 
	TODATE("DATETO", "Date To"), 
	LOWERBOUND("LOWERBOUND", "Lower Bound"), 
	UPPERBOUND("UPPERBOUND", "Upper Bound"),
	
	//Capabilities
	RESULTITEM("RESULTITEM", "Occurrence"),
	TAXONOMYITEM("TAXONOMYITEM", "Taxon"),
	OCCURRENCESPOINTS("OCCURRENCESPOINTS", "OccurrencesPoints"),
	
	
	SYNONYMS("SYNONYMS", "Synonyms"),
	UNFOLD("UNFOLD", "Unfold"),
	NAMESMAPPING("NAMESMAPPING", "Names Mapping"),
	
	UNKNOWN("UNKNOWN", "unknown");
	
	private String id;
	private String name;
	
	SpeciesCapability(){	
	}


	private SpeciesCapability(String id, String name) {
		this.id = id;
		this.name = name;
	}


	public String getId() {
		return id;
	}


	public String getName() {
		return name;
	}
	
	

}
