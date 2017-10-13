/**
 * 
 */
package org.gcube.portlets.user.tdw.datasource.jdbc.dialect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class SQLDialectManager {
	
	protected static Logger logger = LoggerFactory.getLogger(SQLDialectManager.class);
	
	protected static GenericSQLDialect generic = new GenericSQLDialect();
	
	protected static SQLDialect[] dialects = new SQLDialect[]{
		new Derby_10_7_SQLDialect(),
		new PostgresSQL_8_x_x_SQLDialect(),
		new MySQL_5_x_x_SQLDialect()
	};
	
	public static SQLDialect getDialect(String databaseProductName, int databaseMajorVersion, int databaseMinorVersion) throws Exception
	{
		logger.trace("getting dialect for databaseProductName: "+databaseProductName+" databaseMajorVersion: "+databaseMajorVersion+" databaseMinorVersion: "+databaseMinorVersion);
		for (SQLDialect dialect:dialects) if (dialect.supportDataBase(databaseProductName, databaseMajorVersion, databaseMinorVersion)) {
			
			logger.trace("selected dialect: "+dialect.getName());
			return dialect;
		}
		logger.warn("No SQL dialect found for databaseProductName: "+databaseProductName+ " databaseMajorVersion: "+ databaseMajorVersion + " databaseMinorVersion: "+databaseMinorVersion);
		return generic;
	}
	
}
