package org.gcube.data.access.fs;

import java.util.Calendar;

import javax.inject.Inject;

import org.gcube.common.storagehub.model.expressions.Expression;
import org.gcube.common.storagehub.model.expressions.GenericSearchableItem;
import org.gcube.common.storagehub.model.expressions.date.Before;
import org.gcube.common.storagehub.model.expressions.logical.And;
import org.gcube.common.storagehub.model.expressions.text.Contains;
import org.gcube.data.access.storagehub.query.sql2.evaluators.Evaluators;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(WeldJunit4Runner.class)
public class Expressions {

	private static Logger log = LoggerFactory.getLogger(Expression.class);
	
	@Inject 
	Evaluators evaluators;
	
	
	@Test
	public void test() {
		
		evaluators.getEvaluators().forEach(s-> System.out.println(s.getType().toString()));
		
		Expression<Boolean> cont1 = new Contains(GenericSearchableItem.get().title, "Data");
		Expression<Boolean> before = new Before(GenericSearchableItem.get().creationTime, Calendar.getInstance());
		Expression<Boolean> andExpr = new And(cont1, before);
		System.out.println(evaluators.evaluate(andExpr));
		
	}
	
	
}
