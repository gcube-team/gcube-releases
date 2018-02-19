package org.gcube.portlets.user.td.gwtservice.shared.tr.column;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ReplaceColumnByExpressionSession implements Serializable {

	private static final long serialVersionUID = -3940140817918362233L;
	private ColumnData column;
	private boolean allRows;
	private C_Expression cConditionExpression;
	private C_Expression cReplaceExpression;
	private String replaceValue;
	private boolean replaceByValue;

	public ReplaceColumnByExpressionSession() {

	}

	public ReplaceColumnByExpressionSession(ColumnData column, boolean allRows,
			C_Expression cConditionExpression, String replaceValue) {
		this.column = column;
		this.allRows = allRows;
		this.cConditionExpression = cConditionExpression;
		this.replaceValue = replaceValue;
		this.replaceByValue = true;
	}

	public ReplaceColumnByExpressionSession(ColumnData column, boolean allRows,
			C_Expression cConditionExpression, C_Expression cReplaceExpression) {
		this.column = column;
		this.allRows = allRows;
		this.cConditionExpression = cConditionExpression;
		this.cReplaceExpression = cReplaceExpression;
		this.replaceByValue = false;
	}

	public ColumnData getColumn() {
		return column;
	}

	public void setColumn(ColumnData column) {
		this.column = column;
	}

	public boolean isAllRows() {
		return allRows;
	}

	public void setAllRows(boolean allRows) {
		this.allRows = allRows;
	}

	public C_Expression getcConditionExpression() {
		return cConditionExpression;
	}

	public void setcConditionExpression(C_Expression cConditionExpression) {
		this.cConditionExpression = cConditionExpression;
	}

	public C_Expression getcReplaceExpression() {
		return cReplaceExpression;
	}

	public void setcReplaceExpression(C_Expression cReplaceExpression) {
		this.cReplaceExpression = cReplaceExpression;
	}

	public String getReplaceValue() {
		return replaceValue;
	}

	public void setReplaceValue(String replaceValue) {
		this.replaceValue = replaceValue;
	}

	public boolean isReplaceByValue() {
		return replaceByValue;
	}

	public void setReplaceByValue(boolean replaceByValue) {
		this.replaceByValue = replaceByValue;
	}

	@Override
	public String toString() {
		return "ReplaceColumnByExpressionSession [column=" + column
				+ ", cConditionExpression=" + cConditionExpression
				+ ", cReplaceExpression=" + cReplaceExpression
				+ ", replaceValue=" + replaceValue + ", replaceByValue="
				+ replaceByValue + "]";
	}

}
