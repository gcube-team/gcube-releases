package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.aggregation;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class C_Max extends C_AggregationExpression {

	private static final long serialVersionUID = 7765751618067012879L;
	protected String id = "Max";

	public C_Max() {

	}

	
	public C_Max(C_Expression argument) {
		this.argument = argument;
		if (argument != null) {
			this.readableExpression = "Max(" + argument.getReadableExpression()
					+ ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.MAX;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Max [id=" + id + ", argument=" + argument + "]";
	}

}
