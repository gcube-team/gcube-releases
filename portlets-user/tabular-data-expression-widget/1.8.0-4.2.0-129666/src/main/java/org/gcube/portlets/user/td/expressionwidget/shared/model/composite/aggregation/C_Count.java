package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.aggregation;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class C_Count extends C_AggregationExpression {

	private static final long serialVersionUID = 7728851019516221450L;
	protected String id = "Count";

	public C_Count() {

	}

	/**
	 * 
	 * @param argument
	 */
	public C_Count(C_Expression argument) {
		this.argument = argument;
		if (argument != null) {
			this.readableExpression = "Count("
					+ argument.getReadableExpression() + ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.COUNT;
	}

	@Override
	public String getReturnedDataType() {
		return "Integer";
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Count [id=" + id + ", argument=" + argument + "]";
	}

}
