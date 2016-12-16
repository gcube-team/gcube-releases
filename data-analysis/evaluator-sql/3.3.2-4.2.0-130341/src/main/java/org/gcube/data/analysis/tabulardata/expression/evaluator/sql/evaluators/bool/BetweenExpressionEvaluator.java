package org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.bool;

import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.logical.Between;

public class BetweenExpressionEvaluator extends BaseExpressionEvaluator<Between> implements Evaluator<String>  {

	private SQLExpressionEvaluatorFactory factory;
	
	@Override
	public String evaluate() throws EvaluatorException {		
		return String.format("%s BETWEEN SYMMETRIC %s AND %s ", 
				factory.getEvaluator(expression.getArguments().get(0)).evaluate(),
				factory.getEvaluator(expression.getArguments().get(1)).evaluate(),
				factory.getEvaluator(expression.getArguments().get(2)).evaluate());
	}

	public BetweenExpressionEvaluator(Between expression,
			SQLExpressionEvaluatorFactory factory) {
		super(expression);
		this.factory = factory;
	}

	
	
}
