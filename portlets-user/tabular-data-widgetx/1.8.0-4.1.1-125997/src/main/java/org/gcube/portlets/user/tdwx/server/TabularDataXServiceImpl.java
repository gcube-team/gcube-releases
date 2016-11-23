/**
 * 
 */
package org.gcube.portlets.user.tdwx.server;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.portlets.user.tdwx.client.rpc.TabularDataXService;
import org.gcube.portlets.user.tdwx.client.rpc.TabularDataXServiceException;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceX;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceXException;
import org.gcube.portlets.user.tdwx.server.util.SessionUtil;
import org.gcube.portlets.user.tdwx.shared.ColumnsReorderingConfig;
import org.gcube.portlets.user.tdwx.shared.model.TableDefinition;
import org.gcube.portlets.user.tdwx.shared.model.TableId;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TabularDataXServiceImpl extends RemoteServiceServlet implements TabularDataXService {

	private static final long serialVersionUID = 193560783723693864L;

	protected static Logger logger = LoggerFactory.getLogger(TabularDataXServiceImpl.class);
	
	protected DataSourceX getDataSource(int tdSessionId)
	{
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		return SessionUtil.getDataSource(httpSession, tdSessionId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TableDefinition openTable(int tdSessionId, TableId tableId) throws TabularDataXServiceException {
		logger.debug("openTable tdSessionId: "+tdSessionId+" tableId: "+tableId);

		try{
			closeCurrentTable(tdSessionId, true);
			
			HttpSession httpSession = this.getThreadLocalRequest().getSession();
			DataSourceX dataSource = SessionUtil.openDataSource(httpSession, tableId);
			SessionUtil.setDataSource(httpSession, tdSessionId, dataSource);
			logger.debug("Open table get table definition");
			return dataSource.getTableDefinition();
		}catch (Exception e) {
			logger.error("An error occurred opening the specified table "+tableId+" in session "+tdSessionId, e);
			throw new TabularDataXServiceException("An error occurred opening the specified table: "+e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TableDefinition getCurrentTableDefinition(int tdSessionId) throws TabularDataXServiceException {
		logger.debug("getCurrentTableDefinition tdSessionId: "+tdSessionId);

		try{
			DataSourceX dataSource = getDataSource(tdSessionId);
			logger.debug("Service get current table definition");
			return dataSource.getTableDefinition();
		}catch (Exception e) {
			logger.error("An error occurred retrieving the table definition", e);
			throw new TabularDataXServiceException("An error occurred retrieving the table definition: "+e.getMessage());
		}
	}

	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TableDefinition setCurrentTableColumnsReordering(int tdSessionId, ColumnsReorderingConfig columnReorderingConfig) throws TabularDataXServiceException {
		logger.debug("setCurrentTableColumnsReordering tdSessionId: "+tdSessionId);

		try{
			DataSourceX dataSource = getDataSource(tdSessionId);
			logger.debug("Service get current table definition");
			TableDefinition tableDefinition=dataSource.setColumnReordering(columnReorderingConfig);
			return tableDefinition;
		}catch (Exception e) {
			logger.error("An error occurred setting columns reordering", e);
			throw new TabularDataXServiceException("An error occurred setting columns reordering: "+e.getMessage());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TableDefinition getTableDefinition(TableId id) throws TabularDataXServiceException {
		logger.debug("getTableDefinition TableId: "+id);

		try{
			HttpSession httpSession = this.getThreadLocalRequest().getSession();
			DataSourceX dataSource = SessionUtil.openDataSource(httpSession,id);
			logger.debug("Service get table definition");
			return dataSource.getTableDefinition();
		}catch (Exception e) {
			logger.error("An error occurred getting the table definition", e);
			throw new TabularDataXServiceException("An error occurred getting the table definition: "+e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeTable(int tdSessionId) throws TabularDataXServiceException {
		try {
			closeCurrentTable(tdSessionId, false);
		} catch (DataSourceXException e) {
			logger.error("An error occurred closing the current table", e);
			throw new TabularDataXServiceException("An error occurred closing the current table: "+e.getMessage());
		}
	}
	
	
	protected void closeCurrentTable(int tdSessionId, boolean silent) throws DataSourceXException {
		try {
			HttpSession httpSession = this.getThreadLocalRequest().getSession();
			SessionUtil.closeDataSource(httpSession, tdSessionId);
		} catch (DataSourceXException e)
		{
			if (!silent) throw e;
		}
	}

}
