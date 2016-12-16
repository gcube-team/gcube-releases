package org.gcube.data.analysis.tabulardata.expression.evaluator;

public interface Evaluator<R>{
	
	public abstract R evaluate() throws EvaluatorException;

}
