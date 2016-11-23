/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.shared;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 30, 2014
 *
 */
public class SplitAndMergeColumnSession implements Serializable{
	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7183786043709126868L;
	
	protected ColumnData firstColumnData;
	protected ColumnData secondColumnData; //USED BY MERGE OPERATION
	protected String value;
	protected TdOperatorComboOperator operator;
	protected OperationID operatorID;
	
	protected String labelColumn1 = null;
	protected String labelColumn2 = null;

	private ColumnTypeCode columnType1;

	private ColumnTypeCode columnType2;

	private ColumnDataType dataType1;

	private ColumnDataType dataType2;
	
	private boolean deleteSourceColumn;
	
	/**	
	 * 
	 */
	public SplitAndMergeColumnSession() {
	}
	
	/**
	 * 
	 */
	public SplitAndMergeColumnSession(OperationID operatorID) {
		this.operatorID = operatorID;
	}
	
	/**
	 * @param currentValue
	 */
	public void setOperator(TdOperatorComboOperator operator) {
		this.operator = operator;
	}
	
	public TdOperatorComboOperator getOperator() {
		return operator;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public OperationID getOperatorID() {
		return operatorID;
	}

	public void setOperatorID(OperationID operatorID) {
		this.operatorID = operatorID;
	}

	public ColumnData getFirstColumnData() {
		return firstColumnData;
	}

	public void setFirstColumnData(ColumnData firstColumnData) {
		this.firstColumnData = firstColumnData;
	}

	public ColumnData getSecondColumnData() {
		return secondColumnData;
	}

	public void setSecondColumnData(ColumnData secondColumnData) {
		this.secondColumnData = secondColumnData;
	}

	public String getLabelColumn1() {
		return labelColumn1;
	}

	public String getLabelColumn2() {
		return labelColumn2;
	}

	public void setLabelColumn1(String labelColumn1) {
		this.labelColumn1 = labelColumn1;
	}

	public void setLabelColumn2(String labelColumn2) {
		this.labelColumn2 = labelColumn2;
	}


	/**
	 * @param code
	 */
	public void setColumnType1(ColumnTypeCode code) {
		this.columnType1 = code;
		
	}

	/**
	 * @param code
	 */
	public void setColumnType2(ColumnTypeCode code) {
		this.columnType2 = code;
		
	}

	/**
	 * @param type
	 */
	public void setDataType1(ColumnDataType type) {
		this.dataType1 = type;
		
	}

	/**
	 * @param type
	 */
	public void setDataType2(ColumnDataType type) {
		this.dataType2 = type;
		
	}

	public ColumnTypeCode getColumnType1() {
		return columnType1;
	}

	public ColumnTypeCode getColumnType2() {
		return columnType2;
	}

	public ColumnDataType getDataType1() {
		return dataType1;
	}

	public ColumnDataType getDataType2() {
		return dataType2;
	}

	public boolean isDeleteSourceColumn() {
		return deleteSourceColumn;
	}

	public void setDeleteSourceColumn(boolean deleteSourceColumn) {
		this.deleteSourceColumn = deleteSourceColumn;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SplitAndMergeColumnSession [firstColumnData=");
		builder.append(firstColumnData);
		builder.append(", secondColumnData=");
		builder.append(secondColumnData);
		builder.append(", value=");
		builder.append(value);
		builder.append(", operator=");
		builder.append(operator);
		builder.append(", operatorID=");
		builder.append(operatorID);
		builder.append(", labelColumn1=");
		builder.append(labelColumn1);
		builder.append(", labelColumn2=");
		builder.append(labelColumn2);
		builder.append(", columnType1=");
		builder.append(columnType1);
		builder.append(", columnType2=");
		builder.append(columnType2);
		builder.append(", dataType1=");
		builder.append(dataType1);
		builder.append(", dataType2=");
		builder.append(dataType2);
		builder.append(", deleteSourceColumn=");
		builder.append(deleteSourceColumn);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
