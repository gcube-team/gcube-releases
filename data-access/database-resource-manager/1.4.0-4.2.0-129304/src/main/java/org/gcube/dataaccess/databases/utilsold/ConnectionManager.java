package org.gcube.dataaccess.databases.utilsold;
//package org.gcube.dataanalysis.databases.utilsold;
//
////import java.awt.List;
//
//import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
//import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
//import org.hibernate.Query;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.util.List;
//
///**
// * Class that allows to manage a database selected from a user. It performs to
// * set the database configuration, to connect to the database and finally to
// * execute a query.
// */
//public class ConnectionManager {
//	
//	private org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory df;
//
//	
//	public ConnectionManager() {
//
////		org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory df = new org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory();
//		df = new org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory();
//
//		
//	}
//
//	/** Method that allows to set the configuration */
//	public AlgorithmConfiguration setconfiguration(String ConfigPath,
//			String DatabaseUserName, String DatabasePassword,
//			String DatabaseDriver, String DatabaseDialect, String DatabaseURL,
//			String DatabaseName) throws IOException {
//
//		AlgorithmConfiguration config = new AlgorithmConfiguration();
//
//		if (DatabaseName.equals("")) {
//
//			throw new MalformedURLException(
//					"Invalid Url: the database's name is not present");
//			// return null;
//		}
//
//		if (!ConfigPath.equals(""))
//			config.setConfigPath(ConfigPath);
//
//		if (!DatabaseUserName.equals("")) {
//			config.setParam("DatabaseUserName", DatabaseUserName);
//		}
//
//		if (!DatabasePassword.equals(""))
//			config.setParam("DatabasePassword", DatabasePassword);
//
//		if (!DatabaseDriver.equals(""))
//			config.setParam("DatabaseDriver", DatabaseDriver);
//
//		if (!DatabaseDialect.equals(""))
//			config.setParam("DatabaseDialect", DatabaseDialect);
//
//		if (!DatabaseURL.equals(""))
//			config.setParam("DatabaseURL", DatabaseURL);
//
//		return config;
//
//	}
//
//	/** Method that creates the connection */
//	public SessionFactory createConnection(AlgorithmConfiguration config) {
//
//		SessionFactory dbconnection = DatabaseUtils.initDBSession(config);
//
//		return dbconnection;
//
//	}
//	
//	
//
//	// public List <Object> executeQuery(String query, SessionFactory
//	// DBSessionFactory){
//	//
//	// List <Object> obj = null;
//	//
//	//
//	//
//	// return obj;
//	// }
//	//
//
//	/** Method that execute a query */	
//	public List<Object> executeQuery(String query,
//			SessionFactory DBSessionFactory) throws Exception {
//
//		List<Object> obj = null;
//		Session ss = null;
//
//		try {
//			ss = DBSessionFactory.getCurrentSession();
//
//			ss.beginTransaction();
//
//			Query qr = null;
//
//			// Wrapper for a query. It allows the query to operate in a proper
//			// way
//			query = "select * from (" + query + ") as query";
//
//			qr = ss.createSQLQuery(query);
//
//			List<Object> result = qr.list();
//
//			ss.getTransaction().commit();
//
//			/*
//			 * if (result == null) System.out.println(
//			 * "Hibernate doesn't return a valid object when org.gcube.contentmanagement.lexicalmatcher retrieve UserState Object"
//			 * );
//			 * 
//			 * if (result != null && result.size() == 0)
//			 * System.out.println(String.format("found nothing in database"));
//			 */
//			if (result != null && result.size() != 0) {
//				obj = result;
//			}
//
//		} catch (Exception e) {
//
//			// System.out.println(String.format("Error while executing query: %1$s %2$s",
//			// query, e.getMessage()));
//			// e.printStackTrace();
//			// System.out.println(String.format("Error while executing query: %1$s %2$s",
//			// query, e.getMessage()));
//			throw e;
//		}
//
//		return obj;
//
//	}
//
//}
