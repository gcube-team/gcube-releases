package org.gcube.portlets.user.td.gwtservice.client;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
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
public class TestServiceTable {
	private final static long trId = 157;
	private final static long searchTableId=1951;
	private String user=Constants.DEFAULT_USER;
	private String scope=Constants.DEFAULT_SCOPE;
	
	@Test
	public void TestLastTable() {
		
		ScopeProvider.instance.set(scope);

		AuthorizationProvider.instance.set(new AuthorizationToken(
				user));
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

		System.out.println("LastTable[TR id:"+trId+"] :"+lastTable.toString());
	}
	
	
	@Test
	public void TestTable() {
		
		ScopeProvider.instance.set(scope);

		AuthorizationProvider.instance.set(new AuthorizationToken(
				user));
		TabularDataService service = TabularDataServiceFactory.getService();
		

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
