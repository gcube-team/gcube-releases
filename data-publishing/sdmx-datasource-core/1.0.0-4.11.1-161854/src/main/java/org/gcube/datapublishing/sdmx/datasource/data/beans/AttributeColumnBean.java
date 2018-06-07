package org.gcube.datapublishing.sdmx.datasource.data.beans;

public abstract class AttributeColumnBean extends AbstractColumnBean implements ColumnBean 
{


	private boolean observation;

	public AttributeColumnBean (String id, String concept,boolean observation)
	{
		super (id,concept);
		this.observation = observation;
	}
	

	public boolean isObservation ()
	{
		return this.observation;
	}
	
	public boolean isDimension ()
	{
		return !this.observation;
	}

}
