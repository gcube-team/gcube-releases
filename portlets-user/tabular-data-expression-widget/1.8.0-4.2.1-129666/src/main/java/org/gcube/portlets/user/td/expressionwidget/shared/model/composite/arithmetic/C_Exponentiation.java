package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.arithmetic;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class C_Exponentiation extends C_ArithmeticExpression {

	private static final long serialVersionUID = -8124478792466278925L;
	private String id = "Exponentiation";

	public C_Exponentiation() {

	}

	/**
	 * 
	 * @param argument
	 */
	public C_Exponentiation(C_Expression leftArgument,
			C_Expression rightArgument) {
		this.leftArgument = leftArgument;
		this.rightArgument = rightArgument;
		if (leftArgument != null && rightArgument != null) {
			this.readableExpression = "Exponentiation("
					+ leftArgument.getReadableExpression() + ","
					+ rightArgument.getReadableExpression() + ")";
		}

	}

	public C_OperatorType getOperator() {
		return C_OperatorType.EXPONENTIATION;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Exponentiation [id=" + id + ", leftArgument=" + leftArgument
				+ ", rightArgument=" + rightArgument + "]";
	}

}
