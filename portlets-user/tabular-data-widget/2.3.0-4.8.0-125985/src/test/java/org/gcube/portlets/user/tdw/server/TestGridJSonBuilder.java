/**
 * 
 */
package org.gcube.portlets.user.tdw.server;

import java.sql.Date;

import org.gcube.portlets.user.tdw.server.datasource.util.GridJSonBuilder;
import org.junit.Test;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class TestGridJSonBuilder {
	
	@Test
	public void testGridJSonBuilder()
	{
		GridJSonBuilder gridJSonWriter = new GridJSonBuilder();
		
		gridJSonWriter.startRows("data");
		
		gridJSonWriter.startRow();
		gridJSonWriter.addValue("boolean", false);
		gridJSonWriter.addValue("date", new Date(1));
		gridJSonWriter.addValue("double", 2.1);
		gridJSonWriter.addValue("float", 2.3f);
		gridJSonWriter.addValue("long", 123l);
		gridJSonWriter.addValue("string", "This is a string");
		gridJSonWriter.endRow();
		
		gridJSonWriter.endRows();
		
		gridJSonWriter.setOffset("offset", 12);
		gridJSonWriter.setTotalLength("total", 450);
		
		gridJSonWriter.close();
		
		System.out.println(gridJSonWriter.toString());
		
	}

}
