package org.gcube.datapublishing.sdmx.model;

import java.util.Objects;

import org.gcube.common.resources.gcore.GenericResource;

public class TableIdentificators 
{
	private long 	tabularResourceID,
					tableID;

	private String 	tableIDString,
					tabularResourceIDString;

	
	public TableIdentificators(String tabularResourceID, String tableID) 
	{	
		this.tableIDString = tableID;
		this.tabularResourceIDString = tabularResourceID;
		this.tableID = Long.parseLong(tableID);
		this.tabularResourceID = Long.parseLong(tabularResourceID);
	}
	
	
	public long getTabularResourceID() {
		return tabularResourceID;
	}



	public long getTableID() {
		return tableID;
	}

	public boolean equals (String tabularResourceID, String tableID)
	{
		try
		{
			return equals(Long.parseLong(tabularResourceID), Long.parseLong(tableID));
		} catch (Exception e)
		{
			return false;
		}
		
	}
	
	public boolean equals (long tabularResourceID, long tableID)
	{
		return this.tableID == tableID && this.tabularResourceID == tabularResourceID;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.tabularResourceID, this.tableID);
	}
	
	@Override
	public boolean equals (Object tableIdentificators)
	{
		if (tableIdentificators instanceof TableIdentificators)
		{
			return equals(((TableIdentificators) tableIdentificators).getTabularResourceID(), ((TableIdentificators) tableIdentificators).getTableID());
			

		}
		else return false;
		
	}
	
	@Override
	public String toString ()
	{
		return "[tabular resource id "+this.tabularResourceID+" table id "+this.tableID+"]";
	}


	public String getTableIDString() {
		return tableIDString;
	}


	public String getTabularResourceIDString() {
		return tabularResourceIDString;
	}

	

}
