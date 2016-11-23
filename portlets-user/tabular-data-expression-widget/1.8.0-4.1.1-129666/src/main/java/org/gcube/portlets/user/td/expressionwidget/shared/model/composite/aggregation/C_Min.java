package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.aggregation;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class C_Min extends C_AggregationExpression {

	private static final long serialVersionUID = -7717661319471699474L;
	protected String id = "Min";

	public C_Min() {

	}

	/**
	 * 
	 * @param argument
	 */
	public C_Min(C_Expression argument) {
		this.argument = argument;
		if (argument != null) {
			this.readableExpression = "Min(" + argument.getReadableExpression()
					+ ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.MIN;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Min [id=" + id + ", argument=" + argument + "]";
	}

}
