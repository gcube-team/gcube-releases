package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class C_SubstringPosition extends C_Expression {

	private static final long serialVersionUID = 5871179766613405166L;
	protected String id = "SubstringPosition";

	private C_Expression leftArgument;
	private C_Expression rightArgument;

	public C_SubstringPosition() {
		super();
	}

	public C_SubstringPosition(C_Expression leftArgument,
			C_Expression rightArgument) {
		super();
		this.leftArgument = leftArgument;
		this.rightArgument = rightArgument;
		if (leftArgument != null && rightArgument != null) {
			this.readableExpression = "SubstringPosition("
					+ leftArgument.getReadableExpression() + ","
					+ rightArgument.getReadableExpression() + ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.SUBSTRING_BY_REGEX;
	}

	public String getReturnedDataType() {
		return "Integer";
	}

	@Override
	public String getId() {
		return id;
	}

	public C_Expression getLeftArgument() {
		return leftArgument;
	}

	public void setLeftArgument(C_Expression leftArgument) {
		this.leftArgument = leftArgument;
	}

	public C_Expression getRightArgument() {
		return rightArgument;
	}

	public void setRightArgument(C_Expression rightArgument) {
		this.rightArgument = rightArgument;
	}

	@Override
	public String toString() {
		return "SubstringPosition [id=" + id + ", leftArgument=" + leftArgument
				+ ", rightArgument=" + rightArgument + "]";
	}

}
