package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class C_Upper extends C_Expression {
	private static final long serialVersionUID = -5149428840566398839L;
	protected String id = "Upper";
	protected C_Expression argument;

	public C_Upper() {

	}

	public C_Upper(C_Expression argument) {
		this.argument = argument;
		if (argument != null) {
			this.readableExpression = "Upper("
					+ argument.getReadableExpression() + ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.UPPER;
	}

	public String getReturnedDataType() {
		return "Text";
	}

	@Override
	public String getId() {
		return id;
	}

	public C_Expression getArgument() {
		return argument;
	}

	public void setArgument(C_Expression argument) {
		this.argument = argument;
	}

	@Override
	public String toString() {
		return "Upper [id=" + id + ", argument=" + argument + "]";
	}

}
