package org.gcube.data.analysis.tabulardata.expression.composite.arithmetic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Multiplication extends ArithmeticExpression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7719274202793834266L;

	@SuppressWarnings("unused")
	private Multiplication() {}

	public Multiplication(Expression leftArgument, Expression rightArgument) {
		super(leftArgument, rightArgument);	
	}

	@Override
	public Operator getOperator() {		
		return Operator.MULTIPLICATION;
	}
	
}
