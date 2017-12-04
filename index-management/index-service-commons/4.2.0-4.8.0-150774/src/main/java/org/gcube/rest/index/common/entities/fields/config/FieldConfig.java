package org.gcube.rest.index.common.entities.fields.config;

public class FieldConfig {
	
	private FacetType facetType;
	private boolean presentable;
	private String fieldNameAlias;
	private boolean servesAsTitle;
	private boolean servesAsSnippet;
	
	
	public FieldConfig(){
		this(FacetType.NONE, true, null, false, false);
	}
	
	public FieldConfig(String fieldNameAlias){
		this(FacetType.NONE, true, fieldNameAlias, false, false);
	}
	
	public FieldConfig(FacetType facetType, boolean presentable, String fieldNameAlias, boolean servesAsTitle, boolean servesAsSnippet){
		this.facetType = facetType;
		this.presentable = presentable;
		this.fieldNameAlias = fieldNameAlias;
		this.servesAsTitle = servesAsTitle;
		this.servesAsSnippet = servesAsSnippet;
	}

	public FacetType getFacetType() {
		return facetType;
	}

	public void setFacetType(FacetType facetType) {
		this.facetType = facetType;
	}

	public boolean isPresentable() {
		return presentable;
	}

	public void setPresentable(boolean presentable) {
		this.presentable = presentable;
	}

	public String getFieldNameAlias() {
		return fieldNameAlias;
	}

	public void setFieldNameAlias(String fieldNameAlias) {
		this.fieldNameAlias = fieldNameAlias;
	}


	public void setServesAsTitle(boolean servesAsTitle) {
		this.servesAsTitle = servesAsTitle;
	}
	
	public void setServesAsSnippet(boolean servesAsSnippet) {
		this.servesAsSnippet = servesAsSnippet;
	}
	public boolean isServesAsTitle() {
		return servesAsTitle;
	}

	public boolean isServesAsSnippet() {
		return servesAsSnippet;
	}

	@Override
	public String toString(){
		return "[fieldNameAlias: "+fieldNameAlias + 
				" ,presentable: "+presentable+
				" ,servesAsTitle: "+ servesAsTitle +
				" ,servesAsSnippet: "+servesAsSnippet+
				" ,facetType: "+facetType+"]";
	}
	
	
}
