/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.util;



/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public enum TaxonomyGridField implements GridField {
	DATASOURCE("datasource", "Data Source"),
	SYNONYMS("synonyms", "Synonyms"),
	SCIENTIFIC_NAME("scientificName", "Scientific Name"),
	CITATION("citation", "Citation"),
//	NAME("name", "Name"),
	MATCHING_AUTHOR("accordingTo", "According to"),
	
	SCIENTIFICNAMEAUTHORSHIP("scientificNameAuthorship", "S.N. Authorship"),
	CREDITS("credits", "Credits"),
	LSID("lsid", "LSID"),
	PROPERTIES("properties", "Properties"),
	
	MATCHING_RANK("matchingRank", "Rank"),
	MATCHING_ACCORDING_TO("matchingCredits", "Credits"),
	STATUS_REMARKS("statusRemarks", "Status Remarks"),

	
//	PRODUCT_LAYERS("productLayers", "Layers"),
//	PRODUCT_OCCURRENCES("productOccurrences", "Occurrences"),

	CLASSIFICATION_STRING("classificationString", "Classification"),
	
	COMMON_NAMES("commonNames", "Names"),
	TAXONOMY("taxonomy", "Classification"),
	PRODUCTS("products", "Products"),
	PROVENANCE("provenance", "Provenance"),
	SELECTION("selection","Selection"),
	ROW("row","row"),
	STATUSREFNAME("statusRefName", "Status Name"),
	STATUSREFID("statusRefId", "Status ID"),
	DATEMODIFIED("dateModified", "Date Modified"),
	UNKNOWN("unknown", "unknown");

	private String id;
	private String name;
	private boolean sortable;
	
	/**
	 * @param id the field id.
	 * @param name the field name.
	 */
	private TaxonomyGridField(String id, String name) {
		this(id, name, false);
	}
	
	private TaxonomyGridField(String id, String name, boolean sortable) {
		this.id = id;
		this.name = name;
		this.sortable = sortable;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return name;
	}

	@Override
	public boolean isSortable() {
		return sortable;
	}
}
