package org.gcube.data.analysis.excel.metadata;

import java.util.ArrayList;

import org.gcube.data.analysis.excel.ColumnModel;
import org.gcube.data.analysis.excel.BasicTable;

public class MetadataTableImpl extends BasicTable implements MetadataTable {


	private ArrayList<ColumnModel> columns;
	
	public MetadataTableImpl (String tableName)
	{
		super (tableName);
		this.columns = new ArrayList<>();
	}
	
	public MetadataTableImpl ()
	{
		super ();
	}
	
	
	public void addColumn (ColumnModel column)
	{
		this.columns.add(column);
	}
	
	public void setColumn (ColumnModel column, int position)
	{
		columns.set(position-1, column);
	}


	@Override
	public ArrayList<ColumnModel> getColumns() {
		return columns;
	}
	
	
	
}
