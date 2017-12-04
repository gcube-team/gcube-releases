/**
 * 
 */
package org.gcube.portlets.user.tdw.server.datasource;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portlets.user.tdw.shared.model.TableId;

/**
 * Generator of {@link DataSource}
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 */
public interface DataSourceFactory {
	
	/**
	 * The DataSourceFactory unique id.
	 * @return
	 */
	public String getId();
	
	/**
	 * Open the specified datasource {@link DataSource}.
	 * @param id the datasource id.
	 * @return the retrieved datasource.
	 * @throws DataSourceException if an error occurs retrieving the datasource.
	 */
	public DataSource openDataSource(ASLSession session, TableId id) throws DataSourceException;
	
	/**
	 * Close the specified DataSource releasing allocated resources.
	 * @param session
	 * @param dataSource
	 * @throws DataSourceException
	 */
	public void closeDataSource(ASLSession session, DataSource dataSource) throws DataSourceException;

}
