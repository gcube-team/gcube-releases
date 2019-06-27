package org.gcube.portlets.user.td.expressionwidget.shared.model.composite.text;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;
import org.gcube.portlets.user.td.expressionwidget.shared.model.leaf.TD_Value;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class C_TextReplaceMatchingRegex extends C_Expression {

	private static final long serialVersionUID = -5827586649543147576L;

	protected String id = "TextReplaceMatchingRegex";

	private C_Expression toCheckText;
	private TD_Value regexp;
	private TD_Value replacing;

	public C_TextReplaceMatchingRegex() {
		super();
	}

	public C_TextReplaceMatchingRegex(C_Expression toCheckText,
			TD_Value regexp, TD_Value replacing) {
		super();
		this.toCheckText = toCheckText;
		this.regexp = regexp;
		this.replacing = replacing;
		if (toCheckText != null && regexp != null && replacing != null) {
			this.readableExpression = "ReplaceByExpression("
					+ toCheckText.getReadableExpression() + ","
					+ regexp.getReadableExpression() + ","
					+ replacing.getReadableExpression() + ")";
		}

	}

	public C_OperatorType getOperator() {
		return C_OperatorType.TEXT_REPLACE_MATCHING_REGEX;
	}

	public String getReturnedDataType() {
		return "Text";
	}

	@Override
	public String getId() {
		return id;
	}

	public C_Expression getToCheckText() {
		return toCheckText;
	}

	public void setToCheckText(C_Expression toCheckText) {
		this.toCheckText = toCheckText;
	}

	public TD_Value getRegexp() {
		return regexp;
	}

	public void setRegexp(TD_Value regexp) {
		this.regexp = regexp;
	}

	public TD_Value getReplacing() {
		return replacing;
	}

	public void setReplacing(TD_Value replacing) {
		this.replacing = replacing;
	}

	@Override
	public String toString() {
		return "TextReplaceMatchingRegex [id=" + id + ", toCheckText="
				+ toCheckText + ", regexp=" + regexp + ", replacing="
				+ replacing + "]";
	}

}
