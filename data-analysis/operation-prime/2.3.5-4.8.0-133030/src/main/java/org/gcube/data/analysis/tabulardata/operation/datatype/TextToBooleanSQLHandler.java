package org.gcube.data.analysis.tabulardata.operation.datatype;

import java.util.List;

import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.ValueFormat;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;

public class TextToBooleanSQLHandler extends TypeTransitionSQLHandler {

	public TextToBooleanSQLHandler(SQLExpressionEvaluatorFactory evaluator) {
		super(evaluator);
	}

	@Override
	public String getCopyDataSQLCommand(Table newTable, Table targetTable, Column targetColumn, ValueFormat format) throws MalformedExpressionException{
		StringBuilder sqlBuilder = new StringBuilder();
		List<Column> columnsToCopy = newTable.getColumns();
		String columnNamesSnippet = SQLHelper.generateColumnNameSnippet(columnsToCopy);
		sqlBuilder.append(String.format("INSERT INTO %s (%s) ", newTable.getName(), columnNamesSnippet));
		columnNamesSnippet = generateTypedColumnNameSnippet(newTable, targetTable, targetColumn, format);
		sqlBuilder.append(String.format("SELECT %s FROM %s;", columnNamesSnippet, targetTable.getName()));
		return sqlBuilder.toString();
	}

	private String generateTypedColumnNameSnippet(Table newTable, Table targetTable, Column targetColumn,  ValueFormat format) throws MalformedExpressionException{
		StringBuilder sb = new StringBuilder();
		for (Column column : newTable.getColumns()) {
			if (column.getName().equals(targetColumn.getName())) {
				sb.append(String.format("%s::boolean", this.convert(format, targetTable.getId(), targetColumn)));
			} else
				sb.append(column.getName());
			sb.append(", ");
		}
		sb.delete(sb.length() - 2, sb.length() - 1);
		return sb.toString();
	}

}
