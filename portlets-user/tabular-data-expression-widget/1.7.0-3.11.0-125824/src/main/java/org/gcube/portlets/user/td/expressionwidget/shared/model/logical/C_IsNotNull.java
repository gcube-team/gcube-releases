package org.gcube.portlets.user.td.expressionwidget.shared.model.logical;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class C_IsNotNull extends C_Expression {

	private static final long serialVersionUID = 8930488371061116376L;
	protected C_Expression argument;
	protected String id = "IsNotNull";

	public C_IsNotNull(){
		
	}
	
	public C_IsNotNull(C_Expression argument) {
		this.argument = argument;
		if (argument != null) {
			this.readableExpression = "IsNotNull("
					+ argument.getReadableExpression() + ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.IS_NOT_NULL;
	}

	public String getReturnedDataType() {
		return "Boolean";
	}

	public C_Expression getArgument() {
		return argument;
	}

	public void setArgument(C_Expression argument) {
		this.argument = argument;
	}

	@Override
	public String getId() {
		return id;
	}

	

	@Override
	public String toString() {
		return "IsNotNull [id=" + id + ", argument=" + argument + "]";
	}

}