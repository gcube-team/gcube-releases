package org.gcube.data.access.storagehub.query.sql2.evaluators;

import javax.inject.Singleton;

import org.gcube.common.storagehub.model.expressions.Expression;

@Singleton
public abstract class Evaluator<T extends Expression<?>> {
	
		
	public abstract Class<T> getType();

	public abstract String evaluate(T expr, Iterable<Evaluator<Expression<?>>> evaluators);
	
}
