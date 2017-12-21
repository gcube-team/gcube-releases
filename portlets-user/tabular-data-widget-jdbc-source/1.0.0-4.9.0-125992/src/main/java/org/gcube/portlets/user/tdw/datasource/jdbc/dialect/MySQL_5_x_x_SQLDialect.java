/**
 * 
 */
package org.gcube.portlets.user.tdw.datasource.jdbc.dialect;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.gcube.portlets.user.tdw.server.datasource.Direction;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class MySQL_5_x_x_SQLDialect extends AbstractSQLDialect {
	

	@Override
	public String getName() {
		return "MySQL_5_x_x";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supportDataBase(String databaseProductName, int databaseMajorVersion, int databaseMinorVersion) {
		if (!"mysql".equalsIgnoreCase(databaseProductName)) return false;
		return databaseMajorVersion>=5;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTableSizeQuery(String tableName) {
		StringBuilder sql = new StringBuilder("SELECT count(*) FROM ");
		sql.append(tableName);
		return sql.toString();
	}
	
	/**
	 * {@inheritDoc}
	 * @throws SQLException 
	 */
	@Override
	public void setDataPreparedStatementParameters(PreparedStatement preparedStatement, String tableName, String sortingColumn, Direction sortingDirection, int start, int limit) throws SQLException {
		preparedStatement.setMaxRows(start+limit);
		preparedStatement.setInt(1, limit);	
		preparedStatement.setInt(2, start);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDataQuery(String tableName, String sortingColumn, Direction sortingDirection) {
		StringBuilder sql = new StringBuilder("SELECT * FROM ");
		sql.append(tableName);
		if (sortingColumn!=null && sortingDirection!=null) {
			sql.append(" ORDER BY ");
			sql.append(sortingColumn);
			sql.append(' ');
			sql.append(sortingDirection.toString());
		}
		sql.append(" LIMIT ? OFFSET ?");
		return sql.toString();
	}


}
