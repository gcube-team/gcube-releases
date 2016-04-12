/**
 * 
 */
package org.gcube.portlets.user.tdwx.client.model.grid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.tdwx.client.model.json.JSonValue;
import org.gcube.portlets.user.tdwx.shared.model.ColumnDefinition;
import org.gcube.portlets.user.tdwx.shared.model.ColumnKey;
import org.gcube.portlets.user.tdwx.shared.model.DataRow;
import org.gcube.portlets.user.tdwx.shared.model.ValueType;

import com.google.gwt.core.client.JsArray;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class JSonValueConverter {

	protected ColumnDefinition[] columns;

	public JSonValueConverter(List<ColumnDefinition> columns)
	{
		this.columns = columns.toArray(new ColumnDefinition[columns.size()]);
	}

	public List<DataRow> convertToDataRow(List<JSonValue> json)
	{
		List<DataRow> rows = new ArrayList<DataRow>(json.size());
		for (JSonValue value:json) rows.add(convertToDataRow(value));
		return rows;
	}

	public List<DataRow> convertToDataRow(JsArray<JSonValue> json)
	{
		List<DataRow> rows = new ArrayList<DataRow>(json.length());
		for (int i = 0; i < json.length(); i++) rows.add(convertToDataRow(json.get(i)));
		return rows;
	}

	public DataRow convertToDataRow(JSonValue json)
	{
		DataRow row = new DataRow(columns.length);
		for (ColumnDefinition column:columns) setValue(row, json, column.getKey(), column.getValueType());
		return row;
	}

	protected void setValue(DataRow row, JSonValue json, ColumnKey key, ValueType type)
	{
		try {
		if (json.isNull(key.getJSonIndex())) row.set(key, null);
		else {
			switch (type) {
				case BOOLEAN: row.set(key, Boolean.valueOf(json.getAsBool(key.getJSonIndex()))); break;
				case DATE: row.set(key, new Date((long)json.getAsNumber(key.getJSonIndex()))); break;
				case DOUBLE: row.set(key, Double.valueOf(json.getAsNumber(key.getJSonIndex()))); break;
				case INTEGER: row.set(key, Integer.valueOf((int)json.getAsNumber(key.getJSonIndex()))); break;
				case LONG: row.set(key, Long.valueOf((long)json.getAsNumber(key.getJSonIndex()))); break;
				case STRING: row.set(key, json.getAsString(key.getJSonIndex())); break;
				case GEOMETRY: row.set(key, json.getAsString(key.getJSonIndex())); break;				
				default: row.set(key, null); break;
			}
		}
		} catch(IllegalArgumentException iae)
		{
			iae.printStackTrace();
			System.out.println("JSonIndex: "+key.getJSonIndex());
		}
	}

}
