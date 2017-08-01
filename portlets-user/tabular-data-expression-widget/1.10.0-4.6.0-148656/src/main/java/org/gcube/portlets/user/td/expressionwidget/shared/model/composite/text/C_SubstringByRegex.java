package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class C_SubstringByRegex extends C_Expression {

	private static final long serialVersionUID = 5871179766613405166L;
	protected String id = "SubstringByRegex";

	private C_Expression sourceString;
	private C_Expression regex;

	public C_SubstringByRegex() {
		super();
	}

	public C_SubstringByRegex(C_Expression sourceString, C_Expression regex) {
		super();
		this.sourceString = sourceString;
		this.regex = regex;
		if (sourceString != null && regex != null) {
			this.readableExpression = "SubstringByRegex("
					+ sourceString.getReadableExpression() + ","
					+ regex.getReadableExpression() + ")";
		}

	}

	public C_OperatorType getOperator() {
		return C_OperatorType.SUBSTRING_BY_REGEX;
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

	public C_Expression getRegex() {
		return regex;
	}

	public void setRegex(C_Expression regex) {
		this.regex = regex;
	}

	@Override
	public String toString() {
		return "SubstringByRegex [id=" + id + ", sourceString=" + sourceString
				+ ", regex=" + regex + "]";
	}

}
