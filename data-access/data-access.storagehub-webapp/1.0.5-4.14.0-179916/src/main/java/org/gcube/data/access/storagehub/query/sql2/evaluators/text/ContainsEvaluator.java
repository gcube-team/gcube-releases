package org.gcube.data.access.storagehub.query.sql2.evaluators.text;

import javax.inject.Singleton;

import org.gcube.common.storagehub.model.expressions.Expression;
import org.gcube.common.storagehub.model.expressions.text.Contains;
import org.gcube.data.access.storagehub.query.sql2.evaluators.Evaluator;

@Singleton
public class ContainsEvaluator extends Evaluator<Contains> {
	
	
	
	@Override
	public String evaluate(Contains expr, Iterable<Evaluator<Expression<?>>> evaluators) {
		return String.format("node.[%s] LIKE '%%%s%%'", expr.getSearchableField().getName(), expr.getValue());
	}
	
	public Evaluator<Expression<?>> getEvaluator(Class<?> type, Iterable<Evaluator<Expression<?>>> evaluators){
		for (Evaluator<Expression<?>> eval: evaluators) {
			if (eval.getType().equals(type)) return eval;
		}
		throw new IllegalStateException("evaluator not found for class "+type.getName());
	}

	@Override
	public Class<Contains> getType() {
		return Contains.class;
	}
	
}
