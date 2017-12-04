package org.gcube.data.analysis.tabulardata.cube.metadata.model;

import java.util.Collection;
import java.util.List;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

import com.google.common.collect.Lists;

public class TableFactory {

	public static Table createTable(JPATable jpaTable) {
		List<Column> columns = ColumnFactory.createColumns(jpaTable.getColumns());
		Table resultTable = new Table(jpaTable.getTableType());
		resultTable.setId(new TableId(jpaTable.getId()));
		resultTable.setColumns(columns);
		resultTable.setName(jpaTable.getName());
		resultTable.setAllMetadata(jpaTable.getMetadata());
		return resultTable;
	}

	private static class ColumnFactory {

		public static List<Column> createColumns(Collection<JPAColumn> columns) {
			List<Column> result = Lists.newArrayList();
			for (JPAColumn jpaColumn : columns) {
				Column newcolumn = createColumn(jpaColumn);
				result.add(newcolumn);
			}
			return result;
		}

		private static Column createColumn(JPAColumn jpaColumn) {
			Column result = null;
			result = new Column(new ColumnLocalId(jpaColumn.getLocalId()), jpaColumn.getDataType(), jpaColumn.getType());
			result.setName(jpaColumn.getName());
			result.setRelationship(jpaColumn.getRelationship());
			result.setAllMetadata(jpaColumn.getMetadata());
			return result;
		}

	}

}
