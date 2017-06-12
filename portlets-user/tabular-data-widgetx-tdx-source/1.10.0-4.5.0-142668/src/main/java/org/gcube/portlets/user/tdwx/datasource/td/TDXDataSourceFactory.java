/**
 * 
 */
package org.gcube.portlets.user.tdwx.datasource.td;


import org.gcube.portlets.user.tdwx.server.datasource.DataSourceX;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceXException;
import org.gcube.portlets.user.tdwx.server.datasource.DataSourceXFactory;
import org.gcube.portlets.user.tdwx.server.util.ServiceCredentials;
import org.gcube.portlets.user.tdwx.shared.model.TableId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


 
/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TDXDataSourceFactory implements DataSourceXFactory {
	
	private Logger logger = LoggerFactory.getLogger(TDXDataSourceFactory.class);
	private static final String ID=Constants.TDX_DATASOURCE_FACTORY_ID;
	
	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	public DataSourceX openDataSource(ServiceCredentials serviceCredentials, TableId tableId) throws DataSourceXException {
		logger.debug("openDataSource: "+tableId);
		String table = tableId.getTableKey();
		TDXDataSource dataSource = new TDXDataSource(ID,serviceCredentials,table);
		return dataSource;
	}

	/**
	 * {@inheritDoc}
	 */
	public void closeDataSource(ServiceCredentials serviceCredentials, DataSourceX dataSource) throws DataSourceXException {
		logger.debug("closeDataSource: "+dataSource);
		
		((TDXDataSource)dataSource).close();		
	}

}
