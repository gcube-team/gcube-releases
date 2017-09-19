package org.gcube.data.analysis.tabulardata.operation.datatype;

import java.util.List;

import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.ValueFormat;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;

public class AnyToTextSQLHandler extends TypeTransitionSQLHandler {

	public AnyToTextSQLHandler(SQLExpressionEvaluatorFactory evaluator) {
		super(evaluator);
	}

	@Override
	public String getCopyDataSQLCommand(Table newTable, Table targetTable, Column targetColumn, ValueFormat format) {
		StringBuilder sqlBuilder = new StringBuilder();
		List<Column> columnsToCopy = newTable.getColumns();
		String columnNamesSnippet = SQLHelper.generateColumnNameSnippet(columnsToCopy);
		sqlBuilder.append(String.format("INSERT INTO %s (%s) ", newTable.getName(), columnNamesSnippet));
		columnNamesSnippet = generateTypedColumnNameSnippet(newTable,targetColumn);
		sqlBuilder.append(String.format("SELECT %s FROM %s;", columnNamesSnippet, targetTable.getName()));
		return sqlBuilder.toString();
	}
	
	private String generateTypedColumnNameSnippet(Table newTable, Column targetColumn) {
		StringBuilder sb = new StringBuilder();
		for (Column column : newTable.getColumns()) 
			sb.append(column.getName()).append(", ");
		sb.delete(sb.length() - 2, sb.length() - 1);
		return sb.toString();
	}
	
	@Override
	public String getConditionForInvalidEntry(Column targetColumn, ValueFormat format) {
		return "FALSE";
	}
	
}
