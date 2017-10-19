package org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.text;

import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByRegex;
import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;

public class SubstringByRegexEvaluator extends BaseExpressionEvaluator<SubstringByRegex> implements Evaluator<String>{

	private SQLExpressionEvaluatorFactory factory;

	public SubstringByRegexEvaluator(SubstringByRegex expression,
			SQLExpressionEvaluatorFactory factory) {
		super(expression);
		this.factory = factory;
	}

	@Override
	public String evaluate() throws EvaluatorException {
		return String.format("(substring(%s from %s))", 
				factory.getEvaluator(expression.getLeftArgument()).evaluate(),
				factory.getEvaluator(expression.getRightArgument()).evaluate());
	}

}
