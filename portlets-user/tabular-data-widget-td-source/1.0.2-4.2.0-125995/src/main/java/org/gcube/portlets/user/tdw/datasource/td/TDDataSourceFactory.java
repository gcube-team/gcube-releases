/**
 * 
 */
package org.gcube.portlets.user.tdw.datasource.td;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portlets.user.tdw.server.datasource.DataSource;
import org.gcube.portlets.user.tdw.server.datasource.DataSourceException;
import org.gcube.portlets.user.tdw.server.datasource.DataSourceFactory;
import org.gcube.portlets.user.tdw.shared.model.TableId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


 
/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TDDataSourceFactory implements DataSourceFactory {
	
	protected Logger logger = LoggerFactory.getLogger(TDDataSourceFactory.class);
	private static final String ID="TDDataSourceFactory";
	
	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	public DataSource openDataSource(ASLSession aslSession, TableId tableId) throws DataSourceException {
		String table = tableId.getTableKey();
		TDDataSource dataSource = new TDDataSource(ID,aslSession,table);
		return dataSource;
	}

	/**
	 * {@inheritDoc}
	 */
	public void closeDataSource(ASLSession session, DataSource dataSource) throws DataSourceException {
		((TDDataSource)dataSource).close();		
	}

}
