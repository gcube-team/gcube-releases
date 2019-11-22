package org.gcube.portlets.user.td.gwtservice.shared.tr.column.type;

import java.io.Serializable;

import org.gcube.portlets.user.td.gwtservice.shared.tr.column.mapping.ColumnMappingList;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.PeriodDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ValueDataFormat;

public class ChangeColumnTypeSession implements Serializable {

	private static final long serialVersionUID = 7154832921853261421L;

	private ColumnData columnData;
	private ColumnTypeCode columnTypeCode;
	private ColumnDataType columnDataType;
	private ColumnTypeCode columnTypeCodeTarget;
	private ColumnDataType columnDataTypeTarget;
	private String locale;

	private PeriodDataType timeDimensionType;
	private ValueDataFormat valueDataFormat;
	private ColumnData codelistColumnReference;

	private ColumnMappingList columnMappingList;

	public ChangeColumnTypeSession() {

	}

	/**
	 * For change to Dimension
	 * 
	 * @param columnData
	 *            Column data
	 * @param columnTypeCode
	 *            Column type code
	 * @param columnDataType
	 *            Column data type
	 * @param columnTypeCodeTarget
	 *            Column type code target
	 * @param columnDataTypeTarget
	 *            Column data type target
	 * @param codelistColumnReference
	 *            Codelist column reference
	 */
	public ChangeColumnTypeSession(ColumnData columnData, ColumnTypeCode columnTypeCode, ColumnDataType columnDataType,
			ColumnTypeCode columnTypeCodeTarget, ColumnDataType columnDataTypeTarget,
			ColumnData codelistColumnReference) {
		this.columnData = columnData;
		this.columnTypeCode = columnTypeCode;
		this.columnDataType = columnDataType;
		this.columnTypeCodeTarget = columnTypeCodeTarget;
		this.columnDataTypeTarget = columnDataTypeTarget;
		this.codelistColumnReference = codelistColumnReference;

	}

	/**
	 * For change to Dimension with mapping
	 * 
	 * @param columnData
	 *            Column data
	 * @param columnTypeCode
	 *            Column tpye code
	 * @param columnDataType
	 *            Column data type
	 * @param columnTypeCodeTarget
	 *            Column type code target
	 * @param columnDataTypeTarget
	 *            Column data type target
	 * @param codelistColumnReference
	 *            Codelist column reference
	 * @param columnMappingList
	 *            Column mapping list
	 */
	public ChangeColumnTypeSession(ColumnData columnData, ColumnTypeCode columnTypeCode, ColumnDataType columnDataType,
			ColumnTypeCode columnTypeCodeTarget, ColumnDataType columnDataTypeTarget,
			ColumnData codelistColumnReference, ColumnMappingList columnMappingList) {
		this.columnData = columnData;
		this.columnTypeCode = columnTypeCode;
		this.columnDataType = columnDataType;
		this.columnTypeCodeTarget = columnTypeCodeTarget;
		this.columnDataTypeTarget = columnDataTypeTarget;
		this.codelistColumnReference = codelistColumnReference;
		this.columnMappingList = columnMappingList;
	}

	public ColumnTypeCode getColumnTypeCode() {
		return columnTypeCode;
	}

	public void setColumnTypeCode(ColumnTypeCode columnTypeCode) {
		this.columnTypeCode = columnTypeCode;
	}

	public ColumnDataType getColumnDataType() {
		return columnDataType;
	}

	public void setColumnDataType(ColumnDataType columnDataType) {
		this.columnDataType = columnDataType;
	}

	public ColumnTypeCode getColumnTypeCodeTarget() {
		return columnTypeCodeTarget;
	}

	public void setColumnTypeCodeTarget(ColumnTypeCode columnTypeCodeTarget) {
		this.columnTypeCodeTarget = columnTypeCodeTarget;
	}

	public ColumnDataType getColumnDataTypeTarget() {
		return columnDataTypeTarget;
	}

	public void setColumnDataTypeTarget(ColumnDataType columnDataTypeTarget) {
		this.columnDataTypeTarget = columnDataTypeTarget;
	}

	public ColumnData getColumnData() {
		return columnData;
	}

	public void setColumnData(ColumnData columnData) {
		this.columnData = columnData;
	}

	public ColumnData getCodelistColumnReference() {
		return codelistColumnReference;
	}

	public void setCodelistColumnReference(ColumnData codelistColumnReference) {
		this.codelistColumnReference = codelistColumnReference;
	}

	public PeriodDataType getPeriodDataType() {
		return timeDimensionType;
	}

	public void setPeriodDataType(PeriodDataType timeDimensionType) {
		this.timeDimensionType = timeDimensionType;
	}

	public ValueDataFormat getValueDataFormat() {
		return valueDataFormat;
	}

	public void setValueDataFormat(ValueDataFormat valueDataFormat) {
		this.valueDataFormat = valueDataFormat;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public ColumnMappingList getColumnMappingList() {
		return columnMappingList;
	}

	public void setColumnMappingList(ColumnMappingList columnMappingList) {
		this.columnMappingList = columnMappingList;
	}

	@Override
	public String toString() {
		return "ChangeColumnTypeSession [columnData=" + columnData + ", columnTypeCode=" + columnTypeCode
				+ ", columnDataType=" + columnDataType + ", columnTypeCodeTarget=" + columnTypeCodeTarget
				+ ", columnDataTypeTarget=" + columnDataTypeTarget + ", locale=" + locale + ", timeDimensionType="
				+ timeDimensionType + ", timeDataFormat=" + valueDataFormat + ", codelistColumnReference="
				+ codelistColumnReference + ", columnMappingList=" + columnMappingList + "]";
	}

}
