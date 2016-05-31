package org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators;

import org.gcube.data.analysis.tabulardata.expression.NotEvaluableDataTypeException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.mapping.SQLModelMapper;

public class CastExpressionEvaluator extends BaseExpressionEvaluator<Cast> implements Evaluator<String>{

	private SQLExpressionEvaluatorFactory factory;
	private SQLModelMapper sqlModelMapper;
	
	

	public CastExpressionEvaluator(Cast expression,
			SQLExpressionEvaluatorFactory factory, SQLModelMapper sqlModelMapper) {
		super(expression);
		this.factory = factory;
		this.sqlModelMapper = sqlModelMapper;
	}



	@Override
	public String evaluate() throws EvaluatorException {
		String evaluatedArgument=factory.getEvaluator(expression.getArgument()).evaluate();
		try{
			DataType sourceType=expression.getArgument().getReturnedDataType();
			DataType newType=expression.getCastToType();
			String evaluatedTargetType=sqlModelMapper.translateDataTypeToSQL(newType);
			if (sourceType.getClass().equals(newType.getClass())) return evaluatedArgument;
			if (sourceType instanceof TextType && newType instanceof NumericType) return String.format("(CAST(replace(%s,',','.') AS %s))",evaluatedArgument, evaluatedTargetType);
			if (sourceType instanceof GeometryType && newType instanceof TextType ) return String.format("ST_AsText(%s)",evaluatedArgument); 
			return String.format("(CAST (%s AS %s ))", evaluatedArgument, evaluatedTargetType);
		}catch(NotEvaluableDataTypeException e){
			throw new EvaluatorException(String.format("Unable to evaluate cast, expression %s is not typed",evaluatedArgument));
		}
	}
	
}
