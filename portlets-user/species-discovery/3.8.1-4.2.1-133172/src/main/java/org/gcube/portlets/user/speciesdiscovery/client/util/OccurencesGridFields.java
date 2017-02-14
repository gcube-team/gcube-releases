/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.util;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public enum OccurencesGridFields implements GridField {
	
	INSTITUTION_CODE("institutionCode", "InstitutionCode"),
	COLLECTION_CODE("collectionCode", "CollectionCode"),
	CATALOGUE_NUMBER("catalogueNumber", "CatalogueNumber"),
	
	//ADDED BY FRANCESCO 30-08
	DATASOURCE("dataSource", "Data Source"),
	DATAPROVIDER("dataProvider", "Data Provider"),
	DATASET("dataSet","Dataset"),
	
	SCIENTIFICNAMEAUTHORSHIP("scientificNameAuthorship", "S.N. Authorship"),
	CREDITS("credits", "Credits"),
//	LSID("lsid", "LSID"),
	PROPERTIES("properties", "Properties"),
	
	RECORDED_BY("recordedBy", "Recorded By"),
	IDENTIFIED_BY("identifiedBy","Identified By"),
	EVENT_DATE("eventDate", "EventDate"),
	MODIFIED("modified", "Modified"),
	SCIENTIFIC_NAME("scientificName", "ScientificName"),
	KINGDOM("kingdom", "Kingdom"),
	FAMILY("family", "Family"),
	LOCALITY("locality", "Locality"),
	COUNTRY("country", "Country"),
	CITATION("citation", "Citation"),
	DECIMAL_LATITUDE("decimalLatitude", "DecimalLatitude"),
	DECIMAL_LONGITUDE("decimalLongitude", "DecimalLongitude"),
	COORDINATE_UNCERTAINTY_IN_METERS("coordinateUncertaintyInMeters", "CoordinateUncertaintyInMeters"),
	MAX_DEPTH("MaxDepth", "maxDepth"),
	MIN_DEPTH("MinDepth", "minDepth"),
	BASIS_OF_RECORD("basisOfRecord", "BasisOfRecord");

	private String id;
	private String name;
	private boolean sortable;
	
	/**
	 * @param id the field id.
	 * @param name the field name.
	 */
	private OccurencesGridFields(String id, String name) {
		this(id, name, false);
	}
	
	private OccurencesGridFields(String id, String name, boolean sortable) {
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
