/**
 * 
 */
package org.gcube.portlets.user.tdw.datasource.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


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
public class SimpleJDBCDataSourceFactory implements DataSourceFactory {
	
	protected Logger logger = LoggerFactory.getLogger(SimpleJDBCDataSourceFactory.class);
	
	protected Map<String, JDBCCredential> credentials = new HashMap<String, JDBCCredential>();
	
	public String registerTable(String id, String jdbcdUrl, String tableName)
	{
		credentials.put(id, new JDBCCredential(id, jdbcdUrl, tableName));
		return id;
	}
	
	public String registerTable(String jdbcdUrl, String tableName)
	{
		String id = UUID.randomUUID().toString();
		credentials.put(id, new JDBCCredential(id, jdbcdUrl, tableName));
		return id;
	}
	
	public Collection<JDBCCredential> getCredentials()
	{
		return credentials.values();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return "SimpleJDBCDataSourceFactory";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataSource openDataSource(ASLSession session, TableId tableId) throws DataSourceException {
		String id = tableId.getTableKey();
		JDBCCredential credential = credentials.get(id);
		logger.trace("credential: "+credential);
		if (credential==null) {
			logger.error("No credential found for datasource id \""+id+"\"");
			throw new DataSourceException("No credential found for datasource id \""+id+"\"");
		}
		
		JDBCDataSource dataSource = JDBCDataSource.createJDBCDataSource(getId(), credential.getUrl(), credential.getTableName());//, new PostgresSQL_8_x_x_SQLDialect(true));
		return dataSource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeDataSource(ASLSession session, DataSource dataSource) throws DataSourceException {
		((JDBCDataSource)dataSource).close();		
	}

}
