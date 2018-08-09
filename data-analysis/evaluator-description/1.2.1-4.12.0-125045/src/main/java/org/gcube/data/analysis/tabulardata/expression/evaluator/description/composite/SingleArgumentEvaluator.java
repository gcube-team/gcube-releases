package org.gcube.data.analysis.tabulardata.expression.evaluator.description.composite;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.UnaryExpression;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.DescriptionExpressionEvaluatorFactory;

public class SingleArgumentEvaluator extends BaseCompositeDescriptionExpressionEvaluator<UnaryExpression> {

	private static final HashSet<Operator> prefixOperators=new HashSet<>(Arrays.asList(new Operator[]{
			Operator.TRIM,
			Operator.NOT,
			Operator.LENGTH,
			Operator.UPPER,
			Operator.MD5,
			Operator.LOWER,
			Operator.AVG,
			Operator.COUNT,
			Operator.MAX,
			Operator.MIN,
			Operator.SUM,
			Operator.ST_EXTENT,
			Operator.SOUNDEX
	}));
	
	
	public SingleArgumentEvaluator(
			DescriptionExpressionEvaluatorFactory evaluatorFactory,
			UnaryExpression expression) {
		super(evaluatorFactory, expression);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void addPrefixIfNeeded(StringBuilder result) {
		if(prefixOperators.contains(expression.getOperator())) result.append(" "+getOperatorSymbol(expression.getOperator())+" ");
	}
	
	@Override
	protected void addSuffixIfNeeded(StringBuilder result) {
		if(!prefixOperators.contains(expression.getOperator())) result.append(" "+getOperatorSymbol(expression.getOperator())+" ");
	}
	
	
	@Override
	protected Iterator<Expression> getChildren() {			
		return Arrays.asList(new Expression[]{
				expression.getArgument(),				
		}).iterator();
	}
	
}
