package org.gcube.data.analysis.tabulardata.model.exceptions;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.Table;

public class NoSuchColumnException extends IllegalArgumentException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8822847127150288559L;

	public NoSuchColumnException(ColumnLocalId id) {
		super("Column with id '"+id.getValue()+"' does not exist.");
	}
	
	public NoSuchColumnException(String name){
		super("Column with name '"+name+"' does not exist.");
	}
	
	public NoSuchColumnException(ColumnLocalId id, Table table) {
		super("Column with id '"+id.getValue()+"' does not exist in table:\n"+table);
	}
	
	public NoSuchColumnException(String name, Table table){
		super("Column with name '"+name+"' does not exist in table:\n%s" + table);
	}

}
