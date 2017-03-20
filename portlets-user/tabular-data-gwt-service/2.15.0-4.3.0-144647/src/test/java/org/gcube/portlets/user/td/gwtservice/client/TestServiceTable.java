package org.gcube.portlets.user.td.gwtservice.client;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.junit.Test;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TestServiceTable {
	private final static long trId = 157;
	private final static long searchTableId=1951;
	
	
	@Test
	public void TestLastTable() {
		TDService tdService=new TDService();
		TabularDataService service = tdService.getService();
		TabularResourceId tabularResourceId = new TabularResourceId(trId);
		

		Table lastTable;
		try {
			lastTable = service.getLastTable(tabularResourceId);
		} catch (NoSuchTabularResourceException e) {
			System.out.println("No such tabular resource with id: " + trId);
			e.printStackTrace();
			return;
		} catch (NoSuchTableException e) {
			System.out.println("No such last table for: " + trId);
			e.printStackTrace();
			return;
		}

		System.out.println("LastTable[TR id:"+trId+"] :"+lastTable.toString());
	}
	
	
	@Test
	public void TestTable() {
		
		TDService tdService=new TDService();
		TabularDataService service = tdService.getService();
		

		Table searchTable;
		try {
			searchTable = service.getTable(new TableId(searchTableId));
		} catch (NoSuchTableException e) {
			System.out.println("No such last table for: " + trId);
			e.printStackTrace();
			return;
		}

		System.out.println("SearchTable["+searchTableId+"]: "+searchTable.toString());
	}

}
