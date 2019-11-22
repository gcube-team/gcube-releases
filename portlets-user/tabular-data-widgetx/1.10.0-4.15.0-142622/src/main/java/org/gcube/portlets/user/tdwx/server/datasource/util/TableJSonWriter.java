/**
 * 
 */
package org.gcube.portlets.user.tdwx.server.datasource.util;

import java.io.IOException;
import java.io.Writer;

import org.gcube.portlets.user.tdwx.shared.model.TableDefinition;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TableJSonWriter extends GridJSonWriter {
	
	protected TableDefinition tableDefinition;
	
	public TableJSonWriter(Writer writer, TableDefinition tableDefinition) throws IOException
	{
		super(writer);
		this.tableDefinition = tableDefinition; 
	}

	public void startData() throws IOException {
		super.startData(tableDefinition.getJsonRowsField());
	}

	public void endRow() throws IOException
	{
		if (fieldsCount != tableDefinition.getColumns().size()) throw new IllegalStateException("Expected "+tableDefinition.getColumns().size()+" fields, added "+fieldsCount);
		super.endRow();
	}
	
	public void setTotalLength(int length) throws IOException
	{
		super.setTotalLength(tableDefinition.getJsonTotalLengthField(), length);
	}
	
	public void setOffset(int offset) throws IOException
	{
		super.setOffset(tableDefinition.getJsonOffsetField(), offset);
	}

}
