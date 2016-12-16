package org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.text;

import org.gcube.data.analysis.tabulardata.expression.composite.text.RepeatText;
import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;

public class RepeatTextEvaluator extends BaseExpressionEvaluator<RepeatText> implements Evaluator<String>{

	private SQLExpressionEvaluatorFactory factory;

	public RepeatTextEvaluator(RepeatText expression,
			SQLExpressionEvaluatorFactory factory) {
		super(expression);
		this.factory = factory;
	}

	@Override
	public String evaluate() throws EvaluatorException {
		return String.format("(repeat(%s,%s))", 
				factory.getEvaluator(expression.getValue()).evaluate(),
				factory.getEvaluator(expression.getTimes()).evaluate());
	}

}
