package org.gcube.data.analysis.tabulardata.expression.dsl;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.logical.And;
import org.gcube.data.analysis.tabulardata.expression.logical.Or;

public class Logicals {
	
	public static And and(Expression ... expressions){
		return new And(expressions);
	}
	
	public static Or or(Expression ... expressions){
		return new Or(expressions);
	}
}
