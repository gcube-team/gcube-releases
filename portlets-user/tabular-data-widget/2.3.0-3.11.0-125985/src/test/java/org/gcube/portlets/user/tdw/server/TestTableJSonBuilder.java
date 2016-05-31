/**
 * 
 */
package org.gcube.portlets.user.tdw.server;

import java.sql.Date;

import org.gcube.portlets.user.tdw.server.datasource.util.TableJSonBuilder;
import org.gcube.portlets.user.tdw.shared.model.ColumnDefinition;
import org.gcube.portlets.user.tdw.shared.model.ColumnType;
import org.gcube.portlets.user.tdw.shared.model.TableDefinition;
import org.gcube.portlets.user.tdw.shared.model.TableId;
import org.gcube.portlets.user.tdw.shared.model.ValueType;
import org.junit.Test;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class TestTableJSonBuilder {
	
	@Test
	public void testTableJSonBuilder()
	{
		TableDefinition tableDefinition = new TableDefinition(new TableId("", "id"), "name", "jsonDataField", "jsonTotalLengthField", "jsonOffsetField");
		tableDefinition.addColumn(new ColumnDefinition("boolean", "boolean", ValueType.BOOLEAN, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("date", "date", ValueType.DATE, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("double", "double", ValueType.DOUBLE, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("float", "float", ValueType.FLOAT, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("long", "long", ValueType.LONG, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("string", "string", ValueType.STRING, 1, true, true, ColumnType.USER));
		
		TableJSonBuilder gridJSonWriter = new TableJSonBuilder(tableDefinition);
		
		gridJSonWriter.startRows();
		
		gridJSonWriter.startRow();
		gridJSonWriter.addValue("boolean", false);
		gridJSonWriter.addValue("date", new Date(1));
		gridJSonWriter.addValue("double", 2.1);
		gridJSonWriter.addValue("float", 2.3f);
		gridJSonWriter.addValue("long", 123l);
		gridJSonWriter.addValue("string", "This is a string");
		gridJSonWriter.endRow();
		
		gridJSonWriter.endRows();
		
		gridJSonWriter.setOffset(12);
		gridJSonWriter.setTotalLength(450);
		
		gridJSonWriter.close();
		
		System.out.println(gridJSonWriter.toString());
		
	}

}
