package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.arithmetic;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class C_Multiplication extends C_ArithmeticExpression {

	private static final long serialVersionUID = 4822682666900687606L;
	private String id = "Multiplication";

	public C_Multiplication() {

	}

	public C_Multiplication(C_Expression leftArgument,
			C_Expression rightArgument) {
		this.leftArgument = leftArgument;
		this.rightArgument = rightArgument;
		if (leftArgument != null && rightArgument != null) {
			this.readableExpression = "Multiplication("
					+ leftArgument.getReadableExpression() + ","
					+ rightArgument.getReadableExpression() + ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.MULTIPLICATION;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Multiplication [id=" + id + ", leftArgument=" + leftArgument
				+ ", rightArgument=" + rightArgument + "]";
	}

}
