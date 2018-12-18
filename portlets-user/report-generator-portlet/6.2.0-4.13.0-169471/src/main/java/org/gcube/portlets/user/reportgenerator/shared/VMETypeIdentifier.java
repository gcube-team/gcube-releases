package org.gcube.portlets.user.reportgenerator.shared;

public enum VMETypeIdentifier {
	Vme("Vme", "VME"),
	GeneralMeasure("GeneralMeasure", "General Measure"),
	InformationSource("InformationSource", "Information Source"),
	FisheryAreasHistory("FisheryAreasHistory", "fishing foot print"),
	VMEsHistory("VMEsHistory", "regional history"),
	Rfmo("Rfmo", "RFMO");

	private String _id;
	private String displayName;

	private VMETypeIdentifier(String id, String _displayName) {
		this._id = id;
		this.displayName = _displayName;
	}

	/**
	 * @return the 'id' value
	 */
	public String getId() {
		return this._id;
	}

	public String getDisplayName() {
		return displayName;
	}
	
}
