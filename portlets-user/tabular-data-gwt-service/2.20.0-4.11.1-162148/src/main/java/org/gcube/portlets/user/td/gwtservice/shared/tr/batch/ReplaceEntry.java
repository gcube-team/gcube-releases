package org.gcube.portlets.user.td.gwtservice.shared.tr.batch;

import java.io.Serializable;

import org.gcube.portlets.user.td.gwtservice.shared.tr.DimensionRow;

public class ReplaceEntry implements Serializable {

	private static final long serialVersionUID = 1630393311734647924L;

	protected String value;
	protected String rowId;// For view column, contains the value of the
							// associated dimension column
	protected Integer number;
	protected String replacementValue;
	protected DimensionRow replacementDimensionRow;

	public ReplaceEntry() {

	}

	public ReplaceEntry(String value, Integer number, String replacementValue, DimensionRow replacementDimensionRow) {
		this.value = value;
		this.rowId = null;
		this.number = number;
		this.replacementValue = replacementValue;
		this.replacementDimensionRow = replacementDimensionRow;
	}

	public ReplaceEntry(String value, String rowId, Integer number, String replacementValue,
			DimensionRow replacementDimensionRow) {
		this.value = value;
		this.rowId = rowId;
		this.number = number;
		this.replacementValue = replacementValue;
		this.replacementDimensionRow = replacementDimensionRow;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getReplacementValue() {
		return replacementValue;
	}

	public void setReplacementValue(String replacementValue) {
		this.replacementValue = replacementValue;
	}

	public DimensionRow getReplacementDimensionRow() {
		return replacementDimensionRow;
	}

	public void setReplacementDimensionRow(DimensionRow replacementDimensionRow) {
		this.replacementDimensionRow = replacementDimensionRow;
	}

	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	@Override
	public String toString() {
		return "ReplaceEntry [value=" + value + ", rowId=" + rowId + ", number=" + number + ", replacementValue="
				+ replacementValue + ", replacementDimensionRow=" + replacementDimensionRow + "]";
	}

}
