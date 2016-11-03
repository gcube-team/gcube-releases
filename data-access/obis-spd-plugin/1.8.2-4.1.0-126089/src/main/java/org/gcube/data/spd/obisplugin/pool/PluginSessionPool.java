/**
 * 
 */
package org.gcube.data.spd.obisplugin.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.gcube.data.spd.obisplugin.PluginSession;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class PluginSessionPool extends ObjectPool<PluginSession> {

	protected Logger logger = Logger.getLogger(PluginSessionPool.class);
	protected DatabaseCredential databaseCredential;

	public PluginSessionPool(DatabaseCredential databaseCredential) {
		super("PluginSessionPool", 30*60*1000);
		setDatabaseCredential(databaseCredential);
	}

	/**
	 * @param databaseCredential the databaseCredential to set
	 */
	public void setDatabaseCredential(DatabaseCredential databaseCredential) {
		this.databaseCredential = databaseCredential;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PluginSession create() {

		try {
			Connection connection = createConnection();
			return new PluginSession(databaseCredential, connection);
		} catch (SQLException e) {
			logger.error("An error occurred creating the db connection", e);
			return null;
		}
	}
	
	protected Connection createConnection() throws SQLException
	{
		Properties props = new Properties();
		props.setProperty("user", databaseCredential.getUser());
		props.setProperty("password", databaseCredential.getPassword());
		return DriverManager.getConnection(databaseCredential.getUrl(), props);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean validate(PluginSession session) {
		try {
			return session.isValid(databaseCredential);
		} catch (Exception e) {
			logger.warn("An error occurred validating the session", e);
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void expire(PluginSession session) {
		
		try {
			session.expire();
		} catch (SQLException e) {
			logger.warn("An error occurred expiring the session", e);
		}
	}
}
