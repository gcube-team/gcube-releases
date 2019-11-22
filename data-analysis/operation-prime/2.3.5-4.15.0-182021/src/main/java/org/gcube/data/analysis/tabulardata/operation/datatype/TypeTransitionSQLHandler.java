package org.gcube.data.analysis.tabulardata.operation.datatype;

import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.ValueFormat;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

public abstract class TypeTransitionSQLHandler {

	public abstract String getCopyDataSQLCommand(Table newTable, Table targetTable, Column targetColumn, ValueFormat format) throws MalformedExpressionException;

	private SQLExpressionEvaluatorFactory evaluator;
	
	public TypeTransitionSQLHandler(SQLExpressionEvaluatorFactory evaluator){
		this.evaluator = evaluator;
	}
	
	public static TypeTransitionSQLHandler getHandler(DataType sourceType, DataType newType, SQLExpressionEvaluatorFactory evaluator) {
		if (sourceType.getClass().equals(newType.getClass()))
			return new SameTypeSQLHandler(evaluator);
		if (sourceType instanceof TextType && newType instanceof IntegerType)
			return new TextToIntegerSQLHandler(evaluator);
		if (sourceType instanceof TextType && newType instanceof NumericType)
			return new TextToNumericSQLHandler(evaluator);
		if (sourceType instanceof TextType && newType instanceof GeometryType)
			return new TextToGeometrySQLHandler(evaluator);
		if (newType instanceof TextType)
			return new AnyToTextSQLHandler(evaluator);
		if (sourceType instanceof TextType) {
			if (newType instanceof BooleanType)
				return new TextToBooleanSQLHandler(evaluator);
			if (newType instanceof DateType)
				return new TextToDateSQLHandler(evaluator);
			if (newType instanceof IntegerType)
				return new TextToIntegerSQLHandler(evaluator);
			if (newType instanceof NumericType)
				return new TextToNumericSQLHandler(evaluator);
		}
		if (sourceType instanceof IntegerType && newType instanceof NumericType)
			return new IntegerToNumericSQLHandler(evaluator);
		if (sourceType instanceof NumericType && newType instanceof IntegerType )
			return new NumericToIntegerSQLHandler(evaluator);
		throw new UnsupportedOperationException(String.format("Transition from %s to %s is not supported", sourceType.getName(),
				newType.getName()));
	}

	public String getConditionForInvalidEntry(Column targetColumn, ValueFormat format){
		return String.format("NOT(is_valid_regexp(%s, '%s'))",targetColumn.getName(), format.getRegExpr());
	}
		
	public static boolean isSupportedTransition(DataType sourceType, DataType newType, SQLExpressionEvaluatorFactory evaluator){
		try {
			getHandler(sourceType, newType, evaluator);
			return true;
		} catch (UnsupportedOperationException e) {
			return false;
		}
		
	}
	
	protected String convert(ValueFormat format, TableId tableId,  Column column) throws MalformedExpressionException{
		if (format.getConverter()!=null)
			return evaluator.getEvaluator(format.getConverter().getExpression(new ColumnReference(tableId, column.getLocalId()))).evaluate();
		else return column.getName();
	}

}
