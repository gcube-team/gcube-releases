package org.gcube.data.analysis.tabulardata.operation.time;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.ValueFormat;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;

public abstract class PeriodTypeHelper {
	
	public abstract PeriodType getManagedPeriodType();
	
	private CubeManager cubeManager;

	public PeriodTypeHelper(CubeManager cubeManager) {
		this.cubeManager = cubeManager;
	}
	
	public final String getFillValidationColumnSQL(Table targetTable, String validationColumnName,
			 Column targetColumn, ValueFormat timeFormat, SQLExpressionEvaluatorFactory sqlEvaluatorFactory) throws MalformedExpressionException{
		String columnToVerify = targetColumn.getName();
		if (timeFormat.getConverter()!=null)
			columnToVerify = sqlEvaluatorFactory.getEvaluator(
							timeFormat.getConverter().getExpression(new ColumnReference(targetTable.getId(), targetColumn.getLocalId()))).evaluate();
		return String.format("UPDATE %1$s SET %2$s = true WHERE  %3$s in (SELECT %4$s_code From %4$s) ", targetTable.getName(), validationColumnName,
				columnToVerify, this.getManagedPeriodType().getName());
		 
	}

	
	public Table createTimeCodelist(){
		return cubeManager.getTimeTable(getManagedPeriodType());
	}
	
	public String getUpdateDimensionColumnSQL(Column targetColumn, Table newTable, String timeDimensionColumnName,String timeTableName, ValueFormat timeFormat, SQLExpressionEvaluatorFactory sqlEvaluatorFactory) throws MalformedExpressionException{
		String columnToVerify = targetColumn.getName();
		if (timeFormat.getConverter()!=null)
			columnToVerify = sqlEvaluatorFactory.getEvaluator(
							timeFormat.getConverter().getExpression(new ColumnReference(newTable.getId(), targetColumn.getLocalId()))).evaluate();

		return String
				.format("UPDATE %1$s SET %2$s = refCol.id FROM (SELECT id,  %5$s_code as val FROM %3$s) as refCol"
						+ " WHERE refCol.val= %4$s;",
						newTable.getName(), timeDimensionColumnName, timeTableName,
						columnToVerify, getManagedPeriodType().getName().toLowerCase());
				
	}
	
}
