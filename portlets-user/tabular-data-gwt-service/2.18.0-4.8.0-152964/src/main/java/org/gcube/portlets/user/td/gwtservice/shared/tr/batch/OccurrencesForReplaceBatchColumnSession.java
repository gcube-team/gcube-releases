package org.gcube.portlets.user.td.gwtservice.shared.tr.batch;

import java.io.Serializable;

import org.gcube.portlets.user.td.gwtservice.shared.tr.ConditionCode;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class OccurrencesForReplaceBatchColumnSession implements Serializable {

	private static final long serialVersionUID = -838394991333185029L;

	protected ColumnData columnData;
	protected ShowOccurrencesType showType;
	protected boolean hasValidationColumns;

	protected ConditionCode conditionCode;
	protected String validationColumnColumnId;

	public OccurrencesForReplaceBatchColumnSession() {
	}

	public OccurrencesForReplaceBatchColumnSession(ColumnData columnData,
			ShowOccurrencesType showType, boolean hasValidationColumns,
			ConditionCode conditionCode, String validationColumnColumnId) {
		this.columnData = columnData;
		this.showType = showType;
		this.hasValidationColumns = hasValidationColumns;
		this.conditionCode=conditionCode;
		this.validationColumnColumnId=validationColumnColumnId;
	}

	public ColumnData getColumnData() {
		return columnData;
	}

	public void setColumnData(ColumnData columnData) {
		this.columnData = columnData;
	}

	public ShowOccurrencesType getShowType() {
		return showType;
	}

	public void setShowType(ShowOccurrencesType showType) {
		this.showType = showType;
	}

	public boolean isHasValidationColumns() {
		return hasValidationColumns;
	}

	public void setHasValidationColumns(boolean hasValidationColumns) {
		this.hasValidationColumns = hasValidationColumns;
	}
	
	public ConditionCode getConditionCode() {
		return conditionCode;
	}

	public void setConditionCode(ConditionCode conditionCode) {
		this.conditionCode = conditionCode;
	}

	public String getValidationColumnColumnId() {
		return validationColumnColumnId;
	}

	public void setValidationColumnColumnId(String validationColumnColumnId) {
		this.validationColumnColumnId = validationColumnColumnId;
	}

	@Override
	public String toString() {
		return "OccurrencesForReplaceBatchColumnSession [columnData="
				+ columnData + ", showType=" + showType
				+ ", hasValidationColumns=" + hasValidationColumns
				+ ", conditionCode=" + conditionCode
				+ ", validationColumnColumnId=" + validationColumnColumnId
				+ "]";
	}

	
}
