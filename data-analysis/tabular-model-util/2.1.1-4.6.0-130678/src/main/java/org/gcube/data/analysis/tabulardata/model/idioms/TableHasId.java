package org.gcube.data.analysis.tabulardata.model.idioms;

import org.gcube.data.analysis.tabulardata.model.table.Table;

import com.google.common.base.Predicate;

public class TableHasId implements Predicate<Table> {
	
	private Long tableId;
	
	public TableHasId(Long tableId) {
		super();
		this.tableId = tableId;
	}

	public boolean apply(Table table){
		if (table.getId()!= null && table.getId().equals(tableId)) return true;
		return false;
	}
}
