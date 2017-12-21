/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 4, 2013
 *
 */
public enum SPDAccessLoggerType {
	
	//SEARCH -> TERM IS TERM/S SEARCHED
	SEARCH_OCCURRENCE("SEARCH_OCCURRENCE", "SEARCH OCCURRENCE"),
	SEARCH_TAXON("SEARCH_TAXON", "SEARCH TAXON"),
	BY_COMMON_NAME("BY_COMMON_NAME", "BY COMMON NAME"),
	BY_SCIENTIFIC_NAME("BY_SCIENTIFIC_NAME", "BY SCIENTIFIC NAME"),
	TERM_SEARCHED("TERM_SEARCHED", "TERM SEARCHED"),
	DATASOURCE_USED("DATASOURCE_USED", "DATASOURCE USED"),

	//JOB -> TERM IS JOB NAME
	JOB_SUBMITTED("JOB_SUBMITTED", "JOB SUBMITTED"),
	FOR_OCCURRENCE("FOR_OCCURRENCE", "FOR OCCURRENCE"),
	FOR_TAXON("FOR_TAXON", "FOR TAXON"),
	DATASOURCE_JOB_USED("DATASOURCE_JOB_USED", "DATASOURCE JOB USED"),
	
	//SAVE -> TERM IS JOB NAME
	JOB_SAVED("JOB_SAVED", "JOB SAVED"),
//	FOR_OCCURRENCE("FOR_OCCURRENCE", "FOR OCCURRENCE"),
//	FOR_TAXON("FOR_TAXON", "FOR TAXON"),
	DATASOURCE_JOB_DOWLOADED("DATASOURCE_JOB_DOWLOADED", "DATASOURCE JOB DOWLOADED"),
	
	//MAP -> TERM IS TERM/S SEARCHED
	MAP_GENERATED("MAP_GENERATED", "MAP GENERATED"),
//	FOR_OCCURRENCE("FOR_OCCURRENCE", "FOR OCCURRENCE"),
//	FOR_TAXON("FOR_TAXON", "FOR TAXON"),
	DATASOURCE_MAP_GENERATED("DATASOURCE_MAP_GENERATED", "DATASOURCE MAP GENERATED");
	
	private String name;
	private String id;

	/**
	 * 
	 */
	private SPDAccessLoggerType(String id, String name) {
		this.id = id;
		this.name = name;
	}

	protected String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected String getId() {
		return id;
	}

	protected void setId(String id) {
		this.id = id;
	}
}
