package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.aggregation;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class C_Sum extends C_AggregationExpression {

	private static final long serialVersionUID = 1365651146183648884L;
	protected String id = "Sum";

	public C_Sum() {

	}

	
	public C_Sum(C_Expression argument) {
		this.argument = argument;
		if (argument != null) {
			this.readableExpression = "Sum(" + argument.getReadableExpression()
					+ ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.SUM;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Sum [id=" + id + ", argument=" + argument + "]";
	}

}
