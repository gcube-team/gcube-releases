package org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.text;

import org.gcube.data.analysis.tabulardata.expression.composite.text.TextMatchPosixRegexp;
import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;

public class TextMatchPosixregexpEvaluator  extends BaseExpressionEvaluator<TextMatchPosixRegexp> implements Evaluator<String> {
	
	private SQLExpressionEvaluatorFactory factory;
	

	public TextMatchPosixregexpEvaluator(TextMatchPosixRegexp expression,
			SQLExpressionEvaluatorFactory factory) {
		super(expression);
		this.factory = factory;
	}




	public String evaluate() throws EvaluatorException{		
		return String.format("(%s ~ %s)", factory.getEvaluator(expression.getLeftArgument()).evaluate(),
				factory.getEvaluator(expression.getRightArgument()).evaluate());
	}

}