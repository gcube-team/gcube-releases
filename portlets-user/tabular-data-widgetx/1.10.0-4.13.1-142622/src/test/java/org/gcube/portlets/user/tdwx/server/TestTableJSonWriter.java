/**
 * 
 */
package org.gcube.portlets.user.tdwx.server;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Date;

import org.gcube.portlets.user.tdwx.server.datasource.util.TableJSonWriter;
import org.gcube.portlets.user.tdwx.shared.model.ColumnDefinition;
import org.gcube.portlets.user.tdwx.shared.model.ColumnType;
import org.gcube.portlets.user.tdwx.shared.model.TableDefinition;
import org.gcube.portlets.user.tdwx.shared.model.TableId;
import org.gcube.portlets.user.tdwx.shared.model.ValueType;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TestTableJSonWriter {
	
	@Test
	public void testTableJSonWriter() 
	{
		TableDefinition tableDefinition = new TableDefinition(new TableId("", "id"), "name", "jsonDataField", "jsonTotalLengthField", "jsonOffsetField");
		tableDefinition.addColumn(new ColumnDefinition("boolean", "boolean", "boolean", ValueType.BOOLEAN, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("date", "date", "date", ValueType.DATE, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("double", "double", "double", ValueType.DOUBLE, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("long", "long", "long", ValueType.LONG, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("string", "string", "string", ValueType.STRING, 1, true, true, ColumnType.USER));
	
		
		StringWriter writer = new StringWriter();
		
		boolean wasExceptionThrown=false;
		String error="";
		try {
		TableJSonWriter tableJSonWriter = new TableJSonWriter(writer, tableDefinition);
		
		tableJSonWriter.startData();
		
		for (int i = 0; i < 1000; i++) {
		tableJSonWriter.startRow();
		tableJSonWriter.addValue("boolean", false);
		tableJSonWriter.addValue("date", new Date(1));
		tableJSonWriter.addValue("double", 2.1);
		tableJSonWriter.addValue("long", 123l);
		tableJSonWriter.addValue("string", "This is a string");
		tableJSonWriter.endRow();
		}
		
		tableJSonWriter.endData();
		
		tableJSonWriter.setOffset(12);
		tableJSonWriter.setTotalLength(450);
		
		tableJSonWriter.close();
		
		} catch (final IOException e) {
			wasExceptionThrown = true;
			error = "IOException: " + e.getLocalizedMessage();
		}
		Assert.assertFalse("Error " + error, wasExceptionThrown);

		System.out.println(writer.toString());
		
	}

}
