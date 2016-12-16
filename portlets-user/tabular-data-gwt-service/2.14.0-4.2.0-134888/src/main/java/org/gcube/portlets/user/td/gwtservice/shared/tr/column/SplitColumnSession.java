package org.gcube.portlets.user.td.gwtservice.shared.tr.column;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SplitColumnSession implements Serializable {

	private static final long serialVersionUID = -1896235499708614266L;

	private ColumnData columnData;
	private ArrayList<Expression> expressions;
	private String label1;
	private ColumnTypeCode firstSplitColumnType;
	private ColumnDataType firstSplitColumnDataType;
	private String label2;
	private ColumnTypeCode secondSplitColumnType;
	private ColumnDataType secondSplitColumnDataType;
	private boolean deleteColumn;

	public SplitColumnSession() {

	}

	/**
	 * 
	 * @param columnData
	 * @param expressions
	 * @param label1
	 * @param firstSplitColumnType
	 * @param firstSplitColumnDataType
	 * @param label2
	 * @param secondSplitColumnType
	 * @param secondSplitColumnDataType
	 */
	public SplitColumnSession(ColumnData columnData,
			ArrayList<Expression> expressions, String label1,
			ColumnTypeCode firstSplitColumnType,
			ColumnDataType firstSplitColumnDataType, String label2,
			ColumnTypeCode secondSplitColumnType,
			ColumnDataType secondSplitColumnDataType, boolean deleteColumn) {
		this.columnData = columnData;
		this.expressions = expressions;
		this.label1 = label1;
		this.firstSplitColumnType = firstSplitColumnType;
		this.firstSplitColumnDataType = firstSplitColumnDataType;
		this.label2 = label2;
		this.secondSplitColumnType = secondSplitColumnType;
		this.secondSplitColumnDataType = secondSplitColumnDataType;
		this.deleteColumn = deleteColumn;

	}

	public ColumnData getColumnData() {
		return columnData;
	}

	public void setColumnData(ColumnData columnData) {
		this.columnData = columnData;
	}

	public String getLabel1() {
		return label1;
	}

	public void setLabel1(String label1) {
		this.label1 = label1;
	}

	public String getLabel2() {
		return label2;
	}

	public void setLabel2(String label2) {
		this.label2 = label2;
	}

	public ArrayList<Expression> getExpressions() {
		return expressions;
	}

	public void setExpressions(ArrayList<Expression> expressions) {
		this.expressions = expressions;
	}

	public ColumnTypeCode getFirstSplitColumnType() {
		return firstSplitColumnType;
	}

	public void setFirstSplitColumnType(ColumnTypeCode firstSplitColumnType) {
		this.firstSplitColumnType = firstSplitColumnType;
	}

	public ColumnTypeCode getSecondSplitColumnType() {
		return secondSplitColumnType;
	}

	public void setSecondSplitColumnType(ColumnTypeCode secondSplitColumnType) {
		this.secondSplitColumnType = secondSplitColumnType;
	}

	public ColumnDataType getFirstSplitColumnDataType() {
		return firstSplitColumnDataType;
	}

	public void setFirstSplitColumnDataType(
			ColumnDataType firstSplitColumnDataType) {
		this.firstSplitColumnDataType = firstSplitColumnDataType;
	}

	public ColumnDataType getSecondSplitColumnDataType() {
		return secondSplitColumnDataType;
	}

	public void setSecondSplitColumnDataType(
			ColumnDataType secondSplitColumnDataType) {
		this.secondSplitColumnDataType = secondSplitColumnDataType;
	}

	public boolean isDeleteColumn() {
		return deleteColumn;
	}

	public void setDeleteColumn(boolean deleteColumn) {
		this.deleteColumn = deleteColumn;
	}

	@Override
	public String toString() {
		return "SplitColumnSession [columnData=" + columnData
				+ ", expressions=" + expressions + ", label1=" + label1
				+ ", firstSplitColumnType=" + firstSplitColumnType
				+ ", firstSplitColumnDataType=" + firstSplitColumnDataType
				+ ", label2=" + label2 + ", secondSplitColumnType="
				+ secondSplitColumnType + ", secondSplitColumnDataType="
				+ secondSplitColumnDataType + ", deleteColumn=" + deleteColumn
				+ "]";
	}

}
