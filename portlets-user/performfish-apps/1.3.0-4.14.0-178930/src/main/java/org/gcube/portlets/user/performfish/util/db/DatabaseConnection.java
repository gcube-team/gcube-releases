package org.gcube.portlets.user.performfish.util.db;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.performfish.util.PFISHConstants;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;


public class DatabaseConnection {
	private static Log _log = LogFactoryUtil.getLog(DatabaseConnection.class);

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection(DB_Credentials dbparams) throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection =  DriverManager.getConnection(dbparams.getJDBCURL(), dbparams.getUser(), dbparams.getPwd());
            DBUtil.initializeTablesIfNotExist(connection);
        } catch (ClassNotFoundException ex) {
            _log.error("Database Connection Creation Failed : " + ex.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static DatabaseConnection getInstance(String context) throws Exception {
        if (instance == null || instance.getConnection().isClosed()) {
        	DB_Credentials cred = getDBCredentials(context);

            instance = new DatabaseConnection(cred);
            _log.info("Database Connection acquired for : " + cred.getJDBCURL());
        }
        return instance;
    }
    /**
	 * 
	 * @param request
	 * @param response
	 * @param instance
	 * @return the credentails, and if the db is empty created the schema
	 * @throws Exception 
	 */
	private static DB_Credentials getDBCredentials(String currContext) throws Exception {
		AccessPoint ac = getDBAccessPoint(currContext);
		DB_Credentials toReturn = new DB_Credentials();
		_log.debug("Got AccessPoint:" + ac.toString());
		String dbAddress = ac.address(); //"localhost:5432"; //
		toReturn.setDBURL(dbAddress); 
		_log.debug("DB address: "+ dbAddress);
		String dbName = ac.name();
		toReturn.setDBName(dbName); 
		_log.debug("DB name: "+ dbName);
		String dbUser = ac.username();
		toReturn.setUser(dbUser); 
		_log.debug("DB user: " + dbUser);
		String jdbcURL = new StringBuffer("jdbc:postgresql://").append(dbAddress).append("/").append(dbName).toString();
		_log.debug("jdbc.url: "+jdbcURL);
		ScopeProvider.instance.set(currContext);
		String pwd = StringEncrypter.getEncrypter().decrypt(ac.password());
		toReturn.setPwd(pwd);
		_log.debug("Decrypted Password OK");

		return toReturn;
	}
    /**
	 * Gets the survey DB access point.
	 *
	 * @return the survey DB access point
	 */
	private static AccessPoint getDBAccessPoint(String currContext) {
		//set the context for this resource
		ScopeProvider.instance.set(currContext);
		//construct the xquery
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name/text() eq '"+ PFISHConstants.PF_DB_SERVICE_ENDPOINT_NAME +"'");
		query.addCondition("$resource/Profile/Category/text() eq '"+ PFISHConstants.PF_DB_SERVICE_ENDPOINT_CATEGORY +"'");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> conf = client.submit(query);
		ServiceEndpoint res = conf.get(0);

		return res.profile().accessPoints().iterator().next();
	}
	
}
