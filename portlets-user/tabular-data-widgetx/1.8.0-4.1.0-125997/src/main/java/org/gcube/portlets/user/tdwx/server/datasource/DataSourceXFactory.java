package org.gcube.portlets.user.tdwx.server.datasource;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portlets.user.tdwx.shared.model.TableId;


 
/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 * 
 * Generator of {@link DataSourceX}
 *
 */
public interface DataSourceXFactory {
	
	/**
	 * The DataSourceFactory unique id.
	 * @return
	 */
	public String getId();
	
	/**
	 * Open the specified datasource {@link DataSourceX}.
	 * @param id the datasource id.
	 * @return the retrieved datasource.
	 * @throws DataSourceXException if an error occurs retrieving the datasource.
	 */
	public DataSourceX openDataSource(ASLSession session, TableId id) throws DataSourceXException;
	
	/**
	 * Close the specified DataSource releasing allocated resources.
	 * @param session
	 * @param dataSource
	 * @throws DataSourceXException
	 */
	public void closeDataSource(ASLSession session, DataSourceX dataSource) throws DataSourceXException;

}
