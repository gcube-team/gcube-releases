package org.gcube.data.access.storagehub.query.sql2.evaluators.logical;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import org.gcube.common.storagehub.model.expressions.Expression;
import org.gcube.common.storagehub.model.expressions.logical.Or;
import org.gcube.data.access.storagehub.query.sql2.evaluators.Evaluator;
import org.gcube.data.access.storagehub.query.sql2.evaluators.Evaluators;

@Singleton
public class OrEvaluator extends Evaluator<Or> {
	
	

	@Override
	public String evaluate(Or expr, Iterable<Evaluator<Expression<?>>> evaluators) {
		List<String> evaluated = new ArrayList<>();
		for (Expression<?> subExpression :expr.getExpressions()) {
			Evaluator<Expression<?>> eval = Evaluators.getEvaluator(subExpression.getClass(), evaluators);
			evaluated.add(eval.evaluate(subExpression, evaluators));
		}
		
		return "("+evaluated.stream().map(Object::toString).collect(Collectors.joining(" or ")).toString()+")";
	}
	
	@Override
	public Class<Or> getType() {
		return Or.class;
	}

}
