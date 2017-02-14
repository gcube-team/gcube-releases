package org.gcube.data.spd.model.products;

import java.util.List;

import org.gcube.data.spd.model.util.ElementProperty;

public interface ResultElement{

	public enum ResultType {
		OCCURRENCEPOINT,
		TAXONOMY,
		SPECIESPRODUCTS
	}
	
	ResultType getType();
	
	public String getProvider();
	public String getId();
	public String getCitation();
	public String getCredits();
	
	public List<ElementProperty> getProperties();
	
	
}
