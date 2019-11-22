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
public class Derby_10_7_SQLDialect extends AbstractSQLDialect {
	

	@Override
	public String getName() {
		return "Derby_10_7";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supportDataBase(String databaseProductName, int databaseMajorVersion, int databaseMinorVersion) {
		if ("derby".equalsIgnoreCase(databaseProductName)) return false;
		if (databaseMajorVersion==10 && databaseMinorVersion<7) return false;
		return databaseMajorVersion>=10;
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
			sql.append(" ORDER BY \"");
			sql.append(sortingColumn);
			sql.append("\" ");
			sql.append(sortingDirection.toString());
		}
		sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
		return sql.toString();
	}

}
