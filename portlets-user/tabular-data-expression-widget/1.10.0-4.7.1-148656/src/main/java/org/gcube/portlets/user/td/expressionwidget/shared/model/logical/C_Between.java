package org.gcube.portlets.user.td.expressionwidget.shared.model.logical;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class C_Between extends C_Expression {

	private static final long serialVersionUID = 5367101215792568854L;
	protected String id = "Between";
	protected C_Expression leftArgument;
	protected C_Expression minRangeArgument;
	protected C_Expression maxRangeArgument;

	public C_Between() {

	}

	public C_Between(C_Expression leftArgument, C_Expression minRangeArgument,
			C_Expression maxRangeArgument

	) {
		this.leftArgument = leftArgument;
		this.minRangeArgument = minRangeArgument;
		this.maxRangeArgument = maxRangeArgument;

		if (leftArgument != null && minRangeArgument != null
				&& maxRangeArgument != null) {
			this.readableExpression = "Between("
					+ leftArgument.getReadableExpression() + ","
					+ minRangeArgument.getReadableExpression() + ","
					+ maxRangeArgument.getReadableExpression() + ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.BETWEEN;
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

	public C_Expression getMinRangeArgument() {
		return minRangeArgument;
	}

	public void setMinRangeArgument(C_Expression minRangeArgument) {
		this.minRangeArgument = minRangeArgument;
	}

	public C_Expression getMaxRangeArgument() {
		return maxRangeArgument;
	}

	public void setMaxRangeArgument(C_Expression maxRangeArgument) {
		this.maxRangeArgument = maxRangeArgument;
	}

	@Override
	public String toString() {
		return "C_Between [id=" + id + ", leftArgument=" + leftArgument
				+ ", minRangeArgument=" + minRangeArgument
				+ ", maxRangeArgument=" + maxRangeArgument + "]";
	}

	

}
