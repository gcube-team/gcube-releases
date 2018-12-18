package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.arithmetic;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class C_Modulus extends C_ArithmeticExpression {

	private static final long serialVersionUID = -4068595785080246810L;
	private String id = "Modulus";

	public C_Modulus() {

	}

	public C_Modulus(C_Expression leftArgument, C_Expression rightArgument) {
		this.leftArgument = leftArgument;
		this.rightArgument = rightArgument;
		if (leftArgument != null && rightArgument != null) {
			this.readableExpression = "Modulus("
					+ leftArgument.getReadableExpression() + ","
					+ rightArgument.getReadableExpression() + ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.MODULUS;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Modulus [id=" + id + ", leftArgument=" + leftArgument
				+ ", rightArgument=" + rightArgument + "]";
	}

}
