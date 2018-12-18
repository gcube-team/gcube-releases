package org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.text;

import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByIndex;
import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;

public class SubstringByIndexEvaluator extends BaseExpressionEvaluator<SubstringByIndex> implements Evaluator<String>{

	private SQLExpressionEvaluatorFactory factory;

	public SubstringByIndexEvaluator(SubstringByIndex expression,
			SQLExpressionEvaluatorFactory factory) {
		super(expression);
		this.factory = factory;
	}

	@Override
	public String evaluate() throws EvaluatorException {
		return String.format("(substring(%s from %s for %s))", 
				factory.getEvaluator(expression.getSourceString()).evaluate(),
				factory.getEvaluator(expression.getFromIndex()).evaluate(),
				factory.getEvaluator(expression.getToIndex()).evaluate());
	}
	
}