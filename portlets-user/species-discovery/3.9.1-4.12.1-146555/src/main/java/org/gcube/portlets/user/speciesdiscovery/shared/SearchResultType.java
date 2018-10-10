/**
 *
 */
package org.gcube.portlets.user.speciesdiscovery.shared;


/**
 * The Enum SearchResultType.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 10, 2017
 */
public enum SearchResultType {


	SPECIES_PRODUCT("RESULTITEM", "Occurrences"),
	TAXONOMY_ITEM("TAXONOMYITEM", "Taxonomy"),
	OCCURRENCE_POINT("OCCURRENCESPOINTS", "OccurrencesPoints"),
	GIS_LAYER_POINT("GIS_LAYER_POINT", "GisLayerPoints");

	private String id;
	private String name;


	/**
	 * Instantiates a new search result type.
	 *
	 * @param id the id
	 * @param name the name
	 */
	private SearchResultType(String id, String name) {
		this.id = id;
		this.name = name;
	}


	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
