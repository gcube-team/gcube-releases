package org.gcube.common.dbinterface.tables;

import org.gcube.common.dbinterface.queries.Selection;



public class TableFromSubselect extends Table{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2730302886302284535L;
	
	private Selection subquery;
	
	public TableFromSubselect(String tableAlias, Selection query) {
		super(tableAlias);
		this.subquery= query;
	}

	public String getTable(){
		return "("+this.subquery.getExpression()+") AS "+this.tableName;
	}
	
	
}
