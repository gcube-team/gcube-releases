package org.gcube.data.analysis.tabulardata.expression.evaluator.description.composite;

import org.gcube.data.analysis.tabulardata.expression.composite.text.SubstringByIndex;
import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.DescriptionExpressionEvaluatorFactory;

public class SubstringByIndexEvaluator extends BaseExpressionEvaluator<SubstringByIndex> implements Evaluator<String>{

	private DescriptionExpressionEvaluatorFactory factory;

	public SubstringByIndexEvaluator(SubstringByIndex expression,
			DescriptionExpressionEvaluatorFactory factory) {
		super(expression);
		this.factory = factory;
	}
	
	@Override
	public String evaluate() throws EvaluatorException {		
		return String.format("Substring (%s, from %s to %s)",
				factory.getEvaluator(expression.getSourceString()).evaluate(),
				factory.getEvaluator(expression.getFromIndex()).evaluate(),
				factory.getEvaluator(expression.getToIndex()).evaluate());
	}

}
