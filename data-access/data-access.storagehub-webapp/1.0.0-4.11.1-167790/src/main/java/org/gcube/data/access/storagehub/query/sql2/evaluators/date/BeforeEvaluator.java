package org.gcube.data.access.storagehub.query.sql2.evaluators.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Singleton;

import org.gcube.common.storagehub.model.expressions.Expression;
import org.gcube.common.storagehub.model.expressions.date.Before;
import org.gcube.data.access.storagehub.query.sql2.evaluators.Evaluator;

@Singleton
public class BeforeEvaluator extends Evaluator<Before> {
	
	@Override
	public String evaluate(Before expr, Iterable<Evaluator<Expression<?>>> evaluators) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(tz);
		return String.format("node.[%s] < CAST('%s' AS DATE)", expr.getSearchableField().getName(), df.format(expr.getValue().getTime()));
	}
	
	public Evaluator<Expression<?>> getEvaluator(Class<?> type, List<Evaluator<Expression<?>>> evaluators){
		for (Evaluator<Expression<?>> eval: evaluators) {
			if (eval.getType().equals(type)) return eval;
		}
		throw new IllegalStateException("evaluator not found for class "+type.getName());
	}

	@Override
	public Class<Before> getType() {
		return Before.class;
	}
	
}