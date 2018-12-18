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
public class PostgresSQL_8_x_x_SQLDialect extends AbstractSQLDialect {

	protected boolean useSizeEstimation = false;

	public PostgresSQL_8_x_x_SQLDialect(){}

	/**
	 * @param useSizeEstimation
	 */
	public PostgresSQL_8_x_x_SQLDialect(boolean useSizeEstimation) {
		this.useSizeEstimation = useSizeEstimation;
	}

	@Override
	public String getName() {
		return "PostgresSQL_8_x_x";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supportDataBase(String databaseProductName, int databaseMajorVersion, int databaseMinorVersion) {
		if (!"postgresql".equalsIgnoreCase(databaseProductName)) return false;
		if (databaseMajorVersion==8 && databaseMinorVersion<4) return false;
		return databaseMajorVersion>=8;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTableSizeQuery(String tableName) {

		if (!useSizeEstimation) {
			StringBuilder sql = new StringBuilder("SELECT count(*) FROM ");
			sql.append(tableName);
			return sql.toString();
		} else {
			//SELECT reltuples FROM pg_class WHERE relname = 'tbl';
			StringBuilder sql = new StringBuilder("SELECT reltuples FROM pg_class WHERE relname = '");
			sql.append(tableName);
			sql.append("'");
			return sql.toString();
		}
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
		sql.append(" OFFSET ? LIMIT ?");
		return sql.toString();
	}


}
