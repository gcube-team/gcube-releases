package org.gcube.data.analysis.tabulardata.cube.metadata;

import java.util.List;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.table.Table;

public class TableConsistencyChecker {

	public static void checkTableConsistency(Table table) {
		if (!table.hasName())
			throw new RuntimeException("Table metadata does not point to a relational table:\n" + table);
		if (table.getTableType() == null)
			throw new RuntimeException("Table metadata does not present a type:\n" + table);
		checkColumnsConsistency(table);
		checkColumnsIdOrdering(table.getColumns());
	}

	private static void checkColumnsIdOrdering(List<Column> columns) {
		for (Column column : columns) {
			ColumnConsistencyChecker.checkColumnConsistency(column);
		}
	}

	private static void checkColumnsConsistency(Table table) {

	}

	private static class ColumnConsistencyChecker {

		private static void checkColumnConsistency(Column column) {
			if (!column.hasName())
				throw new RuntimeException("Column metadata does not point to a relational table column:\n" + column);
		}

	}
}
