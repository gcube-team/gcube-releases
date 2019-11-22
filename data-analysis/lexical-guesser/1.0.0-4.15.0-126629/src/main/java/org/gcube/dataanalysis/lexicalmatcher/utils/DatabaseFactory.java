package org.gcube.dataanalysis.lexicalmatcher.utils;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.gcube.dataanalysis.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DatabaseFactory {

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
		
		InputStream stream;
		try {
			File fl = new File(configurationFile);
			stream = new FileInputStream(fl);
		} catch (Exception e) {
			stream = ClassLoader.getSystemResourceAsStream(configurationFile);
		}
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(stream);
		
		List<Node> nodes = document.selectNodes("//hibernate-configuration/session-factory/property");
		Iterator<Node> nodesIterator = nodes.iterator();
		
//		System.out.println("--- DATABASE Configuration ---  ");
		
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
				LexicalLogger.getLogger().trace("Dialect -> "+config.getDatabaseDialect());
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
		try {
			ss = DBSessionFactory.getCurrentSession();

			ss.beginTransaction();

			Query qr = null;

			if (useSQL)
				qr = ss.createSQLQuery(query);
			else
				qr = ss.createQuery(query);

			List<Object> result = qr.list();

			ss.getTransaction().commit();

			/*
			if (result == null)
				System.out.println("Hibernate doesn't return a valid object when org.gcube.contentmanagement.lexicalmatcher retrieve UserState Object");

			if (result != null && result.size() == 0)
				System.out.println(String.format("found nothing in database"));
*/
			if (result != null && result.size() != 0) {
				obj = result;
			}

		} catch (Exception e) {

//			System.out.println(String.format("Error while executing query: %1$s %2$s", query, e.getMessage()));
//			e.printStackTrace();
			System.out.println(String.format("Error while executing query: %1$s %2$s", query, e.getMessage()));
			rollback(ss);
		}

		return obj;

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
			LexicalLogger.getLogger().debug("ERROR IN UPDATE: "+e.getMessage());
			throw e;
		}
	}

	public static void executeSQLUpdate(String query, SessionFactory DBSessionFactory) throws Exception{
		executeHQLUpdate(query, DBSessionFactory, true);
	}

	public static List<Object> executeSQLQuery(String query, SessionFactory DBSessionFactory) {
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
