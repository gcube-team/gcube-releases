/**
 * 
 */
package org.gcube.portlets.user.tdw.datasource.jdbc.dialect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.gcube.portlets.user.tdw.server.datasource.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public abstract class AbstractSQLDialect implements SQLDialect {
	
	protected Logger logger = LoggerFactory.getLogger(AbstractSQLDialect.class);

	/**
	 * {@inheritDoc}
	 * @throws SQLException 
	 */
	@Override
	public void setDataPreparedStatementParameters(PreparedStatement preparedStatement, String tableName, String sortingColumn, Direction sortingDirection, int start, int limit) throws SQLException {
		preparedStatement.setMaxRows(start+limit);
		preparedStatement.setInt(1, start);
		preparedStatement.setInt(2, limit);		
	}

	/**
	 * {@inheritDoc}
	 * @throws SQLException 
	 */
	@Override
	public PreparedStatement createDataPreparedStatement(Connection connection, String tableName, String sortingColumn, Direction sortingDirection, int start, int limit) throws SQLException {
		String query = getDataQuery(tableName, sortingColumn, sortingDirection);
		logger.trace("query: "+query);
		return connection.prepareStatement(query);
	}

	/**
	 * Returns a query for table data selection.
	 * @param tableName
	 * @param sortingColumn the sorting column, can be null.
	 * @param sortingDirection the sorting direction, can be null.
	 * @return
	 */
	public abstract String getDataQuery(String tableName, String sortingColumn, Direction sortingDirection);

}
