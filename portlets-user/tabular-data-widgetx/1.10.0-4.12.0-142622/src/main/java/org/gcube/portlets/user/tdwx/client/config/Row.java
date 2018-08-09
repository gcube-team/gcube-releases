/**
 * 
 */
package org.gcube.portlets.user.tdwx.client.config;

import java.util.Date;
import java.util.Map;


import org.gcube.portlets.user.tdwx.shared.model.ColumnKey;
import org.gcube.portlets.user.tdwx.shared.model.DataRow;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
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
	 * Returns the specified field as text value.
	 * @param fieldId the field id.
	 * @return the text value.
	 */
	public String getFieldAsDate(String fieldId)
	{
		Date value = (Date)getField(fieldId);
		DateTimeFormat sdf= DateTimeFormat.getFormat("yyyy-MM-dd");
		return sdf.format(value);
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
