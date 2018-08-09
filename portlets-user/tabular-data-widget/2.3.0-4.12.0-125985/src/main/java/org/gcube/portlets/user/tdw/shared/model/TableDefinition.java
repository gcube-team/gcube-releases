/**
 * 
 */
package org.gcube.portlets.user.tdw.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Describes a table in the widget model.
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class TableDefinition implements Serializable {
	
	private static final long serialVersionUID = 6278293464659249512L;
	
	public static final String DEFAULT_JSON_ROWS_FIELD = "rows";
	public static final String DEFAULT_JSON_TOTAL_LENGTH_FIELD = "total";
	public static final String DEFAULT_JSON_OFFSET_FIELD = "offset";

	protected TableId id;
	protected String name;
	protected String jsonRowsField;
	protected String jsonTotalLengthField;
	protected String jsonOffsetField;
	protected String modelKeyColumnId;
	
	protected Map<String, ColumnDefinition> columns;
	protected List<ColumnKey> keys;
	protected int keySeed = 0;
	
	public TableDefinition(){}
	
	/**
	 * Creates a new Table definition.
	 * @param id the table id.
	 * @param name the table name.
	 */
	public TableDefinition(TableId id, String name)
	{
		this.id = id;
		this.name = name;
		this.jsonRowsField = DEFAULT_JSON_ROWS_FIELD;
		this.jsonTotalLengthField = DEFAULT_JSON_TOTAL_LENGTH_FIELD;
		this.jsonOffsetField = DEFAULT_JSON_OFFSET_FIELD;
		this.columns = new HashMap<String, ColumnDefinition>();
		this.keys = new ArrayList<ColumnKey>();
	}

	/**
	 * Creates a new Table definition.
	 * @param id the table id.
	 * @param name the table name.
	 * @param jsonRowsField the rows field in the JSON data object.
	 * @param jsonTotalLengthField the total length field in the JSON data object.
	 * @param jsonOffsetField the offset field in the JSON data object.
	 */
	public TableDefinition(TableId id, String name, String jsonRowsField, String jsonTotalLengthField, String jsonOffsetField)
	{
		this(id, name);
		this.jsonRowsField = jsonRowsField;
		this.jsonTotalLengthField = jsonTotalLengthField;
		this.jsonOffsetField = jsonOffsetField;
	}

	/**
	 * Creates a new Table definition.
	 * @param id the table id.
	 * @param name the table name.
	 * @param jsonRowsField the rows field in the JSON data object.
	 * @param jsonTotalLengthField the total length field in the JSON object.
	 * @param jsonOffsetField the offset field in the JSON object.
	 * @param columns the column definitions as column id - column definition map.
	 */
	public TableDefinition(TableId id, String name, String jsonRowsField, String jsonTotalLengthField, String jsonOffsetField, Map<String, ColumnDefinition> columns) {
		this(id, name, jsonRowsField, jsonTotalLengthField, jsonOffsetField);
		
		for (ColumnDefinition column:columns.values()) addColumn(column);
	}

	/**
	 * Creates a new Table definition.
	 * @param id the table id.
	 * @param name the table name.
	 * @param jsonRowsField the rows field in the JSON data object.
	 * @param jsonTotalLengthField the total length field in the JSON object.
	 * @param jsonOffsetField the offset field in the JSON object.
	 * @param columns the column definitions as column definition list.
	 */
	public TableDefinition(TableId id, String name, String jsonRowsField, String jsonTotalLengthField, String jsonOffsetField, List<ColumnDefinition> columns) {
		this(id, name, jsonRowsField, jsonTotalLengthField, jsonOffsetField);
		
		this.columns = new HashMap<String, ColumnDefinition>();
		keys = new ArrayList<ColumnKey>(columns.size());
		for (ColumnDefinition column:columns) addColumn(column);
	}
	
	/**
	 * @return the table id.
	 */
	public TableId getId() {
		return id;
	}

	/**
	 * @return the table name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the JSON object rows field name.
	 */
	public String getJsonRowsField() {
		return jsonRowsField;
	}

	/**
	 * @param jsonRowsField the jsonRowsField to set
	 */
	public void setJsonRowsField(String jsonRowsField) {
		this.jsonRowsField = jsonRowsField;
	}

	/**
	 * @return the JSON object length field name.
	 */
	public String getJsonTotalLengthField() {
		return jsonTotalLengthField;
	}
	
	/**
	 * @param jsonTotalLengthField the jsonTotalLengthField to set
	 */
	public void setJsonTotalLengthField(String jsonTotalLengthField) {
		this.jsonTotalLengthField = jsonTotalLengthField;
	}

	/**
	 * @return the JSON object offset field name.
	 */
	public String getJsonOffsetField() {
		return jsonOffsetField;
	}

	/**
	 * @param jsonOffsetField the jsonOffsetField to set
	 */
	public void setJsonOffsetField(String jsonOffsetField) {
		this.jsonOffsetField = jsonOffsetField;
	}

	/**
	 * @return the model Key column.
	 */
	public ColumnDefinition getModelKeyColumn() {
		return columns.get(modelKeyColumnId);
	}

	/**
	 * Sets the model key column.
	 * @param modelKeyColumn the modelKeyColumn to set
	 */
	public void setModelKeyColumnId(String modelKeyColumnId) {
		this.modelKeyColumnId = modelKeyColumnId;
	}

	/**
	 * Returns the column definitions.
	 * @return the column definitions
	 */
	public Map<String, ColumnDefinition> getColumns() {
		return columns;
	}
	
	public List<ColumnDefinition> getColumnsAsList() {
		return new ArrayList<ColumnDefinition>(columns.values());
	}
	
	/**
	 * Adds a new column definition.
	 * @param column the column definition.
	 */
	public void addColumn(ColumnDefinition column)
	{
		columns.put(column.getId(), column);
		column.setIndex(keySeed++);
		keys.add(column.getKey());
	}
	
	/**
	 * Returns the columns keys.
	 * @return the columns keys.
	 */
	public List<ColumnKey> getKeys()
	{
		return keys;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TableDefinition [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", jsonRowsField=");
		builder.append(jsonRowsField);
		builder.append(", jsonOffsetField=");
		builder.append(jsonOffsetField);
		builder.append(", jsonTotalLengthField=");
		builder.append(jsonTotalLengthField);
		builder.append(", modelKeyColumnId=");
		builder.append(modelKeyColumnId);
		builder.append(", columns=");
		builder.append(columns);
		builder.append("]");
		return builder.toString();
	}
}
