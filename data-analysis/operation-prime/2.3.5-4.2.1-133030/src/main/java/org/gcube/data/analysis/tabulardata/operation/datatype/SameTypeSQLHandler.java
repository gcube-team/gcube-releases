package org.gcube.data.analysis.tabulardata.operation.datatype;

import java.util.List;

import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.ValueFormat;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;

public class SameTypeSQLHandler extends TypeTransitionSQLHandler {

	public SameTypeSQLHandler(SQLExpressionEvaluatorFactory evaluator) {
		super(evaluator);
	}

	@Override
	public String getCopyDataSQLCommand(Table newTable, Table targetTable, Column targetColumn, ValueFormat format) {
		StringBuilder sqlBuilder = new StringBuilder();
		List<Column> columnsToCopy = newTable.getColumns();
		String columnNamesSnippet = SQLHelper.generateColumnNameSnippet(columnsToCopy);
		sqlBuilder.append(String.format("INSERT INTO %s (%s) ", newTable.getName(), columnNamesSnippet));
		columnNamesSnippet = SQLHelper.generateColumnNameSnippet(columnsToCopy);
		sqlBuilder.append(String.format("SELECT %s FROM %s;", columnNamesSnippet, targetTable.getName()));
		return sqlBuilder.toString();
	}

	@Override	
	public String getConditionForInvalidEntry(Column targetColumn, ValueFormat format) {
		return "FALSE";
	}

}
