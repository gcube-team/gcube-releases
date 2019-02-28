package org.gcube.data.analysis.tabulardata.model.idioms;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;

import com.google.common.base.Predicate;

public class ColumnHasId implements Predicate<Column> {
	
	private ColumnLocalId columnId;

	public ColumnHasId(ColumnLocalId columnId) {
		this.columnId = columnId;
	}

	@Override
	public boolean apply(Column input) {
		return input.getLocalId().equals(columnId); 
	}
	
	

}
