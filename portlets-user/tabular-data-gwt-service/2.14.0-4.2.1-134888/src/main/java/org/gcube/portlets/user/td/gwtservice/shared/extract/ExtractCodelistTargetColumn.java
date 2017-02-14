package org.gcube.portlets.user.td.gwtservice.shared.extract;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnMockUp;

/**
 * 
 * @author "Giancarlo Panichi"
 * 
 */
public class ExtractCodelistTargetColumn implements Serializable {

	private static final long serialVersionUID = -3223334571168535567L;

	protected boolean newColumn;
	protected ColumnData sourceColumn;
	protected ColumnData targetColumn;
	protected TRId codelist;
	protected ColumnMockUp defColumn;

	public ExtractCodelistTargetColumn() {

	}

	public ExtractCodelistTargetColumn(ColumnData sourceColumn,
			ColumnData targetColumn, TRId codelist) {
		this.newColumn = false;
		this.sourceColumn = sourceColumn;
		this.targetColumn = targetColumn;
		this.codelist = codelist;
	}

	public ExtractCodelistTargetColumn(ColumnData sourceColumn, ColumnMockUp defNewColumn) {
		this.newColumn = true;
		this.sourceColumn = sourceColumn;
		this.defColumn=defNewColumn;
	}

	public boolean isNewColumn() {
		return newColumn;
	}

	public void setNewColumn(boolean newColumn) {
		this.newColumn = newColumn;
	}

	public ColumnData getSourceColumn() {
		return sourceColumn;
	}

	public void setSourceColumn(ColumnData sourceColumn) {
		this.sourceColumn = sourceColumn;
	}

	public ColumnData getTargetColumn() {
		return targetColumn;
	}

	public void setTargetColumn(ColumnData targetColumn) {
		this.targetColumn = targetColumn;
	}

	public TRId getCodelist() {
		return codelist;
	}

	public void setCodelist(TRId codelist) {
		this.codelist = codelist;
	}

	public ColumnMockUp getDefColumn() {
		return defColumn;
	}

	public void setDefColumn(ColumnMockUp defColumn) {
		this.defColumn = defColumn;
	}

	@Override
	public String toString() {
		return "ExtractCodelistTargetColumn [newColumn=" + newColumn
				+ ", sourceColumn=" + sourceColumn + ", targetColumn="
				+ targetColumn + ", codelist=" + codelist + ", defColumn="
				+ defColumn + "]";
	}

	
	

}
