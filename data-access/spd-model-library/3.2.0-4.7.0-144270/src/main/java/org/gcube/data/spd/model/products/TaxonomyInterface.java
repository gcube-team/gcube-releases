package org.gcube.data.spd.model.products;

public interface TaxonomyInterface {

	public abstract String getScientificName();

	public abstract String getCitation();

	public abstract String getScientificNameAuthorship();
	
	public abstract String getLsid();
	
	public abstract String getCredits();
	
	public abstract String getId();

	public abstract String getRank();

	public abstract TaxonomyInterface getParent();

}