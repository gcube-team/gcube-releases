package org.gcube.dataanalysis.ecoengine.datatypes;

import java.util.List;

public class ColumnTypesList  extends StatisticalType {
	
	String tableName;
	protected List<ColumnType> list;
	
	public ColumnTypesList(String tableName, String name, String description, boolean optional) {
		super(name, description, optional);
		this.tableName=tableName;
	}
	
	public void add(ColumnType st){
		list.add(st);
	}

	public List<ColumnType> getList(){
		return list;
	}
	
	public String getTabelName(){
		return tableName;
	}
	
}
