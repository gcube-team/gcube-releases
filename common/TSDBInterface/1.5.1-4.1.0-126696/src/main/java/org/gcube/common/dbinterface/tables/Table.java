package org.gcube.common.dbinterface.tables;

import java.io.Serializable;

public class Table implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5832893512009646381L;
	
	protected String tableAlias;
	protected String tableName;
	
	public Table(String tableName, String tableAlias){
		this.tableName= tableName;
		this.tableAlias= tableAlias;
	}
	
	public Table(String tableName){
		this.tableName= tableName;
		this.tableAlias=null;
	}
	
	public String getTableName(){
		return this.tableName;
	}
	
	public String getTable(){
		return tableAlias==null?tableName:tableName+" AS "+tableAlias;
	}
	
}
