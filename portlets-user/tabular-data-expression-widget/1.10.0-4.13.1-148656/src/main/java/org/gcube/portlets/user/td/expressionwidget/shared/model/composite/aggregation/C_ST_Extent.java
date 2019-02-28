package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.aggregation;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class C_ST_Extent extends C_AggregationExpression {

	private static final long serialVersionUID = -3889328695012258308L;
	protected String id = "ST_Extent";

	public C_ST_Extent() {
	}

	
	public C_ST_Extent(C_Expression argument) {
		this.argument = argument;
		if (argument != null) {
			this.readableExpression = "ST_Extent(" + argument.getReadableExpression() + ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.ST_EXTENT;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "ST_Extent [id=" + id + ", argument=" + argument + "]";
	}

}
