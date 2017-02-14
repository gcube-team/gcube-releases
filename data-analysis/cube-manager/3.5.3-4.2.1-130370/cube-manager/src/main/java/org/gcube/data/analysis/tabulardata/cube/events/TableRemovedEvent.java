package org.gcube.data.analysis.tabulardata.cube.events;

import java.io.Serializable;

import org.gcube.data.analysis.tabulardata.model.table.TableId;

public class TableRemovedEvent implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	TableId tableId;

	public TableRemovedEvent(TableId tableId) {
		super();
		this.tableId = tableId;
	}

	public TableId getTableId() {
		return tableId;
	}
	
}
