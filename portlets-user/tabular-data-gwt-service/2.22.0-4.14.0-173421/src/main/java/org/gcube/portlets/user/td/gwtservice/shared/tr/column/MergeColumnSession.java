package org.gcube.portlets.user.td.gwtservice.shared.tr.column;

import java.io.Serializable;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class MergeColumnSession implements Serializable {

	private static final long serialVersionUID = -1896235499708614266L;

	private ColumnData columnDataSource1;
	private ColumnData columnDataSource2;
	private String label;
	private Expression expression;
	private ColumnTypeCode mergeColumnType;
	private ColumnDataType mergeColumnDataType;
	private boolean deleteColumn;

	public MergeColumnSession() {

	}

	
	public MergeColumnSession(ColumnData columnDataSource1,
			ColumnData columnDataSource2,
			String label, 
			ColumnTypeCode mergeColumnType,
			ColumnDataType mergeColumnDataType,
			Expression expression, boolean deleteColumn) {
		this.columnDataSource1 = columnDataSource1;
		this.columnDataSource2 = columnDataSource2;
		this.label = label;
		this.expression = expression;
		this.mergeColumnType=mergeColumnType;
		this.mergeColumnDataType=mergeColumnDataType;
		this.deleteColumn=deleteColumn;
	}


	public ColumnData getColumnDataSource1() {
		return columnDataSource1;
	}

	public void setColumnDataSource1(ColumnData columnDataSource1) {
		this.columnDataSource1 = columnDataSource1;
	}

	public ColumnData getColumnDataSource2() {
		return columnDataSource2;
	}

	public void setColumnDataSource2(ColumnData columnDataSource2) {
		this.columnDataSource2 = columnDataSource2;
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ColumnTypeCode getMergeColumnType() {
		return mergeColumnType;
	}

	public void setMergeColumnType(ColumnTypeCode mergeColumnType) {
		this.mergeColumnType = mergeColumnType;
	}

	public ColumnDataType getMergeColumnDataType() {
		return mergeColumnDataType;
	}

	public void setMergeColumnDataType(ColumnDataType mergeColumnDataType) {
		this.mergeColumnDataType = mergeColumnDataType;
	}
	
	public boolean isDeleteColumn() {
		return deleteColumn;
	}

	public void setDeleteColumn(boolean deleteColumn) {
		this.deleteColumn = deleteColumn;
	}

	@Override
	public String toString() {
		return "MergeColumnSession [columnDataSource1=" + columnDataSource1
				+ ", columnDataSource2=" + columnDataSource2 + ", label="
				+ label + ", expression=" + expression + ", mergeColumnType="
				+ mergeColumnType + ", mergeColumnDataType="
				+ mergeColumnDataType + ", deleteColumn=" + deleteColumn + "]";
	}

	
}
