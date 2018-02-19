package org.gcube.datapublishing.sdmx.datasource.data.beans;

public class TimeDimensionColumnBean extends DimensionColumnBean implements ColumnBean{


	
	
	public TimeDimensionColumnBean(String id, String concept) {
		super(id, concept);
	}
	
	public boolean isTimeDimension ()
	{
		return true;
	}
	
	public boolean isMeasureDimension ()
	{
		return false;
	}

}
