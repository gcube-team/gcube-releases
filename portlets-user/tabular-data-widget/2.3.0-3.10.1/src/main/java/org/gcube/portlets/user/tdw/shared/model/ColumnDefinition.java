/**
 * 
 */
package org.gcube.portlets.user.tdw.shared.model;

import java.io.Serializable;


/**
 * Describes a column in the widget model.
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 */
public class ColumnDefinition implements Serializable {
	
	private static final long serialVersionUID = 3736483086021088831L;
	
	protected String id;
	protected String label;
	protected ValueType valueType;
	protected int width;
	protected boolean editable;
	protected boolean visible;
	protected ColumnType type;
	protected int position;
	
	protected ColumnKey key;
	
	public ColumnDefinition(){}
	
	/**
	 * Creates a new column definition with type {@link ColumnType} USER.
	 * @param id the column id.
	 * @param label the column label.
	 */
	public ColumnDefinition(String id, String label) {
		this.id = id;
		this.label = label;
		this.type = ColumnType.USER;
		this.position = -1;
	}


	/**
	 * Creates a new column definition.
	 * @param id the column id.
	 * @param label the column label.
	 * @param valueType the type of column values.
	 * @param width the column width.
	 * @param editable flag for editability.
	 * @param visible flag for visibility.
	 * @param type the column type.
	 */
	public ColumnDefinition(String id, String label, ValueType valueType, int width, boolean editable, boolean visible, ColumnType type) {
		this(id, label);
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
	 * @param valueType the valueType to set
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
	 * @param position the position to set
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
	 * @param editable the editable to set
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
	 * @param width the width to set
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
	 * @param visible the visible to set
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
	 * @param type the type to set
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
	 * @param index to set
	 */
	public void setIndex(int index) {
		this.key = new ColumnKey(id, index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ColumnDefinition [id=");
		builder.append(id);
		builder.append(", label=");
		builder.append(label);
		builder.append(", valueType=");
		builder.append(valueType);
		builder.append(", width=");
		builder.append(width);
		builder.append(", editable=");
		builder.append(editable);
		builder.append(", visible=");
		builder.append(visible);
		builder.append(", type=");
		builder.append(type);
		builder.append(", position=");
		builder.append(position);
		builder.append(", key=");
		builder.append(key);
		builder.append("]");
		return builder.toString();
	}
}
