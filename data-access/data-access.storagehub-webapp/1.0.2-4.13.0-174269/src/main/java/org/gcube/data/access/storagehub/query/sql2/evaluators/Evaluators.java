package org.gcube.data.access.storagehub.query.sql2.evaluators;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.common.storagehub.model.expressions.Expression;

@Singleton
public class Evaluators {

	public static Evaluator<Expression<?>> getEvaluator(Class<?> type, Iterable<Evaluator<Expression<?>>> evaluators){
		for (Evaluator<Expression<?>> eval: evaluators) {
			if (eval.getType().equals(type)) return eval;
		}
		throw new IllegalStateException("evaluator not found for class "+type.getName());
	}
	
	@Inject 
	Instance<Evaluator<?>> evaluators;
	
	public String evaluate(Expression<?> expression) {
		for (Evaluator eval: evaluators) {
			if (eval.getType().equals(expression.getClass()))
				return eval.evaluate(expression, evaluators);
		}
		throw new IllegalStateException("Evaluator not found for expression type "+expression.getClass());
	}

	public Instance<Evaluator<?>> getEvaluators() {
		return evaluators;
	}
	
	
	
}
