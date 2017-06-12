package org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.bool;

import java.util.Iterator;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.MultipleArgumentsExpression;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;

public class MultipleArgumentsExpressionEvaluator extends BaseCompositeExpressionEvaluator<MultipleArgumentsExpression>{

	public MultipleArgumentsExpressionEvaluator(
			SQLExpressionEvaluatorFactory evaluatorFactory,
			MultipleArgumentsExpression expression) {
		super(evaluatorFactory, expression);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void addMiddleOperatorIfNeeded(StringBuilder result) {
		if (childExpressionsIterator.hasNext())
			result.append(" "+getOperatorSymbol(expression.getOperator())+" ");
	}
	
	@Override
	protected void addSuffixIfNeeded(StringBuilder result) {
		//NOP
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
