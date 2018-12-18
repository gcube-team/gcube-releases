package org.gcube.data.analysis.tabulardata.expression.evaluator.description.composite;

import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByRegex;
import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.DescriptionExpressionEvaluatorFactory;

public class SubstringByRegexEvaluator extends BaseExpressionEvaluator<SubstringByRegex> implements Evaluator<String>{

	private DescriptionExpressionEvaluatorFactory factory;

	public SubstringByRegexEvaluator(SubstringByRegex expression,
			DescriptionExpressionEvaluatorFactory factory) {
		super(expression);
		this.factory = factory;
	}

	@Override
	public String evaluate() throws EvaluatorException {
		return String.format("(Substring (%s,%s(REGEXP)))", 
				factory.getEvaluator(expression.getLeftArgument()).evaluate(),
				factory.getEvaluator(expression.getRightArgument()).evaluate());
	}
	
	
}
