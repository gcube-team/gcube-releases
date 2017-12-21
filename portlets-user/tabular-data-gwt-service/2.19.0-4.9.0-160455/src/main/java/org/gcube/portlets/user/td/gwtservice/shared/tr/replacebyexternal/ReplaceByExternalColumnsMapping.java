package org.gcube.portlets.user.td.gwtservice.shared.tr.replacebyexternal;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ReplaceByExternalColumnsMapping implements Serializable {

	private static final long serialVersionUID = 2795844498148679703L;

	protected String columnLabel;
	protected ColumnData currentColumn;
	protected ColumnData externalColumn;

	public ReplaceByExternalColumnsMapping() {

	}
	
	
	public ReplaceByExternalColumnsMapping(String columnLabel,
			ColumnData currentColumn, ColumnData externalColumn) {
		super();
		this.columnLabel = columnLabel;
		this.currentColumn = currentColumn;
		this.externalColumn = externalColumn;
	}

	public String getColumnLabel() {
		return columnLabel;
	}

	public void setColumnLabel(String columnLabel) {
		this.columnLabel = columnLabel;
	}

	public ColumnData getCurrentColumn() {
		return currentColumn;
	}

	public void setCurrentColumn(ColumnData currentColumn) {
		this.currentColumn = currentColumn;
	}

	public ColumnData getExternalColumn() {
		return externalColumn;
	}

	public void setExternalColumn(ColumnData externalColumn) {
		this.externalColumn = externalColumn;
	}

	@Override
	public String toString() {
		return "ReplaceByExternalColumnsMapping [columnLabel=" + columnLabel
				+ ", currentColumn=" + currentColumn + ", externalColumn="
				+ externalColumn + "]";
	}

	
}
