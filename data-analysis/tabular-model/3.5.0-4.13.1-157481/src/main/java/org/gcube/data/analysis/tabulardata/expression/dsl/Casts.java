package org.gcube.data.analysis.tabulardata.expression.dsl;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.functions.Cast;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

public class Casts {

	public static Cast toInt(Expression expression){
		return new Cast(expression, new IntegerType() );
	}
	
	public static Cast toNumeric(Expression expression){
		return new Cast(expression, new NumericType());
	}
	
	public static Cast toText(Expression expression){
		return new Cast(expression, new TextType());
	}
	
	public static Cast toGeom(Expression expression){
		return new Cast(expression, new GeometryType());
	}
	
}
