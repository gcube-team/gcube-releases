/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.util;



/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public enum SpeciesGridFields implements GridField {	
	DATASOURCE("datasource", "Data Source"),
	DATAPROVIDER("dataprovider", "Data Provider"),
	SCIENTIFIC_NAME("scientificName", "Scientific Name"),
	DATASET("dataset","Dataset"),
	DATASET_CITATION("datasetCitation", "Data Set Citation"),
	MATCHING_NAME("matchingName", "Name"),
	MATCHING_AUTHOR("accordingTo", "According to"),
	MATCHING_RANK("matchingRank", "Rank"),
	MATCHING_CREDITS("matchingCredits", "Credits"),
	
	PRODUCT_IMAGES("productImages", "Images"),
	PRODUCT_MAPS("productMaps", "Maps"),
	PRODUCT_LAYERS("productLayers", "Layers"),
	PRODUCT_OCCURRENCES("productOccurrences", "Occurrences"),
	
	SCIENTIFICNAMEAUTHORSHIP("scientificNameAuthorship", "S.N. Authorship"),
	CREDITS("credits", "Credits"),
	LSID("lsid", "LSID"),
	PROPERTIES("properties", "Properties"),
	
	
	CLASSIFICATION_STRING("classificationString", "Classification"),
	
	COMMON_NAMES("commonNames", "Names"),
	TAXON("taxon", "Classification"),
	PRODUCTS("products", "Products"),
	PROVENANCE("provenance", "Provenance"),
	IMAGE("image","Image"),
	SELECTION("selection","Selection"),
	ROW("row","row"),
	
	UNKNOWN("unknown", "unknown");

	private String id;
	private String name;
	private boolean sortable;
	
	/**
	 * @param id the field id.
	 * @param name the field name.
	 */
	private SpeciesGridFields(String id, String name) {
		this(id, name, false);
	}
	
	private SpeciesGridFields(String id, String name, boolean sortable) {
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
