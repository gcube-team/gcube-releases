/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.server;

import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 21, 2014
 *
 */
public class TdTabularDataService {
	
	private TabularDataService service;
	private String scope;
	private String username;
	
	public static Logger logger = LoggerFactory.getLogger(TdTabularDataService.class);

	public TdTabularDataService(String scope, String username){
		this.scope = scope;
		this.username = username;
		logger.info("Getting TabularDataServiceFactory...");
		service = TabularDataServiceFactory.getService();
	}
	

	/*public TabularDataService getService() {
		return service;
	}*/
	
	public List<org.gcube.data.analysis.tabulardata.service.tabular.TabularResource> getTabularResources() throws Exception{
		try{
			logger.info("Getting Tabular Resources...");
			ScopeProvider.instance.set(scope);
			AuthorizationProvider.instance.set(new AuthorizationToken(username,scope));
			return service.getTabularResources();
		}catch (Exception e) {
			throw new Exception("Sorry, an error occurred on getting Tabular Resources: ",e);
		}
	}
	
	public Table getTable(TableId tableId) throws NoSuchTableException{
		logger.info("Getting Table...");
		ScopeProvider.instance.set(scope);
		AuthorizationProvider.instance.set(new AuthorizationToken(username,scope));
		return service.getTable(tableId);
	}
}
