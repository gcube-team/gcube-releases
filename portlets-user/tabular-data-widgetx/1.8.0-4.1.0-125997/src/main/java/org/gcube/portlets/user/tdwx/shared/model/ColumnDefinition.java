/**
 * 
 */
package org.gcube.portlets.user.tdwx.shared.model;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.RelationshipData;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ColumnDefinition implements Serializable {

	private static final long serialVersionUID = 3736483086021088831L;

	protected String id; // Column Name on service
	protected String columnLocalId; // ColumnLocalId on service
	protected String label; // Label
	
	//References for Dimenson and TimeDimension Column
	protected RelationshipData relationshipData;
	
	protected boolean viewColumn;
	// View Column References
	// Id column inside current table for index of
	// Dimension and TimeDimension
	protected String sourceTableDimensionColumnId;
	// Column id inside external table for
	// Dimension and TimeDimension
	protected String targetTableColumnId;
	// Table id inside external for Dimension and
	// TimeDimension
	protected long targetTableId;
	
	
	protected String locale;

	protected ValueType valueType;
	protected int width;
	protected boolean editable;
	protected boolean visible;
	protected ColumnType type;
	protected int position;
	protected String tooltipMessage;
	protected String columnTypeName;
	protected String columnDataType;

	protected ColumnKey key;

	public ColumnDefinition() {
	}

	/**
	 * Creates a new column definition with type {@link ColumnType} USER.
	 * 
	 * @param id
	 *            the column id.
	 * @param label
	 *            the column label.
	 */
	public ColumnDefinition(String id, String columnLocalId, String label) {
		this.id = id;
		this.columnLocalId = columnLocalId;
		this.label = label;
		this.type = ColumnType.USER;
		this.position = -1;
	}

	/**
	 * Creates a new column definition.
	 * 
	 * @param id
	 *            the column id.
	 * @param label
	 *            the column label.
	 * @param valueType
	 *            the type of column values.
	 * @param width
	 *            the column width.
	 * @param editable
	 *            flag for editability.
	 * @param visible
	 *            flag for visibility.
	 * @param type
	 *            the column type.
	 */
	public ColumnDefinition(String id, String columnLocalId, String label,
			ValueType valueType, int width, boolean editable, boolean visible,
			ColumnType type) {
		this(id, columnLocalId, label);
		this.valueType = valueType;
		this.width = width;
		this.editable = editable;
		this.visible = visible;
		this.type = type;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the valueType
	 */
	public ValueType getValueType() {
		return valueType;
	}

	/**
	 * @param valueType
	 *            the valueType to set
	 */
	public void setValueType(ValueType valueType) {
		this.valueType = valueType;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * @return the editable
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * @param editable
	 *            the editable to set
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible
	 *            the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return the type
	 */
	public ColumnType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(ColumnType type) {
		this.type = type;
	}

	/**
	 * @return the key
	 */
	public ColumnKey getKey() {
		return key;
	}

	/**
	 * @param index
	 *            to set
	 */
	public void setIndex(int index) {
		this.key = new ColumnKey(id, index);
	}

	public String getTooltipMessage() {
		return tooltipMessage;
	}

	public void setTooltipMessage(String tooltipMessage) {
		this.tooltipMessage = tooltipMessage;
	}

	public String getColumnTypeName() {
		return columnTypeName;
	}

	public void setColumnTypeName(String columnTypeName) {
		this.columnTypeName = columnTypeName;
	}

	public String getColumnDataType() {
		return columnDataType;
	}

	public void setColumnDataType(String columnDataType) {
		this.columnDataType = columnDataType;
	}

	public String getColumnLocalId() {
		return columnLocalId;
	}

	public void setColumnLocalId(String columnLocalId) {
		this.columnLocalId = columnLocalId;
	}

	public boolean isViewColumn() {
		return viewColumn;
	}

	public void setViewColumn(boolean viewColumn) {
		this.viewColumn = viewColumn;
	}

	public String getSourceTableDimensionColumnId() {
		return sourceTableDimensionColumnId;
	}

	public void setSourceTableDimensionColumnId(
			String sourceTableDimensionColumnId) {
		this.sourceTableDimensionColumnId = sourceTableDimensionColumnId;
	}

	public String getTargetTableColumnId() {
		return targetTableColumnId;
	}

	public void setTargetTableColumnId(String targetTableColumnId) {
		this.targetTableColumnId = targetTableColumnId;
	}

	public long getTargetTableId() {
		return targetTableId;
	}

	public void setTargetTableId(long targetTableId) {
		this.targetTableId = targetTableId;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public RelationshipData getRelationshipData() {
		return relationshipData;
	}

	public void setRelationshipData(RelationshipData relationshipData) {
		this.relationshipData = relationshipData;
	}

	@Override
	public String toString() {
		return "ColumnDefinition [id=" + id + ", columnLocalId="
				+ columnLocalId + ", label=" + label + ", relationshipData="
				+ relationshipData + ", viewColumn=" + viewColumn
				+ ", sourceTableDimensionColumnId="
				+ sourceTableDimensionColumnId + ", targetTableColumnId="
				+ targetTableColumnId + ", targetTableId=" + targetTableId
				+ ", locale=" + locale + ", valueType=" + valueType
				+ ", width=" + width + ", editable=" + editable + ", visible="
				+ visible + ", type=" + type + ", position=" + position
				+ ", tooltipMessage=" + tooltipMessage + ", columnTypeName="
				+ columnTypeName + ", columnDataType=" + columnDataType
				+ ", key=" + key + "]";
	}

	
	

}
