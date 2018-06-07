/**
 * 
 */
package org.gcube.portlets.user.tdw.client.config;

import java.util.Map;

import org.gcube.portlets.user.tdw.shared.model.ColumnKey;

import org.gcube.portlets.user.tdw.shared.model.DataRow;

/**
 * Represents a table row.
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 */
public class Row {
	
	protected DataRow dataRow;
	protected Map<String, ColumnKey> keys;

	/**
	 * @param keys
	 */
	public Row(Map<String, ColumnKey> keys) {
		this.keys = keys;
	}

	/**
	 * @param dataRow
	 * @param keys
	 */
	public Row(DataRow dataRow, Map<String, ColumnKey> keys) {
		this.dataRow = dataRow;
		this.keys = keys;
	}

	/**
	 * @param dataRow the dataRow to set
	 */
	protected void setDataRow(DataRow dataRow) {
		this.dataRow = dataRow;
	}

	/**
	 * Returns the specified field as text value.
	 * @param fieldId the field id.
	 * @return the text value.
	 */
	public String getFieldAsText(String fieldId)
	{
		Object value = getField(fieldId);
		return String.valueOf(value);
	}
	
	/**
	 * Returns the specified field as request value type T.
	 * If a wrong type is specified a {@link ClassCastException} will be throw.
	 * @param fieldId the field id.
	 * @return the field value.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getField(String fieldId)
	{
		ColumnKey key = keys.get(fieldId);
		return (T) dataRow.get(key);
	}

}
