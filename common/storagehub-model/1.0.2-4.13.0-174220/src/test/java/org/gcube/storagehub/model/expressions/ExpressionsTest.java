package org.gcube.storagehub.model.expressions;

import java.util.Calendar;

import org.gcube.common.storagehub.model.expressions.Expression;
import org.gcube.common.storagehub.model.expressions.GenericSearchableItem;
import org.gcube.common.storagehub.model.expressions.date.Before;
import org.gcube.common.storagehub.model.expressions.logical.And;
import org.gcube.common.storagehub.model.expressions.text.Contains;
import org.junit.Test;

public class ExpressionsTest {

	
	@Test
	public void contains() {
		Expression<Boolean> expr = new Contains(GenericSearchableItem.get().title, "Data");
		System.out.println( expr.toString());
	}
	
	@Test
	public void and() {
		
		Expression<Boolean> cont1 = new Contains(GenericSearchableItem.get().title, "Data");
		Expression<Boolean> before = new Before(GenericSearchableItem.get().creationTime, Calendar.getInstance());
		Expression<Boolean> andExpr = new And(cont1, before);
		System.out.println( andExpr.toString());
	}
	
}
