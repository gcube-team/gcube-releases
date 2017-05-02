package org.gcube.data.analysis.tabulardata.expression.dsl;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.condtional.Case;
import org.gcube.data.analysis.tabulardata.expression.composite.condtional.Case.WhenConstruct;

public class Conditionals {

	public static Case conditional(WhenConstruct ... whenConstructs){
		return new Case(whenConstructs);
	}
	
	public static WhenConstruct whenThen(Expression when, Expression then){
		return new WhenConstruct(when, then);
	}
		
}
