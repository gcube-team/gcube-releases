package org.gcube.portlets.user.td.gwtservice.shared.tr.union;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class UnionColumnsMapping  implements Serializable {
	
	private static final long serialVersionUID = -8971216501310276805L;
	
	protected String columnLabel;
	protected ColumnData sourceColumn;
	protected ColumnData targetColumn;
	
	public UnionColumnsMapping(){
		
	}
	
	public UnionColumnsMapping(String columnLabel,ColumnData sourceColumn,ColumnData targetColumn){
		this.columnLabel=columnLabel;
		this.sourceColumn=sourceColumn;
		this.targetColumn=targetColumn;
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
	
	public String getColumnLabel() {
		return columnLabel;
	}

	public void setColumnLabel(String columnLabel) {
		this.columnLabel = columnLabel;
	}

	@Override
	public String toString() {
		return "UnionColumnsMapping [columnLabel=" + columnLabel
				+ ", sourceColumn=" + sourceColumn + ", targetColumn="
				+ targetColumn + "]";
	}

	
	
	
	
}
