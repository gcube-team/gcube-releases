package org.gcube.dataaccess.databases.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Class that allows to manage a database selected from a user. It performs to
 * set the database configuration, to connect to the database and finally to
 * execute a query.
 */
public class ConnectionManager {

	// private org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory df;

	// Constructor
	public ConnectionManager() {

		// org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory df = new
		// org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory();
		// df = new org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory();

	}

	public static SessionFactory initDBSession(AlgorithmConfiguration config) {
		SessionFactory dbHibConnection = null;
		try {
			if ((config != null) && (config.getConfigPath() != null)) {
				String defaultDatabaseFile = config.getConfigPath()
						+ "DestinationDBHibernateDBManager.cfg.xml";

				config.setDatabaseDriver(config.getParam("DatabaseDriver"));
				config.setDatabaseUserName(config.getParam("DatabaseUserName"));
				config.setDatabasePassword(config.getParam("DatabasePassword"));
				config.setDatabaseURL(config.getParam("DatabaseURL"));

				dbHibConnection = org.gcube.dataaccess.databases.utils.DatabaseFactory
						.initDBConnection(defaultDatabaseFile, config);
			}
		} catch (Exception e) {
//			System.out.println("ERROR IN DB INITIALIZATION : "
//					+ e.getLocalizedMessage());
			AnalysisLogger.getLogger().debug(
					"In ConnectionManager->  ERROR IN DB INITIALIZATION : " + e.getLocalizedMessage());
			// e.printStackTrace();
			// AnalysisLogger.getLogger().trace(e);
		}
		return dbHibConnection;
	}

	// create the database's connection without using the configuration file but
	// using the data input
	public SessionFactory initDBConnection(AlgorithmConfiguration config) {

		SessionFactory dbconnection = initDBSession(config);

		return dbconnection;

	}

	// create the database's connection using the configuration file
	// Note that this method is not called actually.
	public SessionFactory initDBConnection(String configurationFile)
			throws Exception {
		String xml = FileTools.readXMLDoc(configurationFile);
		SessionFactory DBSessionFactory = null;
		Configuration cfg = new Configuration();
		cfg = cfg.configure(DocumentBuilderFactory.newInstance()
				.newDocumentBuilder()
				.parse(new ByteArrayInputStream(xml.getBytes())));
		DBSessionFactory = cfg.buildSessionFactory();
		return DBSessionFactory;
	}

	/** Method that allows to set the configuration */
	public AlgorithmConfiguration setconfiguration(String ConfigPath,
			String DatabaseUserName, String DatabasePassword,
			String DatabaseDriver, String DatabaseDialect, String DatabaseURL,
			String DatabaseName) throws IOException {

		AlgorithmConfiguration config = new AlgorithmConfiguration();

		if (DatabaseName.equals("")) {

			throw new MalformedURLException(
					"Invalid Url: the database's name is not present");
			// return null;
		}

		if (!ConfigPath.equals(""))
			config.setConfigPath(ConfigPath);

		if (!DatabaseUserName.equals("")) {
			config.setParam("DatabaseUserName", DatabaseUserName);
		}

		if (!DatabasePassword.equals(""))
			config.setParam("DatabasePassword", DatabasePassword);

		if (!DatabaseDriver.equals(""))
			config.setParam("DatabaseDriver", DatabaseDriver);

		if (!DatabaseDialect.equals(""))
			config.setParam("DatabaseDialect", DatabaseDialect);

		if (!DatabaseURL.equals(""))
			config.setParam("DatabaseURL", DatabaseURL);

		return config;

	}

	/** Method that execute a query */
	public List<Object> executeQueryJDBC(String query, Connection conn)
			throws Exception {

		List<Object> result = null;
		
		
		// Get a statement from the connection
		Statement stmt = conn.createStatement();

		AnalysisLogger.getLogger().debug(
				"In ConnectionManager-> executing query: " + query);
		
		// Execute the query
		ResultSet rs = stmt.executeQuery(query);
        
		//get columns
		List<Object> rows = new ArrayList<Object>();
		while (rs.next()) {
			LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
			int colNum = rs.getMetaData().getColumnCount();

			for (int i = 1; i < colNum + 1; i++) {
				String columnName = rs.getMetaData().getColumnLabel(i);
				// System.out.println("column Name: "+columnName);
				String columnType = rs.getMetaData().getColumnTypeName(i);
				Object columnValue = rs.getObject(i);
				//to manage the data geometry
				 if(columnType.equals("geometry")){
					 columnValue = rs.getString(i);
					 //truncate value to 255 characters if it exceeds 255 characters
//					 if((rs.getString(i).length())>255){
//						 columnValue =(Object) (rs.getString(i).substring(0, 254));
////						 System.out.println("elem geometry truncated");
//					 }
				 }
				// System.out.println("value: "+columnValue);
				int j = 1;
				String newcolumnName = columnName;
                //rename a column if the same name is already present
				while (map.get(newcolumnName) != null) {
					newcolumnName = columnName + "_" + j;
					j++;
				}
				map.put(newcolumnName, columnValue);
			}
			rows.add(map);
		}

		result = rows;
		
		if (result == null || result.size() == 0) {
			AnalysisLogger.getLogger().debug(
					"ConnectionManager->Error: Result not available");
			throw new Exception("Result not available");
		} 
		

		stmt.close();
		
		AnalysisLogger.getLogger().debug(
				"In ConnectionManager-> result's size: " + result.size());
		// conn.close(); 
		return result;
	}

