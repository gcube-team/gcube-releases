package org.gcube.data.analysis.tabulardata.cube.events;

import java.io.Serializable;

import org.gcube.data.analysis.tabulardata.model.table.Table;

public class TableCreationEvent implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Table table;

	public TableCreationEvent(Table table) {
		super();
		this.table = table;
	}

	public Table getTable() {
		return table;
	}
		
}
