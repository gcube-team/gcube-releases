package org.gcube.data.analysis.tabulardata.expression.evaluator.description.composite;

import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.DescriptionExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;

public class CastExpressionEvaluator extends BaseExpressionEvaluator<Cast> implements Evaluator<String>{

	private DescriptionExpressionEvaluatorFactory factory;

	public CastExpressionEvaluator(Cast expression,
			DescriptionExpressionEvaluatorFactory factory) {
		super(expression);
		this.factory = factory;
	}
	
	@Override
	public String evaluate() throws EvaluatorException {
		return String.format("((%s) %s)", 
				expression.getCastToType().getName(),factory.getEvaluator(expression.getArgument()).evaluate());
	}
	
}
