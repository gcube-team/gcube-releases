package org.gcube.data.analysis.tabulardata.expression.evaluator.description.composite;

import java.util.Iterator;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.MultipleArgumentsExpression;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.DescriptionExpressionEvaluatorFactory;

public class MultipleArgumentExpressionEvaluator extends BaseCompositeDescriptionExpressionEvaluator<MultipleArgumentsExpression>{

	public MultipleArgumentExpressionEvaluator(
			DescriptionExpressionEvaluatorFactory evaluatorFactory,
			MultipleArgumentsExpression expression) {
		super(evaluatorFactory, expression);	
	}

	@Override
	protected void addSuffixIfNeeded(StringBuilder result) {
		if (childExpressionsIterator.hasNext())
			result.append(" "+getOperatorSymbol(expression.getOperator())+" ");
	}
	
	@Override
	protected void addPrefixIfNeeded(StringBuilder result) {
		// NOP
	}
	
	@Override
	protected Iterator<Expression> getChildren() {		
		return expression.getArguments().iterator();
	}
}
