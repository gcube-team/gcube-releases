package org.gcube.data.analysis.tabulardata.model.idioms;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;

import com.google.common.base.Predicate;

public class ColumnIsOfType implements Predicate<Column> {
	
	ColumnType columnType;
	
	public ColumnIsOfType(ColumnType columnType) {
		super();
		this.columnType = columnType;
	}

	@Override
	public boolean apply(Column input) {
		if (input.getColumnType().equals(columnType)) return true;
		return false;
	}
	
}
