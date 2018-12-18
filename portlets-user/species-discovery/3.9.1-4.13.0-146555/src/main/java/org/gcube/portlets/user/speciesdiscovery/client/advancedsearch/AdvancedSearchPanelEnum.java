package org.gcube.portlets.user.speciesdiscovery.client.advancedsearch;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * 
 */
public enum AdvancedSearchPanelEnum {
	BOUNDS("Filter by BBox"), 
	DATE("Filter by Date"), 
	TYPE("Type"), 
	REGION("Region"),
	SYNONYMS("Synonyms From"),
	UNFOLD("Expand"), 
	DATASOURCE("Filter by Source"), 
	CLASSIFICATION("Classification");

	private String label;

	AdvancedSearchPanelEnum() {
	}

	AdvancedSearchPanelEnum(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
