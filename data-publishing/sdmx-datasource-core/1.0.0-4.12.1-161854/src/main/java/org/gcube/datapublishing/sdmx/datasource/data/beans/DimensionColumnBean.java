package org.gcube.datapublishing.sdmx.datasource.data.beans;

public class DimensionColumnBean extends AbstractColumnBean implements ColumnBean{


	
	
	public DimensionColumnBean(String id, String concept) {
		super(id, concept);
	}
	
	public boolean isTimeDimension ()
	{
		return false;
	}
	
	public boolean isMeasureDimension ()
	{
		return false;
	}

}
