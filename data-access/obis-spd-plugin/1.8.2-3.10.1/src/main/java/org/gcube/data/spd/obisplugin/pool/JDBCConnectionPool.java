/**
 * 
 */
package org.gcube.data.spd.obisplugin.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Adapted from http://sourcemaking.com/design_patterns/object_pool/java
 */
public class JDBCConnectionPool extends ObjectPool<Connection> {

	protected String url, username, password;

	public JDBCConnectionPool(String driver, String dsn, String usr, String pwd) {
		super("JDBCConnectionPool", 10 * 60 * 1000);
		try {
			Class.forName(driver).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.url = dsn;
		this.username = usr;
		this.password = pwd;
	}

	@Override
	protected Connection create() {
		try {
			Connection connection = DriverManager.getConnection(url, username, password);
			connection.setAutoCommit(false);
			connection.setReadOnly(true);
			return connection;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void expire(Connection o) {
		try {
			o.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected boolean validate(Connection o) {
		try {
			return !o.isClosed() && o.isValid(1000);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}