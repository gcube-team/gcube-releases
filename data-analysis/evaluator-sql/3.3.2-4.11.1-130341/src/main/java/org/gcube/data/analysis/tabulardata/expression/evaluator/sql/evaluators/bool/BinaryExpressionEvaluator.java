package org.gcube.data.analysis.tabulardata.expression.evaluator.sql.evaluators.bool;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.BinaryExpression;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;

public class BinaryExpressionEvaluator extends BaseCompositeExpressionEvaluator<BinaryExpression>{

	private static final HashSet<Operator> prefixOperators=new HashSet<>(Arrays.asList(new Operator[]{
			Operator.SIMILARITY,
			Operator.LEVENSHTEIN
		}));
	
	
	
	public BinaryExpressionEvaluator(
			SQLExpressionEvaluatorFactory evaluatorFactory,
			BinaryExpression expression) {
		super(evaluatorFactory, expression);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void addMiddleOperatorIfNeeded(StringBuilder result) {
		if (childExpressionsIterator.hasNext()&&(!prefixOperators.contains(expression.getOperator())))
			result.append(" "+getOperatorSymbol(expression.getOperator())+" ");
	}
	
	@Override
	protected void addPrefixIfNeeded(StringBuilder result) {
		if(prefixOperators.contains(expression.getOperator()))
			result.append(" "+getOperatorSymbol(expression.getOperator())+" ( ");
	}
	
	@Override
	protected Iterator<Expression> getChildren() {			
		return Arrays.asList(new Expression[]{
				expression.getLeftArgument(),
				expression.getRightArgument()
		}).iterator();
	}
	
	@Override
	protected void addSuffixIfNeeded(StringBuilder result) {
		if(prefixOperators.contains(expression.getOperator()))
			result.append(")");
	}
}
