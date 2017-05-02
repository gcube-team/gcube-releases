package org.gcube.data.analysis.tabulardata.cube.metadata.exceptions;

import org.gcube.data.analysis.tabulardata.model.table.TableType;

public class NoSuchTableException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3218208012859148967L;

	long id;
	
	TableType type;
	
	public NoSuchTableException(long id){
		super(String.format("Unable to retrieve table with id '%1$s'.",id));
		this.id = id;
	}
	
	public NoSuchTableException(String name){
		super(String.format("Unable to retrieve table with name '%1$s'.",name));
	}

	public NoSuchTableException(long id, TableType type) {
		super("Unable to retrieve table of type " + type + " and id=" + id);
		this.id = id;
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public TableType getType() {
		return type;
	}

}
