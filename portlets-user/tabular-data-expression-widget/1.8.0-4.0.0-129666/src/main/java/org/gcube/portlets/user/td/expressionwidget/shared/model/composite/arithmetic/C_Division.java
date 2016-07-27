package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.arithmetic;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class C_Division extends C_ArithmeticExpression {

	private static final long serialVersionUID = 6678032753615890456L;
	private String id = "Division";

	public C_Division() {

	}

	public C_Division(C_Expression leftArgument, C_Expression rightArgument) {
		this.leftArgument = leftArgument;
		this.rightArgument = rightArgument;
		if (leftArgument != null && rightArgument != null) {
			this.readableExpression = "Division("
					+ leftArgument.getReadableExpression() + ","
					+ rightArgument.getReadableExpression() + ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.DIVISION;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Division [id=" + id + ", leftArgument=" + leftArgument
				+ ", rightArgument=" + rightArgument + "]";
	}

}
