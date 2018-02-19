/**
 * 
 */
package org.gcube.portlets.user.tdwx.server;

import java.sql.Date;

import org.gcube.portlets.user.tdwx.server.datasource.util.TableJSonBuilder;
import org.gcube.portlets.user.tdwx.shared.model.ColumnDefinition;
import org.gcube.portlets.user.tdwx.shared.model.ColumnType;
import org.gcube.portlets.user.tdwx.shared.model.TableDefinition;
import org.gcube.portlets.user.tdwx.shared.model.TableId;
import org.gcube.portlets.user.tdwx.shared.model.ValueType;
import org.junit.Test;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TestTableJSonBuilder {
	
	@Test
	public void testTableJSonBuilder()
	{
		TableDefinition tableDefinition = new TableDefinition(new TableId("", "id"), "name", "jsonDataField", "jsonTotalLengthField", "jsonOffsetField");
		tableDefinition.addColumn(new ColumnDefinition("boolean", "boolean", "boolean", ValueType.BOOLEAN, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("date", "date", "date", ValueType.DATE, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("double", "double", "double", ValueType.DOUBLE, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("long", "long", "long", ValueType.LONG, 1, true, true, ColumnType.USER));
		tableDefinition.addColumn(new ColumnDefinition("string", "string", "string", ValueType.STRING, 1, true, true, ColumnType.USER));
		
		TableJSonBuilder gridJSonWriter = new TableJSonBuilder(tableDefinition);
		
		gridJSonWriter.startRows();
		
		gridJSonWriter.startRow();
		gridJSonWriter.addValue("boolean", false);
		gridJSonWriter.addValue("date", new Date(1));
		gridJSonWriter.addValue("double", 2.1);
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
