package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.functions;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class C_Cast extends C_Expression {

	private static final long serialVersionUID = 5871179766613405166L;
	protected String id = "Cast";
	protected C_Expression leftArgument;
	protected ColumnDataType rightArgument;

	public C_Cast() {
		super();
	}

	public C_Cast(C_Expression leftArgument, ColumnDataType rightArgument) {
		super();
		this.leftArgument = leftArgument;
		this.rightArgument = rightArgument;
		if (leftArgument != null && rightArgument != null) {
			this.readableExpression = "Cast("
					+ leftArgument.getReadableExpression() + ","
					+ rightArgument.getLabel() + ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.CAST;
	}

	public ColumnDataType getReturnedDataType() {
		return rightArgument;
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

	public ColumnDataType getRightArgument() {
		return rightArgument;
	}

	public void setRightArgument(ColumnDataType rightArgument) {
		this.rightArgument = rightArgument;
	}

	@Override
	public String toString() {
		return "Cast [id=" + id + ", leftArgument=" + leftArgument
				+ ", rightArgument=" + rightArgument + "]";
	}

	public boolean isCastSupported(ColumnDataType sourceType,
			ColumnDataType targetType) {
		// from / to text
		if (targetType == ColumnDataType.Text
				|| sourceType == ColumnDataType.Text)
			return true;
		// same class
		if (sourceType == targetType)
			return true;
		// integer <--> numeric
		if ((sourceType == ColumnDataType.Integer && targetType == ColumnDataType.Numeric)
				|| (sourceType == ColumnDataType.Numeric && targetType == ColumnDataType.Integer))
			return true;
		return false;
	}

}
