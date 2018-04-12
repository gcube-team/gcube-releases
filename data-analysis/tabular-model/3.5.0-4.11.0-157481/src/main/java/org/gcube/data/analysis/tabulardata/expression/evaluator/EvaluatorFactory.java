package org.gcube.data.analysis.tabulardata.expression.evaluator;

import org.gcube.data.analysis.tabulardata.expression.Expression;

public  interface EvaluatorFactory<R>{

	public Evaluator<R> getEvaluator(Expression expression);
	
}
