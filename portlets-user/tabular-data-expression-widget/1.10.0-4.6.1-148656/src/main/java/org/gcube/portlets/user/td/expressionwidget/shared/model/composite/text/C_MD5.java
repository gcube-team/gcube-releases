package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class C_MD5 extends C_Expression {
	private static final long serialVersionUID = -5149428840566398839L;
	protected String id = "MD5";
	protected C_Expression argument;

	public C_MD5() {

	}

	public C_MD5(C_Expression argument) {
		this.argument = argument;
		if (argument != null) {
			this.readableExpression = "MD5(" + argument.getReadableExpression()
					+ ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.MD5;
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
		return "MD5 [id=" + id + ", argument=" + argument + "]";
	}

}
