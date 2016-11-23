/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public enum SearchResultType {
	
	
	SPECIES_PRODUCT("RESULTITEM", "Occurrences"),
	TAXONOMY_ITEM("TAXONOMYITEM", "Taxonomy"),
	OCCURRENCE_POINT("OCCURRENCESPOINTS", "OccurrencesPoints");
	
	private String id;
	private String name;


	private SearchResultType(String id, String name) {
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
