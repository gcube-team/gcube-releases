package org.gcube.dataanalysis.ecoengine.datatypes;


//name of columns
public class ColumnType extends StatisticalType{
	
	private String tableName;
	
	public ColumnType(String tableName, String name, String description, String defaultValue, boolean optional) {
		super(name, description, defaultValue, optional);
		this.tableName=tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	
		
}
