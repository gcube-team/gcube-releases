package org.gcube.data.analysis.tabulardata.exceptions;

import org.gcube.data.analysis.tabulardata.model.table.Table;

public class ValidationException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3386776826260644390L;

	private Table table;
	
	public ValidationException(Table table){
		this.table = table;
	}

	/**
	 * @return the table
	 */
	public Table getTable() {
		return table;
	}
	
}
