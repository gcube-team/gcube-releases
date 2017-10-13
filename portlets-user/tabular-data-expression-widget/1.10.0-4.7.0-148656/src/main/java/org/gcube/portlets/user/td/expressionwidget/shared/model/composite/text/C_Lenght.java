package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class C_Lenght extends C_Expression {
	private static final long serialVersionUID = -5149428840566398839L;
	protected String id = "Length";
	protected C_Expression argument;

	public C_Lenght() {

	}

	public C_Lenght(C_Expression argument) {
		this.argument = argument;
		if (argument != null) {
			this.readableExpression = "Lenght("
					+ argument.getReadableExpression() + ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.LENGTH;
	}

	public String getReturnedDataType() {
		return "Integer";
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
		return "Length [id=" + id + ", argument=" + argument + "]";
	}

}
