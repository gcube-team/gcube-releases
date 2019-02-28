package org.gcube.data.access.storagehub.query.sql2.evaluators.logical;

import javax.inject.Singleton;

import org.gcube.common.storagehub.model.expressions.Expression;
import org.gcube.common.storagehub.model.expressions.logical.ISDescendant;
import org.gcube.data.access.storagehub.query.sql2.evaluators.Evaluator;

@Singleton
public class ISDescendantEvaluator extends Evaluator<ISDescendant> {

	@Override
	public String evaluate(ISDescendant expr, Iterable<Evaluator<Expression<?>>> evaluators) {
		
		return String.format("ISDESCENDANTNODE('%s')", expr.getPath().toPath());
	}
	
	@Override
	public Class<ISDescendant> getType() {
		return ISDescendant.class;
	}


}
