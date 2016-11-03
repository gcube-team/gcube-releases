package org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.text;

import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchSQLRegexp;
import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;

public class TextMatchSQLRegexpEvaluator extends BaseExpressionEvaluator<TextMatchSQLRegexp> implements Evaluator<String> {
	
	private SQLExpressionEvaluatorFactory factory;
	

	public TextMatchSQLRegexpEvaluator(TextMatchSQLRegexp expression,
			SQLExpressionEvaluatorFactory factory) {
		super(expression);
		this.factory = factory;
	}




	public String evaluate() throws EvaluatorException{		
		return String.format("(%s SIMILAR TO %s)", factory.getEvaluator(expression.getLeftArgument()).evaluate(),
				factory.getEvaluator(expression.getRightArgument()).evaluate());
	}

}
