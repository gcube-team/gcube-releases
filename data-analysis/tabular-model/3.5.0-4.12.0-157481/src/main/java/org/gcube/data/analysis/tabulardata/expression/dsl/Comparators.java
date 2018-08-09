package org.gcube.data.analysis.tabulardata.expression.dsl;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.GreaterOrEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.GreaterThan;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.LessOrEquals;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.LessThan;

public class Comparators {

	public static LessOrEquals lessEq(Expression leftArgument, Expression rightArgument){
		return new LessOrEquals(leftArgument, rightArgument);
	}
	
	public static LessThan less(Expression leftArgument, Expression rightArgument){
		return new LessThan(leftArgument, rightArgument);
	}
	
	public static GreaterOrEquals greaterEq(Expression leftArgument, Expression rightArgument){
		return new GreaterOrEquals(leftArgument, rightArgument);
	}
	
	public static GreaterThan greater(Expression leftArgument, Expression rightArgument){
		return new GreaterThan(leftArgument, rightArgument);
	}
	
	public static Equals eq(Expression leftArgument, Expression rightArgument){
		return new Equals(leftArgument, rightArgument);
	}
	
}
