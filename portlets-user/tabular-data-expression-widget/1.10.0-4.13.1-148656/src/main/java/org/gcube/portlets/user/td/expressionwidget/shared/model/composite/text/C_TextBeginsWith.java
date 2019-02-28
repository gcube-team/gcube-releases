package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class C_TextBeginsWith extends C_Expression {

	private static final long serialVersionUID = 5871179766613405166L;
	protected String id = "TextBeginsWith";
	protected C_Expression leftArgument;
	protected C_Expression rightArgument;

	public C_TextBeginsWith() {

	}

	public C_TextBeginsWith(C_Expression leftArgument,
			C_Expression rightArgument) {
		this.leftArgument = leftArgument;
		this.rightArgument = rightArgument;
		if (leftArgument != null && rightArgument != null) {
			this.readableExpression = "TextBeginsWith("
					+ leftArgument.getReadableExpression() + ","
					+ rightArgument.getReadableExpression() + ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.BEGINS_WITH;
	}

	public String getReturnedDataType() {
		return "Boolean";
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
		return "TextBeginsWith [id=" + id + ", leftArgument=" + leftArgument
				+ ", rightArgument=" + rightArgument + "]";
	}

}
