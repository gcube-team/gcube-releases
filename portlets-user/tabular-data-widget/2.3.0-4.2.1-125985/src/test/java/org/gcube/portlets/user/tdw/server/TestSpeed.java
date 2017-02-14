/**
 * 
 */
package org.gcube.portlets.user.tdw.server;

import java.io.IOException;
import java.sql.Date;

import org.apache.commons.io.output.NullWriter;
import org.gcube.portlets.user.tdw.server.datasource.util.TableJSonBuilder;
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
public class TestSpeed {

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	@Test
	public void test_speed()  {
		boolean wasExceptionThrown=false;
		String error="";
		try{ 
			testWriter();
			Runtime.getRuntime().gc();
			testBuilder();
		} catch (final IOException e)
			  {
			    wasExceptionThrown = true;
			    error="IOException: "+e.getLocalizedMessage();
		}
		Assert.assertFalse("Error "+error, wasExceptionThrown);	
		
	}

	protected static void testWriter() throws IOException
	{
		TableDefinition tableDefinition = new TableDefinition(new TableId("", "id"), "name", "jsonDataField", "jsonTotalLengthField", "jsonOffsetField");
		tableDefinition.addColumn(new ColumnDefinition("boolean", "boolean", ValueType.BOOLEAN, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("date", "date", ValueType.DATE, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("double", "double", ValueType.DOUBLE, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("float", "float", ValueType.FLOAT, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("long", "long", ValueType.LONG, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("string", "string", ValueType.STRING, 1, true, true, ColumnType.USER));


		long startFreeMemory = Runtime.getRuntime().freeMemory();
		NullWriter writer = new NullWriter();
		//NullWriter

		TableJSonWriter tableJSonWriter = new TableJSonWriter(writer, tableDefinition);

		long start = System.currentTimeMillis();
		tableJSonWriter.startData();

		for (int i = 0; i < 1000000; i++) {
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

		System.out.println("Time Writer : "+(System.currentTimeMillis()-start));
		long deltaMemory = startFreeMemory-Runtime.getRuntime().freeMemory();
		System.out.println("freeMemory: "+deltaMemory);

		//System.out.println(writer.toString().length());

	}

	protected static void testBuilder() throws IOException
	{
		TableDefinition tableDefinition = new TableDefinition(new TableId("", "id"), "name", "jsonDataField", "jsonTotalLengthField", "jsonOffsetField");
		tableDefinition.addColumn(new ColumnDefinition("boolean", "boolean", ValueType.BOOLEAN, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("date", "date", ValueType.DATE, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("double", "double", ValueType.DOUBLE, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("float", "float", ValueType.FLOAT, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("long", "long", ValueType.LONG, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("string", "string", ValueType.STRING, 1, true, true, ColumnType.USER));


		TableJSonBuilder gridJSonWriter = new TableJSonBuilder(tableDefinition);

		long startFreeMemory = Runtime.getRuntime().freeMemory();
		long start = System.currentTimeMillis();
		gridJSonWriter.startRows();

		for (int i = 0; i < 1000000; i++) {
			gridJSonWriter.startRow();
			gridJSonWriter.addValue("boolean", false);
			gridJSonWriter.addValue("date", new Date(1));
			gridJSonWriter.addValue("double", 2.1);
			gridJSonWriter.addValue("float", 2.3f);
			gridJSonWriter.addValue("long", 123l);
			gridJSonWriter.addValue("string", "This is a string");
			gridJSonWriter.endRow();
		}

		gridJSonWriter.endRows();

		gridJSonWriter.setOffset(12);
		gridJSonWriter.setTotalLength(450);

		gridJSonWriter.close();

		long deltaMemory = startFreeMemory-Runtime.getRuntime().freeMemory();
		String data = gridJSonWriter.toString();


		System.out.println("Time Reader: "+(System.currentTimeMillis()-start));
		
		System.out.println("freeMemory: "+deltaMemory);

		System.out.println(data.length());

	}

}
