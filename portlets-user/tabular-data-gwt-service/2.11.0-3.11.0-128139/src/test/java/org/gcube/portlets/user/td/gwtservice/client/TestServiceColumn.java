package org.gcube.portlets.user.td.gwtservice.client;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.junit.Test;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TestServiceColumn {
	private final static long trId = 225;
	private final String columnLocalId="a5aedc5b-b843-451b-9659-afd672656276";
	
	
	@Test
	public void TestTROperation() {

		ScopeProvider.instance.set(Constants.DEFAULT_SCOPE);

		AuthorizationProvider.instance.set(new AuthorizationToken(
				Constants.DEFAULT_USER));
		TabularDataService service = TabularDataServiceFactory.getService();
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

		System.out.println(lastTable.toString());
		
		ColumnLocalId clId=new ColumnLocalId(columnLocalId);
		Column column=lastTable.getColumnById(clId);
		System.out.println(column.toString());
		
		
	}

}
