package org.gcube.data.analysis.tabulardata.expression.evaluator.description.composite;

import java.util.Iterator;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.CompositeExpression;
import org.gcube.data.analysis.tabulardata.expression.evaluator.BaseExpressionEvaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.Evaluator;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.DescriptionExpressionEvaluatorFactory;

public abstract class BaseCompositeDescriptionExpressionEvaluator<T extends CompositeExpression> extends BaseExpressionEvaluator<T> implements Evaluator<String>{

	DescriptionExpressionEvaluatorFactory evaluatorFactory;

	public BaseCompositeDescriptionExpressionEvaluator(DescriptionExpressionEvaluatorFactory evaluatorFactory, T expression) {
		super(expression);
		this.evaluatorFactory = evaluatorFactory;
	}

	protected Iterator<Expression> childExpressionsIterator;

	public String evaluate() {
		StringBuilder result = new StringBuilder();
		childExpressionsIterator = getChildren();
		while (childExpressionsIterator.hasNext()) {
			addExpressionEvaluationToResult(result);
		}
		addEnclosingParenthesis(result);
		return result.toString();
	}

	private void addEnclosingParenthesis(StringBuilder result) {
		result.insert(0, '(');
		result.append(')');
	}

	protected abstract Iterator<Expression> getChildren();

	private void addExpressionEvaluationToResult(StringBuilder result) {
		addPrefixIfNeeded(result);
		Evaluator<String> evaluator = getEvaluator(childExpressionsIterator.next());
		result.append(evaluator.evaluate());
		addSuffixIfNeeded(result);
	}

	protected abstract void addSuffixIfNeeded(StringBuilder result);

	protected abstract void addPrefixIfNeeded(StringBuilder result);

	private Evaluator<String> getEvaluator(Expression expression) {
		return evaluatorFactory.getEvaluator(expression);
	}


	protected final String getOperatorSymbol(Operator op){
		if (op==null) throw new EvaluatorException("Operator "+op+" not supported");
		return op.getSymbol();
		
	}

}
