package org.gcube.dataanalysis.ecoengine.utils;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.dataanalysis.ecoengine.datatypes.DatabaseType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.DatabaseParameters;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DatabaseFactory{

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseFactory.class);

	public static SessionFactory initDBConnection(String configurationFile) throws Exception {
		String xml = FileTools.readXMLDoc(configurationFile);
		LOGGER.debug("initialising DB Connection with conf: {} ",xml);
		SessionFactory DBSessionFactory = null;
		Configuration cfg = new Configuration();
		cfg = cfg.configure(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes())));
		DBSessionFactory = cfg.buildSessionFactory();
		return DBSessionFactory;
	}


	public static List<StatisticalType> getDefaultDatabaseConfiguration(String configurationFile) throws Exception {

		final List<StatisticalType> defaultconfig = new ArrayList<StatisticalType>();

		LOGGER.debug("reading default DB configuration from {}",configurationFile);

		// take the configuration file
		File fl = new File(configurationFile);
		FileInputStream stream = new FileInputStream(fl);

		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

		parser.parse(stream, new DefaultHandler(){

			private DatabaseParameters actualparameter = null;
			private String actualParameterDescription= null;

			@Override
			public void startElement(String uri, String localName,
					String qName, Attributes attributes) throws SAXException {

				if (localName.equals("property")){
					String element = attributes.getValue("name");

					if (element.equals("connection.driver_class")){
						actualparameter = DatabaseParameters.DATABASEDRIVER;
						actualParameterDescription = "DatabaseDriver";
					}else if (element.equals("connection.url")) {
						actualparameter = DatabaseParameters.DATABASEURL;
						actualParameterDescription = "DatabaseURL";
					}else if (element.equals("connection.username")){ 
						actualparameter = DatabaseParameters.DATABASEUSERNAME;
						actualParameterDescription = "DatabaseUserName";
					}else if (element.equals("connection.password")){ 
						actualparameter = DatabaseParameters.DATABASEPASSWORD;
						actualParameterDescription = "DATABASEPASSWORD";
					}else if (element.equals("dialect")){
						actualparameter = DatabaseParameters.DATABASEDIALECT;
						actualParameterDescription = "DATABASEDIALECT";
					}
				}

			}

			@Override
			public void characters(char ch[], int start, int length) throws SAXException {

				if (actualParameterDescription!=null){
					defaultconfig.add(new DatabaseType(actualparameter, actualParameterDescription, actualParameterDescription, new String(ch, start, length)));
					actualparameter = null;
					actualParameterDescription= null;
				}
			}

		});




		return defaultconfig;
	}

	public static SessionFactory initDBConnection(String configurationFile, LexicalEngineConfiguration config) throws Exception {

		LOGGER.debug("init DB configuration from {}",configurationFile);

		if (config==null)
			return initDBConnection(configurationFile);
		
		Configuration cfg = new Configuration();
		
		try(FileInputStream stream = new FileInputStream(new File(configurationFile))){
			cfg = cfg.configure(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream));
		}
		

		if ((config.getDatabaseDriver() != null)&&(config.getDatabaseDriver().length()>0))
			cfg.setProperty("hibernate.connection.driver_class",config.getDatabaseDriver());
		else
			config.setDatabaseDriver(cfg.getProperty("hibernate.connection.driver_class"));

		if ((config.getDatabaseURL() != null)&&(config.getDatabaseURL().length()>0))
			cfg.setProperty("hibernate.connection.url",config.getDatabaseURL());
		else
			config.setDatabaseURL(cfg.getProperty("hibernate.connection.url"));

		if ((config.getDatabaseUserName() != null)&&(config.getDatabaseUserName().length()>0))
			cfg.setProperty("hibernate.connection.username", config.getDatabaseUserName());
		else
			config.setDatabaseUserName(cfg.getProperty("hibernate.connection.username"));

		if ((config.getDatabasePassword() != null)&&(config.getDatabasePassword().length()>0))
			cfg.setProperty("hibernate.connection.password", config.getDatabasePassword());
		else
			config.setDatabasePassword(cfg.getProperty("hibernate.connection.password"));

		if ((config.getDatabaseDialect() != null)&&(config.getDatabaseDialect().length()>0))
			cfg.setProperty("hibernate.dialect", config.getDatabaseDialect());
		else
			config.setDatabaseDialect(cfg.getProperty("hibernate.dialect"));

		if (config.getDatabaseIdleConnectionTestPeriod() != null)
			cfg.setProperty("hibernate.c3p0.idleConnectionTestPeriod",config.getDatabaseIdleConnectionTestPeriod());
		else
			config.setDatabaseIdleConnectionTestPeriod(cfg.getProperty("hibernate.c3p0.idleConnectionTestPeriod"));

		if (config.getDatabaseAutomaticTestTable() != null)
			cfg.setProperty("hibernate.c3p0.automaticTestTable",config.getDatabaseAutomaticTestTable());
		else
			config.setDatabaseAutomaticTestTable(cfg.getProperty("hibernate.c3p0.automaticTestTable"));


		cfg.setProperty("hibernate.hbm2ddl.auto", "create");
		
		Properties props = cfg.getProperties();

		LOGGER.debug("properties for this connection are url: {}  user: {}", props.get("hibernate.connection.url"), props.getProperty("hibernate.connection.username"));
	
		
		SessionFactory DBSessionFactory = null;
		DBSessionFactory = cfg.buildSessionFactory();

		
		return DBSessionFactory;
	}

	@SuppressWarnings({"unchecked"})
	public static List<Object> executeHQLQuery(String query, SessionFactory DBSessionFactory, boolean useSQL) {
		List<Object> obj = null;
		Session ss = null;
		ss = DBSessionFactory.getCurrentSession();

		ss.beginTransaction();

		Query qr = null;

		if (useSQL)
			qr = ss.createSQLQuery(query);
		else
			qr = ss.createQuery(query);
		List<Object> result = null;
		try{
			result = qr.list();
			ss.getTransaction().commit();
		}catch(Exception e){
			System.out.println("Could not execute query "+e.getMessage());
			e.printStackTrace();
			rollback(ss);
		}

		if (result == null)
			System.out.println("Hibernate doesn't return a valid object when org.gcube.contentmanagement.lexicalmatcher retrieve UserState Object");

		//			if (result != null && result.size() == 0)
		//				System.out.println(String.format("found nothing in database for query: "+query));

		if (result != null && result.size() != 0) {
			obj = result;
		}


		//			rollback(ss);

		return obj;

	}

	public static Connection getDBConnection(String drivername,String username, String password, String databaseurl) throws Exception{
		// Load the database driver
		Class.forName(drivername) ;
		// Get a connection to the database
		Connection conn = DriverManager.getConnection(databaseurl,username,password) ;
		return conn;
	}

	public static void executeUpdateNoTransaction(final String query, String drivername,String username, String password, String databaseurl, boolean useSQL) throws Exception{

		// Load the database driver
		Class.forName(drivername) ;

		// Get a connection to the database
		Connection conn = DriverManager.getConnection(databaseurl,username,password) ;

		// Get a statement from the connection
		Statement stmt = conn.createStatement() ;

		// Execute the query
		stmt.executeUpdate( query) ;

		stmt.close() ;
		conn.close() ;

	}

	public static void executeUpdateNoTransaction(final String query, SessionFactory DBSessionFactory, boolean useSQL) throws Exception{
		//		System.out.println("executing query: " + query);
		Session ss = null;

		try {




			/*

			ss = DBSessionFactory.getCurrentSession();

//			System.out.println("executing query");
			ss.doWork(new Work() {

				@Override
				public void execute(Connection conn) throws SQLException {
					Statement stmt = conn.createStatement() ;
					  // Execute the query
				      ResultSet rs = stmt.executeQuery(query) ;

				}
			});
			 */	  
		} catch (Exception e) {
			throw e;
		}

	}

	public static void executeHQLUpdate(String query, SessionFactory DBSessionFactory, boolean useSQL) throws Exception{
		//		System.out.println("executing query: " + query);
		Session ss = null;

		try {

			ss = DBSessionFactory.getCurrentSession();			
			//			System.out.println("executing query");
			ss.beginTransaction();
			Query qr = null;

			if (useSQL)
				qr = ss.createSQLQuery(query);
			else
				qr = ss.createQuery(query);

			qr.executeUpdate();
			ss.getTransaction().commit();

		} catch (Exception e) {
			rollback(ss);
			//			e.printStackTrace();
			throw e;
		}
	}


	public static void executeNativeUpdate(String query, SessionFactory DBSessionFactory) {
		//		System.out.println("executing query: " + query);
		Session ss = null;

		try {

			ss = DBSessionFactory.getCurrentSession();

			System.out.println("executing query");
			ss.beginTransaction();
			Query qr = null;


			qr = DBSessionFactory.getCurrentSession().getNamedQuery("mySp").setParameter("param", query);

			qr.executeUpdate();
			ss.getTransaction().commit();

		} catch (Exception e) {
			rollback(ss);
			e.printStackTrace();
		}
	}


	public static void executeSQLUpdate(String query, SessionFactory DBSessionFactory) throws  Exception {
		executeHQLUpdate(query, DBSessionFactory, true);
	}

	public static List<Object> executeSQLQuery(String query, SessionFactory DBSessionFactory) {
		//		System.out.println("QUERY: "+query);
		return executeHQLQuery(query, DBSessionFactory, true);
	}

	public static void rollback(Session ss) {

		try {
			if (ss != null && ss.getTransaction() != null)
				ss.getTransaction().rollback();
		} catch (Exception ex) {

		} finally {
			try {
				ss.close();
			} catch (Exception ee) {
			}
		}
	}

	public static void saveObject(Object obj, SessionFactory DBSessionFactory) throws Exception {
		if (DBSessionFactory != null) {
			Session ss = null;
			try {
				ss = DBSessionFactory.getCurrentSession();
				ss.beginTransaction();
				ss.saveOrUpdate(obj);
				ss.getTransaction().commit();
			} catch (Exception e) {
				rollback(ss);
				throw e;
			}
		}
	}

}
