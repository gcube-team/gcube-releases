/**
 * 
 */
package org.gcube.portlets.user.tdw.datasource.jdbc.dialect;

import org.gcube.portlets.user.tdw.server.datasource.Direction;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class GenericSQLDialect extends AbstractSQLDialect {

	@Override
	public String getName() {
		return "GenericSQLDialect";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supportDataBase(String databaseProductName, int databaseMajorVersion, int databaseMinorVersion) {
		return true;
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
