package org.gcube.data.analysis.tabulardata.expression.evaluator.description.composite;

import java.util.Arrays;
import java.util.Iterator;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.BinaryExpression;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.DescriptionExpressionEvaluatorFactory;

public class BinaryExpressionEvaluator extends BaseCompositeDescriptionExpressionEvaluator<BinaryExpression>{

	
	
	public BinaryExpressionEvaluator(
			DescriptionExpressionEvaluatorFactory evaluatorFactory,
			BinaryExpression expression) {
		super(evaluatorFactory, expression);
		// TODO Auto-generated constructor stub
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
		return Arrays.asList(new Expression[]{
				expression.getLeftArgument(),
				expression.getRightArgument()
		}).iterator();
	}
}
