package org.gcube.data.analysis.excel.data;

import java.util.ArrayList;

public class TableMetaData {

	private static final String DEFAULT_TABLE_NAME ="table1";
	
	private String tableName;
	private ArrayList<Column> columns;
	
	public TableMetaData (String tableName)
	{
		this.tableName = tableName;
		this.columns = new ArrayList<>();
	}
	
	public TableMetaData ()
	{
		this (DEFAULT_TABLE_NAME);
	}
	
	
	public void addColumn (Column column)
	{
		this.columns.add(column);
	}
	
	public void setColumn (Column column, int position)
	{
		columns.set(position-1, column);
	}

	public String getTableName() {
		return tableName;
	}

	public ArrayList<Column> getColumns() {
		return columns;
	}
	
	
	
}
