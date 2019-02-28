package org.gcube.data.analysis.tabulardata.expression.evaluator.description.composite;

import org.gcube.data.analysis.tabulardata.expression.composite.text.TextReplaceMatchingRegex;
import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.DescriptionExpressionEvaluatorFactory;

public class TextReplaceMatchingRegexEvaluator extends BaseExpressionEvaluator<TextReplaceMatchingRegex> implements Evaluator<String>{

	private DescriptionExpressionEvaluatorFactory factory;

	public TextReplaceMatchingRegexEvaluator(
			TextReplaceMatchingRegex expression,
			DescriptionExpressionEvaluatorFactory factory) {
		super(expression);
		this.factory = factory;
	}
	
	@Override
	public String evaluate() throws EvaluatorException {
		return String.format("(regex_replace(%s,%s,%s,'g'))", 
				factory.getEvaluator(expression.getToCheckText()).evaluate(),
				factory.getEvaluator(expression.getRegexp()).evaluate(),
				factory.getEvaluator(expression.getReplacingValue()).evaluate());
	}
	
	
}
