/**
 * 
 */
package org.gcube.portlets.user.tdw.server;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.portlets.user.tdw.client.rpc.TabularDataService;
import org.gcube.portlets.user.tdw.client.rpc.TabularDataServiceException;
import org.gcube.portlets.user.tdw.server.datasource.DataSource;
import org.gcube.portlets.user.tdw.server.datasource.DataSourceException;
import org.gcube.portlets.user.tdw.server.util.SessionUtil;
import org.gcube.portlets.user.tdw.shared.model.TableDefinition;
import org.gcube.portlets.user.tdw.shared.model.TableId;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class TabularDataServiceImpl extends RemoteServiceServlet implements TabularDataService {

	private static final long serialVersionUID = 193560783723693864L;

	protected static Logger logger = LoggerFactory.getLogger(TabularDataServiceImpl.class);
	
	protected DataSource getDataSource(int tdSessionId)
	{
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		return SessionUtil.getDataSource(httpSession, tdSessionId);
	}

	/**
	 * {@inheritDoc}
	 */
	public TableDefinition openTable(int tdSessionId, TableId tableId) throws TabularDataServiceException {
		logger.debug("openTable tdSessionId: "+tdSessionId+" tableId: "+tableId);

		try{
			closeCurrentTable(tdSessionId, true);
			
			HttpSession httpSession = this.getThreadLocalRequest().getSession();
			DataSource dataSource = SessionUtil.openDataSource(httpSession, tableId);
			SessionUtil.setDataSource(httpSession, tdSessionId, dataSource);
			return dataSource.getTableDefinition();
		}catch (Exception e) {
			logger.error("An error occurred opening the specified table "+tableId+" in session "+tdSessionId, e);
			throw new TabularDataServiceException("An error occurred opening the specified table: "+e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public TableDefinition getCurrentTableDefinition(int tdSessionId) throws TabularDataServiceException {
		logger.debug("getCurrentTableDefinition tdSessionId: "+tdSessionId);

		try{
			DataSource dataSource = getDataSource(tdSessionId);
			return dataSource.getTableDefinition();
		}catch (Exception e) {
			logger.error("An error occurred retrieving the table definition", e);
			throw new TabularDataServiceException("An error occurred retrieving the table definition: "+e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public TableDefinition getTableDefinition(TableId id) throws TabularDataServiceException {
		logger.debug("getTableDefinition TableId: "+id);

		try{
			HttpSession httpSession = this.getThreadLocalRequest().getSession();
			DataSource dataSource = SessionUtil.openDataSource(httpSession,id);
			return dataSource.getTableDefinition();
		}catch (Exception e) {
			logger.error("An error occurred getting the table definition", e);
			throw new TabularDataServiceException("An error occurred getting the table definition: "+e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void closeTable(int tdSessionId) throws TabularDataServiceException {
		try {
			closeCurrentTable(tdSessionId, false);
		} catch (DataSourceException e) {
			logger.error("An error occurred closing the current table", e);
			throw new TabularDataServiceException("An error occurred closing the current table: "+e.getMessage());
		}
	}
	

	protected void closeCurrentTable(int tdSessionId, boolean silent) throws DataSourceException {
		try {
			HttpSession httpSession = this.getThreadLocalRequest().getSession();
			SessionUtil.closeDataSource(httpSession, tdSessionId);
		} catch (DataSourceException e)
		{
			if (!silent) throw e;
		}
	}

}
