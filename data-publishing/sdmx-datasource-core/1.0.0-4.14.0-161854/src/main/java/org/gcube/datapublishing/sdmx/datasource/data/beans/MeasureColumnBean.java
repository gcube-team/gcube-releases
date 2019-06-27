package org.gcube.datapublishing.sdmx.datasource.data.beans;

public class MeasureColumnBean extends DimensionColumnBean implements ColumnBean{

	
	public MeasureColumnBean(String id, String concept) {
		super(id, concept);
	}
	
	public boolean isTimeDimension ()
	{
		return false;
	}
	
	public boolean isMeasureDimension ()
	{
		return true;
	}

}
