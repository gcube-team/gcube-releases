/**
 * 
 */
package org.gcube.portlets.user.tdw.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gcube.portlets.user.tdw.client.util.ColumnPositionComparator;
import org.gcube.portlets.user.tdw.shared.model.ColumnDefinition;
import org.junit.Test;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class TestSorting {

	/**
	 * @param args
	 */
	
	@Test
	public void testSorting() {
		List<ColumnDefinition> cols = new ArrayList<ColumnDefinition>();
		ColumnDefinition col = new ColumnDefinition("id1", "label1");
		col.setPosition(1);
		cols.add(col);
		col = new ColumnDefinition("id2", "label2");
		col.setPosition(2);
		cols.add(col);
		col = new ColumnDefinition("id3", "label3");
		col.setPosition(3);
		cols.add(col);
		col = new ColumnDefinition("id4", "label4");
		col.setPosition(4);
		cols.add(col);
		col = new ColumnDefinition("id5", "label5");
		col.setPosition(5);
		cols.add(col);
		
		col = new ColumnDefinition("id6-1", "label6-1");
		col.setPosition(6);
		cols.add(col);
		
		col = new ColumnDefinition("id6-2", "label6-2");
		col.setPosition(6);
		cols.add(col);
		col = new ColumnDefinition("id7", "label7");
		col.setPosition(7);
		cols.add(col);
		
		
		col = new ColumnDefinition("idNoPos1", "labelNoPos1");
		cols.add(col);
		
		col = new ColumnDefinition("idNoPos2", "labelNoPos2");
		cols.add(col);
		Collections.sort(cols, new ColumnPositionComparator(false));
		
		for (ColumnDefinition column:cols) System.out.println(column);

	}

}
