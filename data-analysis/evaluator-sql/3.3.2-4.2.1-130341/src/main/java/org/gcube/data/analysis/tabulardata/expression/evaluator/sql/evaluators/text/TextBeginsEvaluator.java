package org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.text;

import org.gcube.data.analysis.tabulardata.expression.composite.text.TextBeginsWith;
import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;

public class TextBeginsEvaluator extends BaseExpressionEvaluator<TextBeginsWith> implements Evaluator<String> {

	private SQLExpressionEvaluatorFactory factory;
	
	
	public TextBeginsEvaluator(TextBeginsWith expression, SQLExpressionEvaluatorFactory factory) {
		super(expression);
		this.factory=factory;
	}

	public String evaluate() throws EvaluatorException {	
		String rightArgument=factory.getEvaluator(expression.getRightArgument()).evaluate().replace("'", "");
		return String.format("(%s LIKE '%s%%')", factory.getEvaluator(expression.getLeftArgument()).evaluate(),
				rightArgument);
	}

}