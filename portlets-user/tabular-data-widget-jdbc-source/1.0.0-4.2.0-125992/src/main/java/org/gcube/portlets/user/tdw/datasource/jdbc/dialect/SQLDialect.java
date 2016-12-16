/**
 * 
 */
package org.gcube.portlets.user.tdw.datasource.jdbc.dialect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.gcube.portlets.user.tdw.server.datasource.Direction;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface SQLDialect {
	
	
	public String getName();
	
	/**
	 * Checks if the specified database system is compatible with this dialect.
	 * @param databaseProductName
	 * @param databaseMajorVersion
	 * @param databaseMinorVersion
	 * @return
	 */
	public boolean supportDataBase(String databaseProductName, int databaseMajorVersion, int databaseMinorVersion);
	
	/**
	 * Returns a query for table size retrieving.
	 * @param tableName
	 * @return
	 */
	public String getTableSizeQuery(String tableName);
	
	public PreparedStatement createDataPreparedStatement(Connection connection, String tableName, String sortingColumn, Direction sortingDirection, int start, int limit) throws SQLException;
	
	public void setDataPreparedStatementParameters(PreparedStatement preparedStatement, String tableName, String sortingColumn, Direction sortingDirection, int start, int limit) throws SQLException;

}
