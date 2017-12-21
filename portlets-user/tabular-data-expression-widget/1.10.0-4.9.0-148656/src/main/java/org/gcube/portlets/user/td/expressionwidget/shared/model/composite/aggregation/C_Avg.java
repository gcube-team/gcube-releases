package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.aggregation;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class C_Avg extends C_AggregationExpression {

	private static final long serialVersionUID = 7728851019516221450L;
	protected String id = "Avg";

	public C_Avg() {

	}

	
	public C_Avg(C_Expression argument) {
		this.argument = argument;
		if (argument != null) {
			this.readableExpression = "AVG(" + argument.getReadableExpression()
					+ ")";
		}

	}

	public C_OperatorType getOperator() {
		return C_OperatorType.AVG;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Avg [id=" + id + ", argument=" + argument + "]";
	}

}
