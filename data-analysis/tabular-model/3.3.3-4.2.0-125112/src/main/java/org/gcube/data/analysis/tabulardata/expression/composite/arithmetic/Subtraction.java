package org.gcube.data.analysis.tabulardata.expression.composite.arithmetic;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.Operator;
@XmlRootElement
public class Subtraction extends ArithmeticExpression {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1256644830788961321L;

	@SuppressWarnings("unused")
	private Subtraction() {}

	public Subtraction(Expression leftArgument, Expression rightArgument) {
		super(leftArgument, rightArgument);	
	}

	@Override
	public Operator getOperator() {		
		return Operator.SUBTRACTION;
	}
	
}
