/**
 * 
 */
package org.gcube.portlets.user.tdw.server;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Date;

import org.gcube.portlets.user.tdw.server.datasource.util.TableJSonWriter;
import org.gcube.portlets.user.tdw.shared.model.ColumnDefinition;
import org.gcube.portlets.user.tdw.shared.model.ColumnType;
import org.gcube.portlets.user.tdw.shared.model.TableDefinition;
import org.gcube.portlets.user.tdw.shared.model.TableId;
import org.gcube.portlets.user.tdw.shared.model.ValueType;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class TestTableJSonWriter {
	
	@Test
	public void testTableJSonWriter() 
	{
		TableDefinition tableDefinition = new TableDefinition(new TableId("", "id"), "name", "jsonDataField", "jsonTotalLengthField", "jsonOffsetField");
		tableDefinition.addColumn(new ColumnDefinition("boolean", "boolean", ValueType.BOOLEAN, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("date", "date", ValueType.DATE, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("double", "double", ValueType.DOUBLE, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("float", "float", ValueType.FLOAT, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("long", "long", ValueType.LONG, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("string", "string", ValueType.STRING, 1, true, true, ColumnType.USER));
		
		
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
		tableJSonWriter.addValue("float", 2.3f);
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
