package org.gcube.portlets.user.td.expressionwidget.shared.model.logical;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public class C_IsNull extends C_Expression {
	private static final long serialVersionUID = -7845762664802353175L;

	protected String id = "IsNull";
	protected C_Expression argument;

	public C_IsNull(){
		
	}
	
	public C_IsNull(C_Expression argument) {
		this.argument=argument;
		if (argument != null) {
			this.readableExpression = "IsNull("
					+ argument.getReadableExpression() + ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.IS_NULL;
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
		return "IsNull [id=" + id + ", argument=" + argument + "]";
	}
	
	
	
}
