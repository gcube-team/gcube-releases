package org.gcube.application.reporting.reference;

public enum ReferenceReportType {
	GeneralMeasure("GeneralMeasure"),
	InformationSource("InformationSource"),
	FisheryAreasHistory("FisheryAreasHistory"),
	VMEsHistory("VMEsHistory"),
	Rfmo("Rfmo");
	
	private String _id;
	
	private ReferenceReportType(String id) {
		this._id = id;
	}

	/**
	 * @return the 'id' value
	 */
	public String getId() {
		return this._id;
	}
	
	
}
