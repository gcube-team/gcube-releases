package org.gcube.data.analysis.tabulardata.expression.composite.arithmetic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Modulus extends ArithmeticExpression {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7343493694227374362L;

	@SuppressWarnings("unused")
	private Modulus() {}

	public Modulus(Expression leftArgument, Expression rightArgument) {
		super(leftArgument, rightArgument);	
	}

	@Override
	public Operator getOperator() {		
		return Operator.MODULUS;
	}
	
}
