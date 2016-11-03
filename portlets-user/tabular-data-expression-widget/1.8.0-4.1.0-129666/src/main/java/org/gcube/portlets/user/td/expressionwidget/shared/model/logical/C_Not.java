package org.gcube.portlets.user.td.expressionwidget.shared.model.logical;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class C_Not extends C_Expression {
	private static final long serialVersionUID = 9206533042761278382L;
	protected String id = "Not";
	protected C_Expression argument;

	public C_Not(){
		
	}
	
	public C_Not(C_Expression argument) {
		this.argument = argument;
		if (argument != null) {
			this.readableExpression = "Not("
					+ argument.getReadableExpression() + ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.NOT;
	}

	public String getReturnedDataType() {
		return "Boolean";
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
		return "Not [id=" + id + ", argument=" + argument + "]";
	}
	
	
	
}
