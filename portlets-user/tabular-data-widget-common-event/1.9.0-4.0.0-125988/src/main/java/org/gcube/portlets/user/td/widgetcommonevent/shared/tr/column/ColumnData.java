package org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ColumnData implements Serializable {

	private static final long serialVersionUID = 7614033455605898209L;

	private String id; // For insert in table only
	private String columnId; // Id of column on server
	private String name;
	private String typeName;
	private String typeCode;
	private String dataTypeName;
	private String label;
	private String locale;
	
	private TRId trId;
	
	private boolean viewColumn;
	private ColumnViewData columnViewData;
	
	private PeriodDataType periodDataType;
	
	// Relationship for Dimension and Timedimension columns
	private RelationshipData relationship;
	
	// validation columns that validate this column
	private ArrayList<String> validationColumnReferences;

	// true if this is a validation column
	private boolean validationColumn;
	// if this is a validation column then contains the columns validated
	private ArrayList<String> validatedColumns;
	
	
	public ColumnData(){
		
	}
	
	/**
	 * 
	 * @param id
	 * @param columnId
	 * @param name
	 * @param typeName
	 * @param typeCode
	 * @param dataTypeName
	 * @param label
	 * @param locale
	 * @param trId
	 * @param viewColumn
	 * @param columnViewData
	 * @param periodDataType
	 * @param relationship
	 * @param validationColumnReferences
	 * @param validationColumn
	 * @param validatedColumns
	 */
	public ColumnData(String id, String columnId, String name, String typeName,
			String typeCode, String dataTypeName, String label, String locale,
			TRId trId, boolean viewColumn, ColumnViewData columnViewData,
			PeriodDataType periodDataType, RelationshipData relationship,
			ArrayList<String> validationColumnReferences,
			boolean validationColumn, ArrayList<String> validatedColumns) {
		super();
		this.id = id;
		this.columnId = columnId;
		this.name = name;
		this.typeName = typeName;
		this.typeCode = typeCode;
		this.dataTypeName = dataTypeName;
		this.label = label;
		this.locale = locale;
		this.trId = trId;
		this.viewColumn = viewColumn;
		this.columnViewData = columnViewData;
		this.periodDataType = periodDataType;
		this.relationship = relationship;
		this.validationColumnReferences = validationColumnReferences;
		this.validationColumn = validationColumn;
		this.validatedColumns = validatedColumns;
	}


	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDataTypeName() {
		return dataTypeName;
	}

	public void setDataTypeName(String dataTypeName) {
		this.dataTypeName = dataTypeName;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ColumnViewData getColumnViewData() {
		return columnViewData;
	}

	public void setColumnViewData(ColumnViewData columnViewData) {
		this.columnViewData = columnViewData;
	}

	public boolean isViewColumn() {
		return viewColumn;
	}

	public void setViewColumn(boolean viewColumn) {
		this.viewColumn = viewColumn;
	}

	public boolean isValidationColumn() {
		return validationColumn;
	}

	public void setValidationColumn(boolean validationColumn) {
		this.validationColumn = validationColumn;
	}

	public ArrayList<String> getValidatedColumns() {
		return validatedColumns;
	}

	public void setValidatedColumns(ArrayList<String> validatedColumns) {
		this.validatedColumns = validatedColumns;
	}

	public ArrayList<String> getValidationColumnReferences() {
		return validationColumnReferences;
	}
	
	public void setValidationColumnReferences(
			ArrayList<String> validationColumnReferences) {
		this.validationColumnReferences = validationColumnReferences;
	}
	
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public RelationshipData getRelationship() {
		return relationship;
	}

	public void setRelationship(RelationshipData relationship) {
		this.relationship = relationship;
	}
	
	public PeriodDataType getPeriodDataType() {
		return periodDataType;
	}

	public void setPeriodDataType(PeriodDataType periodDataType) {
		this.periodDataType = periodDataType;
	}


	@Override
	public String toString() {
		return "ColumnData [id=" + id + ", columnId=" + columnId + ", name="
				+ name + ", typeName=" + typeName + ", typeCode=" + typeCode
				+ ", dataTypeName=" + dataTypeName + ", label=" + label
				+ ", locale=" + locale + ", trId=" + trId + ", viewColumn="
				+ viewColumn + ", columnViewData=" + columnViewData
				+ ", periodDataType=" + periodDataType + ", relationship="
				+ relationship + ", validationColumnReferences="
				+ validationColumnReferences + ", validationColumn="
				+ validationColumn + ", validatedColumns=" + validatedColumns
				+ "]";
	}
	
	
}
