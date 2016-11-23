package org.gcube.data.analysis.tabulardata.expression.evaluator;

import org.gcube.data.analysis.tabulardata.expression.Expression;

public abstract class BaseExpressionEvaluator<T extends Expression> {

	protected T expression;

	public BaseExpressionEvaluator(T expression) {
		this.expression = expression;
	}
	
}
