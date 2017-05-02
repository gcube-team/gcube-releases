package org.gcube.portlets.user.td.expressionwidget.shared.model.logical;

import org.gcube.portlets.user.td.expressionwidget.shared.expression.C_MultivaluedExpression;
import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class C_ValueIsIn extends C_Expression {
	private static final long serialVersionUID = 739569588958197726L;
	protected String id = "ValueIsIn";
	protected C_Expression leftArgument;
	protected C_MultivaluedExpression rightArgument;

	public C_ValueIsIn(){
		
	}
	
	
	public C_ValueIsIn(C_Expression leftArgument, C_MultivaluedExpression rightArgument) {
		this.leftArgument = leftArgument;
		this.rightArgument = rightArgument;
		if (leftArgument != null && rightArgument != null) {
			this.readableExpression = "ValueIsIn("
					+ leftArgument.getReadableExpression() + ","
					+ rightArgument.getReadableMultivaluedString() + ")";
		}
	}

	public C_OperatorType getOperator() {
		return C_OperatorType.IN;
	}

	public String getReturnedDataType() {
		return "Boolean";
	}

	@Override
	public String getId() {
		return id;
	}

	
	public C_Expression getLeftArgument() {
		return leftArgument;
	}

	public void setLeftArgument(C_Expression leftArgument) {
		this.leftArgument = leftArgument;
	}

	public C_MultivaluedExpression getRightArgument() {
		return rightArgument;
	}

	public void setRightArgument(C_MultivaluedExpression rightArgument) {
		this.rightArgument = rightArgument;
	}

	@Override
	public String toString() {
		return "ValueIsIn [id=" + id + ", leftArgument=" + leftArgument
				+ ", rightArgument=" + rightArgument + "]";
	}
	
	
	
	
}
