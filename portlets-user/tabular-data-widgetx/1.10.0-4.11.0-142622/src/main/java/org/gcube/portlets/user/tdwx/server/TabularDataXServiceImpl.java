/**
 * 
 */
package org.gcube.portlets.user.tdwx.server;

import javax.servlet.http.HttpServletRequest;

import org.gcube.portlets.user.tdwx.client.rpc.TabularDataXService;
import org.gcube.portlets.user.tdwx.client.rpc.TabularDataXServiceException;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceX;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceXException;
import org.gcube.portlets.user.tdwx.server.util.ServiceCredentials;
import org.gcube.portlets.user.tdwx.shared.ColumnsReorderingConfig;
import org.gcube.portlets.user.tdwx.shared.model.TableDefinition;
import org.gcube.portlets.user.tdwx.shared.model.TableId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TabularDataXServiceImpl extends RemoteServiceServlet implements
		TabularDataXService {

	private static final long serialVersionUID = 193560783723693864L;

	private static Logger logger = LoggerFactory
			.getLogger(TabularDataXServiceImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TableDefinition openTable(int tdSessionId, TableId tableId)
			throws TabularDataXServiceException {
		logger.debug("openTable tdSessionId: " + tdSessionId + " tableId: "
				+ tableId);

		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(httpRequest);
			closeCurrentTable(httpRequest, serviceCredentials, tdSessionId,
					true);
			DataSourceX dataSource = SessionUtil.openDataSource(httpRequest,
					serviceCredentials, tableId);
			SessionUtil.setDataSource(httpRequest, serviceCredentials,
					tdSessionId, dataSource);
			logger.debug("Open table get table definition");
			return dataSource.getTableDefinition();
		} catch (Exception e) {
			logger.error("An error occurred opening the specified table "
					+ tableId + " in session " + tdSessionId, e);
			throw new TabularDataXServiceException(
					"An error occurred opening the specified table: "
							+ e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TableDefinition getCurrentTableDefinition(int tdSessionId)
			throws TabularDataXServiceException {
		logger.debug("getCurrentTableDefinition tdSessionId: " + tdSessionId);

		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(httpRequest);
			DataSourceX dataSource = SessionUtil.getDataSource(httpRequest,
					serviceCredentials, tdSessionId);
			logger.debug("Service get current table definition");
			return dataSource.getTableDefinition();
		} catch (Exception e) {
			logger.error("An error occurred retrieving the table definition", e);
			throw new TabularDataXServiceException(
					"An error occurred retrieving the table definition: "
							+ e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TableDefinition setCurrentTableColumnsReordering(int tdSessionId,
			ColumnsReorderingConfig columnReorderingConfig)
			throws TabularDataXServiceException {
		logger.debug("setCurrentTableColumnsReordering tdSessionId: "
				+ tdSessionId);

		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(httpRequest);
			DataSourceX dataSource = SessionUtil.getDataSource(httpRequest,
					serviceCredentials, tdSessionId);
			logger.debug("Service get current table definition");
			TableDefinition tableDefinition = dataSource
					.setColumnReordering(columnReorderingConfig);
			return tableDefinition;
		} catch (Exception e) {
			logger.error("An error occurred setting columns reordering", e);
			throw new TabularDataXServiceException(
					"An error occurred setting columns reordering: "
							+ e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TableDefinition getTableDefinition(TableId id)
			throws TabularDataXServiceException {
		logger.debug("getTableDefinition TableId: " + id);

		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(httpRequest);
			DataSourceX dataSource = SessionUtil.openDataSource(httpRequest,
					serviceCredentials, id);
			logger.debug("Service get table definition");
			return dataSource.getTableDefinition();
		} catch (Exception e) {
			logger.error("An error occurred getting the table definition", e);
			throw new TabularDataXServiceException(
					"An error occurred getting the table definition: "
							+ e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeTable(int tdSessionId) throws TabularDataXServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(httpRequest);
			closeCurrentTable(httpRequest, serviceCredentials, tdSessionId,
					false);
		} catch (Exception e) {
			logger.error("An error occurred closing the current table", e);
			throw new TabularDataXServiceException(
					"An error occurred closing the current table: "
							+ e.getMessage());
		}
	}

	protected void closeCurrentTable(HttpServletRequest httpRequest,
			ServiceCredentials serviceCredentials, int tdSessionId,
			boolean silent) throws DataSourceXException {
		try {
			SessionUtil.closeDataSource(httpRequest, serviceCredentials,
					tdSessionId);
		} catch (DataSourceXException e) {
			if (!silent)
				throw e;
		}
	}

}
