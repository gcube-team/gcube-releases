package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.comparable;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class C_LessThan extends C_Expression {
	private static final long serialVersionUID = 6420164520127827750L;
	protected String id = "LessThan";
	protected C_Expression leftArgument;
	protected C_Expression rightArgument;

	public C_LessThan() {

	}

	public C_LessThan(C_Expression leftArgument, C_Expression rightArgument) {
		this.leftArgument = leftArgument;
		this.rightArgument = rightArgument;
		if (leftArgument != null && rightArgument != null) {
			this.readableExpression = "LessThan("
					+ leftArgument.getReadableExpression() + ","
					+ rightArgument.getReadableExpression() + ")";
		}

	}

	public C_OperatorType getOperator() {
		return C_OperatorType.LESSER;
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
		return "LessThan [id=" + id + ", leftArgument=" + leftArgument
				+ ", rightArgument=" + rightArgument + "]";
	}

}
