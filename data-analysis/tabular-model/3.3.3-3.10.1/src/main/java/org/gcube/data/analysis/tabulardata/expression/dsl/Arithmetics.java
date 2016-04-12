package org.gcube.data.analysis.tabulardata.expression.dsl;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Addition;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Exponentiation;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Multiplication;
import org.gcube.data.analysis.tabulardata.expression.composite.arithmetic.Subtraction;

public class Arithmetics {
 
	public static Subtraction sub(Expression leftArg, Expression rightArg){
		return new Subtraction(leftArg, rightArg);
	}
	
	public static Addition add(Expression leftArg, Expression rightArg){
		return new Addition(leftArg, rightArg);
	}
	
	public static Exponentiation exp(Expression leftArg, Expression rightArg){
		return new Exponentiation(leftArg, rightArg);
	}
	
	public static Multiplication mul(Expression leftArg, Expression rightArg){
		return new Multiplication(leftArg, rightArg);
	}
}
