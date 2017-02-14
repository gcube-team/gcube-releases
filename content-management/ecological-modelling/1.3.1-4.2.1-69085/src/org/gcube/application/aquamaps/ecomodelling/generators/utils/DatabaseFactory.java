package org.gcube.application.aquamaps.ecomodelling.generators.utils;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;

public class DatabaseFactory{

	public static SessionFactory initDBConnection(String configurationFile) throws Exception {
		String xml = FileTools.readXMLDoc(configurationFile);
		SessionFactory DBSessionFactory = null;
		Configuration cfg = new Configuration();
		cfg = cfg.configure(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes())));
		DBSessionFactory = cfg.buildSessionFactory();
		return DBSessionFactory;
	}

	@SuppressWarnings({"unchecked"})
	public static SessionFactory initDBConnection(String configurationFile, LexicalEngineConfiguration config) throws Exception {

		
		if (config==null)
			return initDBConnection(configurationFile);
		
		
		// take the configuration file
		File fl = new File(configurationFile);
		FileInputStream stream = new FileInputStream(fl);
		
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(stream);
		List<Node> nodes = document.selectNodes("//hibernate-configuration/session-factory/property");
		Iterator<Node> nodesIterator = nodes.iterator();

		while (nodesIterator.hasNext()) {
			Node currentnode = nodesIterator.next();
			String element = currentnode.valueOf("@name");
			if (element.equals("connection.driver_class"))
				if (config.getDatabaseDriver() != null){
					currentnode.setText(config.getDatabaseDriver());
				}
			if (element.equals("connection.url")) {
				if (config.getDatabaseURL() != null)
					currentnode.setText(config.getDatabaseURL());
			}
			if (element.equals("connection.username")) {
				if (config.getDatabaseUserName() != null)
					currentnode.setText(config.getDatabaseUserName());
			}
			if (element.equals("connection.password")) {
				if (config.getDatabasePassword() != null)
					currentnode.setText(config.getDatabasePassword());
			}
			if (element.equals("dialect")) {
				if (config.getDatabaseDialect() != null)
					currentnode.setText(config.getDatabaseDialect());
			}
			if (element.equals("c3p0.idleConnectionTestPeriod")) {
				if (config.getDatabaseIdleConnectionTestPeriod() != null)
					currentnode.setText(config.getDatabaseIdleConnectionTestPeriod());
			}
			if (element.equals("c3p0.automaticTestTable")) {
				if (config.getDatabaseAutomaticTestTable() != null)
					currentnode.setText(config.getDatabaseAutomaticTestTable());
			}
		}

		Configuration cfg = new Configuration();
		cfg = cfg.configure(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(document.asXML().getBytes())));
		cfg.setProperty("hibernate.hbm2ddl.auto", "create");
		
		SessionFactory DBSessionFactory = null;
		DBSessionFactory = cfg.buildSessionFactory();

		// close stream
		stream.close();
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

			List<Object> result = qr.list();

			ss.getTransaction().commit();

			if (result == null)
				System.out.println("Hibernate doesn't return a valid object when org.gcube.contentmanagement.lexicalmatcher retrieve UserState Object");

//			if (result != null && result.size() == 0)
//				System.out.println(String.format("found nothing in database for query: "+query));

			if (result != null && result.size() != 0) {
				obj = result;
			}

			rollback(ss);

		return obj;

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
