/**
 * 
 */
package org.gcube.portlets.user.tdw.server.datasource.util;

import java.sql.Date;

import org.gcube.portlets.user.tdw.server.util.JSONConstants;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class GridJSonBuilder {
	
	protected enum JGridSonWriterState {DATA, ROWS, ROW, CLOSED};
	protected JGridSonWriterState state;
	protected boolean rowsDeclared;
	protected boolean totalLengthDeclared;
	protected boolean offsetDeclared;

	protected StringBuilder json;
	protected boolean firstField;
	protected boolean firstRow;
	protected boolean firstColumn;
	protected int rowsCount;
	protected int fieldsCount;
	
	public GridJSonBuilder()
	{
		clean();
	}
	
	protected void resetState(){
		state = JGridSonWriterState.DATA;
		rowsDeclared = false;
		totalLengthDeclared = false;
		offsetDeclared = false;
	}
	
	public void clean(){

		json = new StringBuilder();
		json.append('{');
		firstField = true;
		firstRow = true;
		firstColumn = true;
		
		rowsCount = 0;
		fieldsCount = 0;
		
		resetState();
	}
	
	protected void checkState(JGridSonWriterState expectedState)
	{
		if (state!=expectedState) throw new IllegalStateException("Writer in state "+state+" instead of "+expectedState);
	}
	
	protected void checkFirstField()
	{
		if (!firstField) json.append(',');
		else firstField = false;
	}

	/**
	 * Starts the data field.
	 * @param dataField the data field name.
	 */
	public void startRows(String dataField)
	{
		checkState(JGridSonWriterState.DATA);
		if (rowsDeclared) throw new IllegalStateException("Data field already declared");
		state = JGridSonWriterState.ROWS;
		
		checkFirstField();
		json.append("\"");
		json.append(dataField);
		json.append("\":[");
		firstRow = true;
	}

	/**
	 * Starts a new data row.
	 */
	public void startRow()
	{
		checkState(JGridSonWriterState.ROWS);
		state = JGridSonWriterState.ROW;
		fieldsCount = 0;
		firstColumn = true;
		
		if (!firstRow) json.append(',');
		else firstRow = false;
		
		json.append('{');
	}
	
	protected void addKey(String key)
	{
		appendQuoted(key);
		json.append(':');
	}
	
	protected void addValueKey(String key)
	{
		checkState(JGridSonWriterState.ROW);
		
		if (firstColumn) firstColumn = false;
		else json.append(',');
		
		addKey(key);
		fieldsCount++;
	}
	
	/**
	 * Adds a new value in the row.
	 * @param id the value id.
	 * @param value the value to insert.
	 */
	public void addValue(String id, Boolean value)
	{
		addValueKey(id);
		if (value == null) json.append(JSONConstants.NULL);
		else json.append(value?JSONConstants.TRUE:JSONConstants.FALSE);
	}
	
	/**
	 * Adds a new value in the row.
	 * @param id the value id.
	 * @param value the value to insert.
	 */
	public void addValue(String id, Date value)
	{
		addValueKey(id);
		json.append((value==null)?JSONConstants.NULL:value.getTime());
	}
	
	/**
	 * Adds a new value in the row.
	 * @param id the value id.
	 * @param value the value to insert.
	 */
	public void addValue(String id, Double value)
	{
		addValueKey(id);
		json.append((value==null)?JSONConstants.NULL:value);
	}
	
	/**
	 * Adds a new value in the row.
	 * @param id the value id.
	 * @param value the value to insert.
	 */
	public void addValue(String id, Float value)
	{
		addValueKey(id);
		json.append((value==null)?JSONConstants.NULL:value);
	}
	
	/**
	 * Adds a new value in the row.
	 * @param id the value id.
	 * @param value the value to insert.
	 */
	public void addValue(String id, Long value)
	{
		addValueKey(id);
		json.append((value==null)?JSONConstants.NULL:value);
	}
	
	/**
	 * Adds a new value in the row.
	 * @param id the value id.
	 * @param value the value to insert.
	 */
	public void addValue(String id, Integer value)
	{
		addValueKey(id);
		json.append((value==null)?JSONConstants.NULL:value);
	}
	
	/**
	 * Adds a new value in the row.
	 * @param id the value id.
	 * @param value the value to insert.
	 */
	public void addValue(String id, String value)
	{
		addValueKey(id);
		if (value==null) json.append(JSONConstants.NULL);
		else appendQuoted(value);
	}
	
	/**
	 * Ends the data row. 
	 */
	public void endRow()
	{
		checkState(JGridSonWriterState.ROW);
		state = JGridSonWriterState.ROWS;
		rowsCount++;
		
		json.append('}');
	}
	
	/**
	 * Ends the data field. 
	 */
	public void endRows()
	{
		checkState(JGridSonWriterState.ROWS);
		state = JGridSonWriterState.DATA;
		
		json.append("]");
		
		rowsDeclared = true;
	}
	

	/**
	 * Set the total length field.
	 * @param totalLengthField
	 * @param length
	 */
	public void setTotalLength(String totalLengthField, int length)
	{
		checkState(JGridSonWriterState.DATA);
		if (totalLengthDeclared) throw new IllegalStateException("Total length field already declared");
		
		checkFirstField();
		addKey(totalLengthField);
		json.append(length);
		
		totalLengthDeclared = true;
	}
	
	public void setOffset(String offsetField, int offset)
	{
		checkState(JGridSonWriterState.DATA);
		if (offsetDeclared) throw new IllegalStateException("Offset field already declared");
		
		checkFirstField();
		addKey(offsetField);
		json.append(offset);
		
		offsetDeclared = true;
	}
	
	public void close()
	{
		checkState(JGridSonWriterState.DATA);
		if (!rowsDeclared) throw new IllegalStateException("No rows field adeclared");
		if (!totalLengthDeclared) throw new IllegalStateException("No total length field declared");
		if (!offsetDeclared) throw new IllegalStateException("No offset field declared");
		state = JGridSonWriterState.CLOSED;
		
		json.append("}");
	}
	
	public String toString()
	{
		checkState(JGridSonWriterState.CLOSED);
		return json.toString();
	}
	
	/**
	 * Produce a string in double quotes with backslash sequences in all the
	 * right places. A backslash will be inserted within </, allowing JSON
	 * text to be delivered in HTML. In JSON text, a string cannot contain a
	 * control character or an unescaped quote or backslash.
	 * @param value A String
	 * @return  A String correctly formatted for insertion in a JSON text.
	 */
	protected void appendQuoted(String value) {
		if (value == null || value.length() == 0) {
			json.append("\"\"");
		} else {

			char b;
			char c = 0;
			int i;
			int len = value.length();
			String t;

			json.append('"');
			for (i = 0; i < len; i += 1) {
				b = c;
				c = value.charAt(i);
				switch (c) {
					case '\\':
					case '"':
						json.append('\\');
						json.append(c);
						break;
					case '/':
						if (b == '<') {
							json.append('\\');
						}
						json.append(c);
						break;
					case '\b':
						json.append("\\b");
						break;
					case '\t':
						json.append("\\t");
						break;
					case '\n':
						json.append("\\n");
						break;
					case '\f':
						json.append("\\f");
						break;
					case '\r':
						json.append("\\r");
						break;
					default:
						if (c < ' ' || (c >= '\u0080' && c < '\u00a0') ||
								(c >= '\u2000' && c < '\u2100')) {
							t = "000" + Integer.toHexString(c);
							json.append("\\u" + t.substring(t.length() - 4));
						} else {
							json.append(c);
						}
				}
			}
			json.append('"');
		}
	}

}
