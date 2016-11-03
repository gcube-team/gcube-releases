package org.gcube.portlets.user.td.expressionwidget.shared.model.composite;

import org.gcube.portlets.user.td.expressionwidget.shared.expression.C_MultivaluedExpression;
import org.gcube.portlets.user.td.expressionwidget.shared.model.leaf.C_Leaf;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

public class C_ExternalReferenceExpression extends C_Expression implements
		C_MultivaluedExpression {

	private static final long serialVersionUID = 1260894239836974010L;
	protected C_Leaf leftArgument;
	protected C_Expression rightArgument;

	protected String id = "ExternalReferenceExpression";

	public C_ExternalReferenceExpression() {

	}

	public C_ExternalReferenceExpression(C_Leaf leftArgument,
			C_Expression rightArgument) {
		this.leftArgument = leftArgument;
		this.rightArgument = rightArgument;
		if (leftArgument != null && rightArgument != null) {
			this.readableExpression = "ExternalReferenceExpression("
					+ leftArgument.getReadableExpression() + ","
					+ rightArgument.getReadableExpression() + ")";
		}
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getIdMulti() {
		return id;
	}

	public C_Leaf getLeftArgument() {
		return leftArgument;
	}

	public void setLeftArgument(C_Leaf leftArgument) {
		this.leftArgument = leftArgument;
	}

	public C_Expression getRightArgument() {
		return rightArgument;
	}

	public void setRightArgument(C_Expression rightArgument) {
		this.rightArgument = rightArgument;
	}
	

	@Override
	public String getReadableMultivaluedString() {
		return readableExpression;
	}

	
	@Override
	public String toString() {
		return "ExternalReferenceExpression [ id=" + id + ", leftArgument="
				+ leftArgument + ", rightArgument=" + rightArgument + "]";
	}

}
