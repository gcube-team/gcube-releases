package org.gcube.datapublishing.sdmx.datasource.series.model.impl;

import java.util.HashMap;
import java.util.Map;

import org.gcube.datapublishing.sdmx.datasource.series.model.ObservationModel;

public class ObservationModelImpl implements ObservationModel
{
	
	private Map<String, String> attributes;
	private String 	observationDimension,
					value;
	
	public ObservationModelImpl ()
	{
		this.attributes = new HashMap<String, String> ();
		this.observationDimension = null;
		this.value = null;
	}
	
	public void addAttribute (String key, String value)
	{
		this.attributes.put(key, value);
	}

	@Override
	public String getObservationDimension() {
		return observationDimension;
	}

	public void setObservationDimension(String observationDimension) {
		this.observationDimension = observationDimension;
	}
	@Override
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public Map<String, String> getAttributes() {
		return attributes;
	}
	
	

}
