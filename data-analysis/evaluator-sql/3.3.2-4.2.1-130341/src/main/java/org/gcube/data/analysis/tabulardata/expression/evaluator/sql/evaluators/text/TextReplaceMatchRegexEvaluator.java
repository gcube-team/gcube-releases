package org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.text;

import org.gcube.data.analysis.tabulardata.expression.composite.text.TextReplaceMatchingRegex;
import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;

public class TextReplaceMatchRegexEvaluator extends BaseExpressionEvaluator<TextReplaceMatchingRegex> implements Evaluator<String>{

	private SQLExpressionEvaluatorFactory factory;

	public TextReplaceMatchRegexEvaluator(TextReplaceMatchingRegex expression,
			SQLExpressionEvaluatorFactory factory) {
		super(expression);
		this.factory = factory;
	}
	
	@Override
	public String evaluate() throws EvaluatorException {
		return String.format("(regexp_replace(%s,%s,%s,'g'))", 
				factory.getEvaluator(expression.getToCheckText()).evaluate(),
				factory.getEvaluator(expression.getRegexp()).evaluate(),
				factory.getEvaluator(expression.getReplacingValue()).evaluate());
	}
}
