package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.arithmetic;

import java.util.ArrayList;

import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class C_ArithmeticExpression extends C_Expression {

	private static final long serialVersionUID = 897961910530874376L;

	private String id = "ArithmeticExpression";

	public static final ArrayList<ColumnDataType> acceptedDataTypes = new ArrayList<ColumnDataType>();

	protected C_Expression leftArgument;
	protected C_Expression rightArgument;

	public C_ArithmeticExpression() {

	}

	public C_ArithmeticExpression(C_Expression leftArgument,
			C_Expression rightArgument) {
		this.leftArgument = leftArgument;
		this.rightArgument = rightArgument;
		if (leftArgument != null && rightArgument != null) {
			this.readableExpression = "ArithmeticExpression("
					+ leftArgument.getReadableExpression() + ","
					+ rightArgument.getReadableExpression() + ")";
		}
	}

	static {
		acceptedDataTypes.add(ColumnDataType.Numeric);
		acceptedDataTypes.add(ColumnDataType.Integer);
		// acceptedDataTypes.add(ColumnDataType.Date); //Date+Date?
		// acceptedDataTypes.add(ColumnDataType.Geometry); //Point+Point
		// PostiGIS?
	}

	public String getReturnedDataType() {
		return "DataType";
	}

	public ArrayList<ColumnDataType> allowedLeftDataTypes() {
		return acceptedDataTypes;
	}

	public ArrayList<ColumnDataType> allowedRightDataTypes() {
		return acceptedDataTypes;
	}

	public static boolean isAccepted(ColumnDataType columnDataType) {
		for (ColumnDataType c : acceptedDataTypes) {
			if (c.compareTo(columnDataType) == 0) {
				return true;
			}
		}
		return false;
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
		return "ArithmeticExpression [id=" + id + ", leftArgument="
				+ leftArgument + ", rightArgument=" + rightArgument + "]";
	}

}
