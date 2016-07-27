package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.arithmetic;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class C_Addition extends C_ArithmeticExpression {

	private static final long serialVersionUID = 3148009575987859751L;

	private String id = "Addition";

	public C_Addition() {

	}

	public C_Addition(C_Expression leftArgument, C_Expression rightArgument) {
		this.leftArgument = leftArgument;
		this.rightArgument = rightArgument;
		if (leftArgument != null && rightArgument != null) {
			this.readableExpression = "Addition("
					+ leftArgument.getReadableExpression() + ","
					+ rightArgument.getReadableExpression() + ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.ADDITION;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Addition [id=" + id + ", leftArgument=" + leftArgument
				+ ", rightArgument=" + rightArgument + "]";
	}

}