	/** Method that execute a query */
	public List<Object> executeQuery(String query,
			SessionFactory DBSessionFactory) throws Exception {

		List<Object> obj = null;
		Session ss = null;

		try {
			ss = DBSessionFactory.getCurrentSession();

			// print check added to measure the timeout
			// AnalysisLogger.getLogger().debug(
			// "In ConnectionManager-> beginTransaction: ");

			ss.beginTransaction();

			Query qr = null;

			// statement to check if the query is a "show create table"
			String keyword = "show create table";

			String regex = ".*\\b" + keyword.replaceAll(" +", "[ ]\\+")
					+ "\\b.*";

			if ((!(query.toLowerCase().contains("explain")))
					&& (!(query.toLowerCase().matches(regex)))) { // it does not
				// work if the
				// query
				// performs an
				// explain
				// operation

				// Wrapper for a query. It allows the query to operate in a
				// proper
				// way

				// query check in order to remove the character ";" if the query
				// contains it

				query = query.trim();

				if (query.endsWith(";")) {

					int endIndex = query.indexOf(";");

					query = query.substring(0, endIndex);

				}

				query = "select * from (" + query + ") as query";

			}

			AnalysisLogger.getLogger().debug(
					"In ConnectionManager-> executing query: " + query);

			qr = ss.createSQLQuery(query);

			qr.setResultTransformer(AliasToEntityOrderedMapResultTransformer.INSTANCE);

			// @SuppressWarnings("unchecked")
			List<Object> result = qr.list();

			AnalysisLogger.getLogger().debug(
					"In ConnectionManager-> result's size: " + result.size());

			ss.getTransaction().commit();

			/*
			 * if (result == null) System.out.println(
			 * "Hibernate doesn't return a valid object when org.gcube.contentmanagement.lexicalmatcher retrieve UserState Object"
			 * );
			 * 
			 * if (result != null && result.size() == 0)
			 * System.out.println(String.format("found nothing in database"));
			 */

			if (result != null && result.size() != 0) {

				obj = result;

			} else {

				AnalysisLogger.getLogger().debug(
						"ConnectionManager->Error: Result not available");

				throw new Exception("Result not available");

			}

		} catch (Exception e) {

			e.printStackTrace();

			// System.out.println(String.format("Error while executing query: %1$s %2$s",
			// query, e.getMessage()));
			// e.printStackTrace();
			// System.out.println(String.format("Error while executing query: %1$s %2$s",
			// query, e.getMessage()));
			
//			 AnalysisLogger.getLogger().debug(
//			 "In ConnectionManager-> ", e);

			if (e instanceof org.hibernate.MappingException) {

				// e.printStackTrace();

				// System.out.println(e.getClass().toString());

				// AnalysisLogger
				// .getLogger()
				// .debug("In ConnectionManager-> ERROR The query could not be executed: Error in retrieving a user defined type. Try to use a store procedure to convert the type");
				//
				// throw new Exception(
				// "The query could not be executed: Error in retrieving a user defined type. Try to use a store procedure to convert the type");

				AnalysisLogger.getLogger().debug(
						"In ConnectionManager-> " + e.getLocalizedMessage());

				throw new Exception(e.getLocalizedMessage());

			}

			if (e instanceof org.hibernate.exception.SQLGrammarException) {

				String cause = "";
				if (e.getCause() != null) {
					cause = e.getCause().getLocalizedMessage();
					// System.out.println(e.getCause().getLocalizedMessage());
				}

				// System.out.println(e.getCause().getMessage());

				// AnalysisLogger.getLogger().debug("In ConnectionManager-> ERROR The query could not be executed: SQL grammar error in the query");

				// throw new
				// Exception("The query could not be executed: SQL grammar error in the query");

				// AnalysisLogger.getLogger().debug(
				// "In ConnectionManager-> "
				// + e.getCause().getLocalizedMessage());
				//
				// throw new Exception(e.getCause().getMessage());

				AnalysisLogger.getLogger().debug(
						"In ConnectionManager-> " + e.getLocalizedMessage()
								+ "." + cause);

				throw new Exception(e.getLocalizedMessage() + "." + cause);

			}

			if (e instanceof org.hibernate.exception.GenericJDBCException) {

				// AnalysisLogger.getLogger().debug(
				// "In ConnectionManager-> "
				// + e.getCause().toString());

				// e.printStackTrace();

				AnalysisLogger.getLogger().debug(
						"In ConnectionManager-> " + e.getLocalizedMessage());

				throw new Exception(e.getLocalizedMessage());

			}

			else {

				throw e;

			}

			// throw e;
		}

		return obj;

	}

	// //to cancel the execution of the current query
	// public void removeQueryExecution(SessionFactory DBSessionFactory) throws
	// Exception{
	//
	// try{
	//
	// Session ss = DBSessionFactory.getCurrentSession();
	//
	// ss.beginTransaction();
	//
	// ss.cancelQuery();
	//
	// ss.getTransaction().commit();
	//
	// }
	// catch (Exception e) {
	// throw e;
	// }
	//
	// }

	// /** Method that creates the connection */
	// public SessionFactory createConnection(AlgorithmConfiguration config) {
	//
	// SessionFactory dbconnection = DatabaseUtils.initDBSession(config);
	//
	// return dbconnection;
	//
	// }

}
