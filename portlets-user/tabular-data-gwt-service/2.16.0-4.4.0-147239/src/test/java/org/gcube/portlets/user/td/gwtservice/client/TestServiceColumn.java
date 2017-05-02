package org.gcube.portlets.user.td.gwtservice.client;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TestServiceColumn {
	private static Logger logger = LoggerFactory.getLogger(TestServiceColumn.class);

	private final static long trId = 225;
	private final String columnLocalId="a5aedc5b-b843-451b-9659-afd672656276";
	
	
	@Test
	public void TestTROperation() {
		TDService tdService=new TDService();
		TabularDataService service = tdService.getService();
		TabularResourceId tabularResourceId = new TabularResourceId(trId);
		

		Table lastTable;
		try {
			lastTable = service.getLastTable(tabularResourceId);
		} catch (NoSuchTabularResourceException e) {
			logger.debug("No such tabular resource with id: " + trId);
			e.printStackTrace();
			return;
		} catch (NoSuchTableException e) {
			logger.debug("No such last table for: " + trId);
			e.printStackTrace();
			return;
		}

		logger.debug(lastTable.toString());
		
		ColumnLocalId clId=new ColumnLocalId(columnLocalId);
		Column column=lastTable.getColumnById(clId);
		logger.debug(column.toString());
		
		
	}

}
