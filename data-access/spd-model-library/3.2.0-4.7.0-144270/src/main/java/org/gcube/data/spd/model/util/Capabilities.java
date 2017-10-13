package org.gcube.data.spd.model.util;

public enum Capabilities {
	Classification("getClassificationInterface", true),
	NamesMapping("getMappingInterface", false),
	Occurrence("getOccurrencesInterface", true),
	Expansion("getExpansionInterface", false),
	Unfold("getUnfoldInterface", false);
	
	private String method;
	private boolean propertySupport;
	
	Capabilities(String method, boolean propertySupport){
		this.method = method;
		this.propertySupport = propertySupport;
	}
	
	
	public boolean isPropertySupport() {
		return propertySupport;
	}


	public void setPropertySupport(boolean propertySupport) {
		this.propertySupport = propertySupport;
	}



	public String getMethod(){
		return this.method;
	}

}
