package org.gcube.portlets.user.speciesdiscovery.shared;

public enum SearchType {

	BY_SCIENTIFIC_NAME("SCIENTIFIC_NAME", "Scientific_Name"), 
	BY_COMMON_NAME("COMMON_NAME", "Common_Name"),
	BY_QUERY("QUERY", "Query");

	private String id;
	private String name;

	SearchType() {
	}

	private SearchType(String id, String name) {
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
