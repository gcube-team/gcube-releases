package org.gcube.data.analysis.tabulardata.commons.utils;

public enum Licence {
		
	Attribution("Attribution"),
	AttributionShareAlike("Attribution-ShareAlike"),
	AttributionNoDerivs("Attribution-NoDerivs"),
	AttributionNonCommercial("Attribution-NonCommercial"), 
	AttributionNonCommercialShareAlike("Attribution-NonCommercial-ShareAlike"),
	AttributionNonCommercialNoDerivs("Attribution-NonCommercial-NoDerivs"); 
	
	private String name;

	private Licence(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
		
}
