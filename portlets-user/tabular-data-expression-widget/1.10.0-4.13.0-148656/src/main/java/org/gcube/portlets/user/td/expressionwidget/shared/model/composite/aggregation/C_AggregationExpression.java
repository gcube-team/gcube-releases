package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.aggregation;

import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */

public class C_AggregationExpression extends C_Expression {

	private static final long serialVersionUID = 119851273439171169L;

	private String id = "AggregationExpression";
	protected C_Expression argument;

	public C_AggregationExpression() {

	}

	public C_AggregationExpression(C_Expression argument) {
		this.argument = argument;
		if (argument != null) {
			this.readableExpression = "AggregationExpression("
					+ argument.getReadableExpression() + ")";
		}
	}

	@Override
	public String getId() {
		return id;
	}

	public String getReturnedDataType() {
		return "DataType";
	}

	public C_Expression getArgument() {
		return argument;
	}

	public void setArgument(C_Expression argument) {
		this.argument = argument;
	}

	@Override
	public String toString() {
		return "C_AggregationExpression [id=" + id + ", argument=" + argument
				+ "]";
	}

}
