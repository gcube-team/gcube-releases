/**
 * 
 */
package org.gcube.portlets.user.tdwx.server.datasource.util;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;


import org.gcube.portlets.user.tdwx.server.util.JSONConstants;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class GridJSonWriter {
	
	protected enum JGridSonWriterState {READY, DATA, INROW, CLOSED};
	protected JGridSonWriterState state;
	protected boolean dataDeclared = false;
	protected boolean totalLengthDeclared = false;
	protected boolean offsetDeclared = false;

	protected Writer writer;
	protected boolean firstField = true;
	protected boolean firstRow = true;
	protected boolean firstColumn = true;
	protected int rowsCount = 0;
	protected int fieldsCount;
	
	public GridJSonWriter(Writer writer) throws IOException
	{
		this.writer = writer;
		writer.append('{');
		state = JGridSonWriterState.READY;
	}
	
	protected void checkState(JGridSonWriterState expectedState)
	{
		if (state!=expectedState) throw new IllegalStateException("Writer in state "+state+" instead of "+expectedState);
	}
	
	protected void checkFirstField() throws IOException
	{
		if (!firstField) writer.append(',');
		else firstField = false;
	}

	/**
	 * Starts the data field.
	 * @param dataField the data field name.
	 * @throws IOException 
	 */
	public void startData(String dataField) throws IOException
	{
		checkState(JGridSonWriterState.READY);
		if (dataDeclared) throw new IllegalStateException("Data field already declared");
		state = JGridSonWriterState.DATA;
		
		checkFirstField();
		writer.append('\"');
		writer.append(dataField);
		writer.append("\":[");
		firstRow = true;
	}

	/**
	 * Starts a new data row.
	 * @throws IOException 
	 */
	public void startRow() throws IOException
	{
		checkState(JGridSonWriterState.DATA);
		state = JGridSonWriterState.INROW;
		fieldsCount = 0;
		firstColumn = true;
		
		if (!firstRow) writer.append(',');
		else firstRow = false;
		
		writer.append('{');
	}
	
	protected void addKey(String key) throws IOException
	{
		appendQuoted(key);
		writer.append(':');
	}
	
	protected void addValueKey(String key) throws IOException
	{
		checkState(JGridSonWriterState.INROW);
		
		if (firstColumn) firstColumn = false;
		else writer.append(',');
		
		addKey(key);
		fieldsCount++;
	}
	
	/**
	 * Adds a new value in the row.
	 * @param id the value id.
	 * @param value the value to insert.
	 * @throws IOException 
	 */
	public void addValue(String id, Boolean value) throws IOException
	{
		addValueKey(id);
		if (value == null) writer.append(JSONConstants.NULL);
		else writer.append(value?JSONConstants.TRUE:JSONConstants.FALSE);
	}
	
	/**
	 * Adds a new value in the row.
	 * @param id the value id.
	 * @param value the value to insert.
	 * @throws IOException 
	 */
	public void addValue(String id, Date value) throws IOException
	{
		addValueKey(id);
		writer.append((value==null)?JSONConstants.NULL:String.valueOf(value.getTime()));
	}
	
	/**
	 * Adds a new value in the row.
	 * @param id the value id.
	 * @param value the value to insert.
	 * @throws IOException 
	 */
	public void addValue(String id, Double value) throws IOException
	{
		addValueKey(id);
		writer.append((value==null)?JSONConstants.NULL:String.valueOf(value));
	}
	
	
	
	/**
	 * Adds a new value in the row.
	 * @param id the value id.
	 * @param value the value to insert.
	 * @throws IOException 
	 */
	public void addValue(String id, Long value) throws IOException
	{
		addValueKey(id);
		writer.append((value==null)?JSONConstants.NULL:String.valueOf(value));
	}
	
	/**
	 * Adds a new value in the row.
	 * @param id the value id.
	 * @param value the value to insert.
	 * @throws IOException 
	 */
	public void addValue(String id, String value) throws IOException
	{
		addValueKey(id);
		if (value==null) writer.append(JSONConstants.NULL);
		else appendQuoted(value);
	}
	
	/**
	 * Ends the data row. 
	 * @throws IOException 
	 */
	public void endRow() throws IOException
	{
		checkState(JGridSonWriterState.INROW);
		state = JGridSonWriterState.DATA;
		rowsCount++;
		
		writer.append('}');
	}
	
	/**
	 * Ends the data field. 
	 * @throws IOException 
	 */
	public void endData() throws IOException
	{
		checkState(JGridSonWriterState.DATA);
		state = JGridSonWriterState.READY;
		
		writer.append("]");
		
		dataDeclared = true;
	}
	

	/**
	 * Set the total length field.
	 * @param totalLengthField
	 * @param length
	 * @throws IOException 
	 */
	public void setTotalLength(String totalLengthField, int length) throws IOException
	{
		checkState(JGridSonWriterState.READY);
		if (totalLengthDeclared) throw new IllegalStateException("Total length field already declared");
		
		checkFirstField();
		addKey(totalLengthField);
		writer.append(String.valueOf(length));
		
		totalLengthDeclared = true;
	}
	
	public void setOffset(String offsetField, int offset) throws IOException
	{
		checkState(JGridSonWriterState.READY);
		if (offsetDeclared) throw new IllegalStateException("Offset field already declared");
		
		checkFirstField();
		addKey(offsetField);
		writer.append(String.valueOf(offset));
		
		offsetDeclared = true;
	}
	
	public void close() throws IOException
	{
		checkState(JGridSonWriterState.READY);
		if (!dataDeclared) throw new IllegalStateException("No data field adeclared");
		if (!totalLengthDeclared) throw new IllegalStateException("No total length field declared");
		if (!offsetDeclared) throw new IllegalStateException("No offset field declared");
		state = JGridSonWriterState.CLOSED;
		
		writer.append('}');
	}
	
	/**
	 * Produce a string in double quotes with backslash sequences in all the
	 * right places. A backslash will be inserted within </, allowing JSON
	 * text to be delivered in HTML. In JSON text, a string cannot contain a
	 * control character or an unescaped quote or backslash.
	 * @param value A String
	 * @return  A String correctly formatted for insertion in a JSON text.
	 * @throws IOException 
	 */
	protected void appendQuoted(String value) throws IOException {
		if (value == null || value.length() == 0) {
			writer.append("\"\"");
		} else {

			char b;
			char c = 0;
			int i;
			int len = value.length();
			String t;

			writer.append('"');
			for (i = 0; i < len; i += 1) {
				b = c;
				c = value.charAt(i);
				switch (c) {
					case '\\':
					case '"':
						writer.append('\\');
						writer.append(c);
						break;
					case '/':
						if (b == '<') {
							writer.append('\\');
						}
						writer.append(c);
						break;
					case '\b':
						writer.append("\\b");
						break;
					case '\t':
						writer.append("\\t");
						break;
					case '\n':
						writer.append("\\n");
						break;
					case '\f':
						writer.append("\\f");
						break;
					case '\r':
						writer.append("\\r");
						break;
					default:
						if (c < ' ' || (c >= '\u0080' && c < '\u00a0') ||
								(c >= '\u2000' && c < '\u2100')) {
							t = "000" + Integer.toHexString(c);
							writer.append("\\u" + t.substring(t.length() - 4));
						} else {
							writer.append(c);
						}
				}
			}
			writer.append('"');
		}
	}

}
