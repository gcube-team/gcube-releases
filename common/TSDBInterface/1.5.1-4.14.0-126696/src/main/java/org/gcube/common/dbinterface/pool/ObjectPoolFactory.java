package org.gcube.common.dbinterface.pool;

import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectPoolFactory extends BasePoolableObjectFactory<DBSession> {

	private static final Logger logger = LoggerFactory.getLogger(ObjectPoolFactory.class);

	private String dsn, usr, pwd;
		
	protected ObjectPoolFactory(String driver, String dsn, String usr, String pwd) {
		super();
		try {
			Class.forName(driver).newInstance();
		} catch (Exception e) {
			logger.error("error getting db drivers",e);
		}
		this.dsn = dsn;
		this.usr = usr;
		this.pwd = pwd;
	}

	@Override
	public DBSession makeObject() throws Exception {
		try {
			return new DBSession(DriverManager.getConnection(dsn, usr, pwd));
		} catch (SQLException e) {
			logger.error("",e);
			return (null);
		}
	}
	
	public void passivateObject(DBSession dbSession) { 
        dbSession.release(); 
    }

	/* (non-Javadoc)
	 * @see org.apache.commons.pool.BasePoolableObjectFactory#destroyObject(java.lang.Object)
	 */
	@Override
	public void destroyObject(DBSession obj) throws Exception {
		obj.closeConnection();
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.pool.BasePoolableObjectFactory#validateObject(java.lang.Object)
	 */
	@Override
	public boolean validateObject(DBSession obj) {
		try {
			return !obj.isConnectionClosed();
		} catch (Exception e) {
			return false;
		}
	} 
	
	
	
}
