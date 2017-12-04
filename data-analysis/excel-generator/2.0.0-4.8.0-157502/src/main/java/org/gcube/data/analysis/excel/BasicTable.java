package org.gcube.data.analysis.excel;

public abstract class BasicTable {

	private static final String DEFAULT_TABLE_NAME ="table1";
	
	protected String tableName;

	
	public BasicTable (String tableName)
	{
		this.tableName = tableName;
	}
	
	public BasicTable ()
	{
		this (DEFAULT_TABLE_NAME);
	}
	


	public String getTableName() {
		return tableName;
	}


	
	
	
}
