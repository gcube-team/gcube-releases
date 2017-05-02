package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class C_SubstringByIndex extends C_Expression {

	private static final long serialVersionUID = 5871179766613405166L;
	protected String id = "SubstringByIndex";

	private C_Expression sourceString;
	private C_Expression fromIndex;
	private C_Expression toIndex;

	public C_SubstringByIndex() {
		super();
	}

	public C_SubstringByIndex(C_Expression sourceString,
			C_Expression fromIndex, C_Expression toIndex) {
		super();
		this.sourceString = sourceString;
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;
		if (sourceString != null && fromIndex != null && toIndex != null) {
			this.readableExpression = "SubstringByIndex("
					+ sourceString.getReadableExpression() + ","
					+ fromIndex.getReadableExpression() + ","
					+ toIndex.getReadableExpression() + ")";
		}

	}

	public C_OperatorType getOperator() {
		return C_OperatorType.SUBSTRING_BY_INDEX;
	}

	public String getReturnedDataType() {
		return "Text";
	}

	@Override
	public String getId() {
		return id;
	}

	public C_Expression getSourceString() {
		return sourceString;
	}

	public void setSourceString(C_Expression sourceString) {
		this.sourceString = sourceString;
	}

	public C_Expression getFromIndex() {
		return fromIndex;
	}

	public void setFromIndex(C_Expression fromIndex) {
		this.fromIndex = fromIndex;
	}

	public C_Expression getToIndex() {
		return toIndex;
	}

	public void setToIndex(C_Expression toIndex) {
		this.toIndex = toIndex;
	}

	@Override
	public String toString() {
		return "SubstringByIndex [id=" + id + ", sourceString=" + sourceString
				+ ", fromIndex=" + fromIndex + ", toIndex=" + toIndex + "]";
	}

}
