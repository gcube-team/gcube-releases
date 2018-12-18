package org.gcube.portlets.user.td.widgetcommonevent.shared;

import java.io.Serializable;

/**
 * Cell basic information
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class CellData implements Serializable {

	private static final long serialVersionUID = -8622096647178437477L;
	protected String value; // row value
	protected String columnName; // column name
	protected String columnId; // column local id
	protected String columnLabel; // column label
	protected String rowId; // row id value for service
	protected int viewRowIndex; // Row show on grid
	protected int viewColumnIndex; // Column show on grid

	public CellData() {

	}

	public CellData(String value, String columnName, String columnId,
			String columnLabel, String rowId, int viewRowIndex,
			int viewColumnIndex) {
		this.value = value;
		this.columnName = columnName;
		this.columnId = columnId;
		this.columnLabel = columnLabel;
		this.rowId = rowId;
		this.viewRowIndex = viewRowIndex;
		this.viewColumnIndex = viewColumnIndex;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public String getColumnLabel() {
		return columnLabel;
	}

	public void setColumnLabel(String columnLabel) {
		this.columnLabel = columnLabel;
	}

	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	public int getViewRowIndex() {
		return viewRowIndex;
	}

	public void setViewRowIndex(int viewRowIndex) {
		this.viewRowIndex = viewRowIndex;
	}

	public int getViewColumnIndex() {
		return viewColumnIndex;
	}

	public void setViewColumnIndex(int viewColumnIndex) {
		this.viewColumnIndex = viewColumnIndex;
	}

	@Override
	public String toString() {
		return "CellData [value=" + value + ", columnName=" + columnName
				+ ", columnId=" + columnId + ", columnLabel=" + columnLabel
				+ ", rowId=" + rowId + ", viewRowIndex=" + viewRowIndex
				+ ", viewColumnIndex=" + viewColumnIndex + "]";
	}

}
