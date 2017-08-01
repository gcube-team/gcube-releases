package org.gcube.data.analysis.tabulardata.expression.composite.arithmetic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Division extends ArithmeticExpression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2594987342675251973L;


	
	@SuppressWarnings("unused")
	private Division() {}

	public Division(Expression leftArgument, Expression rightArgument) {
		super(leftArgument, rightArgument);	
	}

	@Override
	public Operator getOperator() {		
		return Operator.DIVISION;
	}
	
}
