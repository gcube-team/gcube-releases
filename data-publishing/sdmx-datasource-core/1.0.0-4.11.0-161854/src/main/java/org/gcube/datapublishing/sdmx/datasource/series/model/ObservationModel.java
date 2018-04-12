package org.gcube.datapublishing.sdmx.datasource.series.model;

import java.util.Map;

public interface ObservationModel 
{
	

	public String getObservationDimension();
	
	public void setObservationDimension(String observationDimension);
	
	public String getValue();

	public void setValue(String value);

	public Map<String, String> getAttributes();
	
	

}
