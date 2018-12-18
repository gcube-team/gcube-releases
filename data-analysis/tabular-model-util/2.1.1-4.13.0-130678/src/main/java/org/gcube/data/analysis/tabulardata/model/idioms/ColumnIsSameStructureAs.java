package org.gcube.data.analysis.tabulardata.model.idioms;

import org.gcube.data.analysis.tabulardata.model.column.Column;

import com.google.common.base.Predicate;

public class ColumnIsSameStructureAs implements Predicate<Column> {

	private Column comparingColumn;

	public ColumnIsSameStructureAs(Column comparingColumn) {
		this.comparingColumn = comparingColumn;
	}

	@Override
	public boolean apply(Column input) {
		return comparingColumn.sameStructureAs(input);
	}

}
