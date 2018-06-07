package org.gcube.dataaccess.databases.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.hibernate.MappingException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/** Class that allows to connect to a database and to execute a query */
public class DatabaseFactory {

	// Method that establish a connection with the database
	public static SessionFactory initDBConnection(String configurationFile)
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

	@SuppressWarnings({ "unchecked" })
	public static SessionFactory initDBConnection(String configurationFile,
			LexicalEngineConfiguration config)
			throws Exception {
//		AnalysisLogger.getLogger().debug("DatabaseFactory-> start init");
		if (config == null)
			return initDBConnection(configurationFile);

		// take the configuration file
		File fl = new File(configurationFile);
		FileInputStream stream = new FileInputStream(fl);

		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(stream);
		List<Node> nodes = document
				.selectNodes("//hibernate-configuration/session-factory/property");
		Iterator<Node> nodesIterator = nodes.iterator();

		while (nodesIterator.hasNext()) {
			Node currentnode = nodesIterator.next();
			String element = currentnode.valueOf("@name");
			if (element.equals("connection.driver_class")) {
				if ((config.getDatabaseDriver() != null)
						&& (config.getDatabaseDriver().length() > 0))
					currentnode.setText(config.getDatabaseDriver());
				else
					config.setDatabaseDriver(currentnode.getText());
			}
			if (element.equals("connection.url")) {
				if ((config.getDatabaseURL() != null)
						&& (config.getDatabaseURL().length() > 0))
					currentnode.setText(config.getDatabaseURL());
				else
					config.setDatabaseURL(currentnode.getText());
			}
			if (element.equals("connection.username")) {
				if ((config.getDatabaseUserName() != null)
						&& (config.getDatabaseUserName().length() > 0))
					currentnode.setText(config.getDatabaseUserName());
				else
					config.setDatabaseUserName(currentnode.getText());
			}
			if (element.equals("connection.password")) {
				if ((config.getDatabasePassword() != null)
						&& (config.getDatabasePassword().length() > 0))
					currentnode.setText(config.getDatabasePassword());
				else
					config.setDatabasePassword(currentnode.getText());
			}
			if (element.equals("dialect")) {
				if ((config.getDatabaseDialect() != null)
						&& (config.getDatabaseDialect().length() > 0))
					currentnode.setText(config.getDatabaseDialect());
				else
					config.setDatabaseDialect(currentnode.getText());
			}
			if (element.equals("c3p0.idleConnectionTestPeriod")) {
				if (config.getDatabaseIdleConnectionTestPeriod() != null)
					currentnode.setText(config
							.getDatabaseIdleConnectionTestPeriod());
				else
					config.setDatabaseIdleConnectionTestPeriod(currentnode
							.getText());
			}
			if (element.equals("c3p0.automaticTestTable")) {
				if (config.getDatabaseAutomaticTestTable() != null)
					currentnode.setText(config.getDatabaseAutomaticTestTable());
				else
					config.setDatabaseAutomaticTestTable(currentnode.getText());
			}

//			 if (element.equals("c3p0.timeout")) {
//			 currentnode.setText("" + 30);
//			 }
			/*
			if (element.equals("hibernate.c3p0.testConnectionOnCheckout")) {

				currentnode.setText("" + true);
			}
			
			if (element.equals("hibernate.c3p0.idle_test_period")) {

				currentnode.setText("" + 1);
			}
			
			if (element.equals("hibernate.c3p0.timeout")) {

				currentnode.setText("" + 1);
			}
			
			if (element.equals("connection.pool_size")) {

				currentnode.setText("" + 1);
			}
			if (element.equals("c3p0.idleConnectionTestPeriod")) {

				currentnode.setText("" + 1);
			}
			*/
			if (element.equals("c3p0.checkoutTimeout")) {

//				currentnode.setText("" + (20000));          //added a timeout about 30 seconds 
				currentnode.setText("" + (1000));           //added a timeout about 5 seconds
			}
			
//			if (element.equals("c3p0.acquireRetryAttempts")) {
//
//				currentnode.setText("" + 10);
//			}

		}

		Configuration cfg = new Configuration();
		cfg = cfg.configure(DocumentBuilderFactory.newInstance()
				.newDocumentBuilder()
				.parse(new ByteArrayInputStream(document.asXML().getBytes())));
		cfg.setProperty("hibernate.hbm2ddl.auto", "create");

		SessionFactory DBSessionFactory = null;
//		AnalysisLogger.getLogger().debug("DatabaseFactory-> buildSession");
		DBSessionFactory = cfg.buildSessionFactory();

		// close stream
		stream.close();
		return DBSessionFactory;
	}

	// Method that execute the query
	public static List<Object> executeSQLQuery(String query,
			SessionFactory DBSessionFactory) throws Exception {
		// System.out.println("QUERY: "+query);
		try {
			return executeHQLQuery(query, DBSessionFactory, true);

		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}

	}

	public static List<Object> executeHQLQuery(String query,
			SessionFactory DBSessionFactory, boolean useSQL) throws Exception,
			MappingException {
		Session ss = null;
		List<Object> obj = null;

		try {

			ss = DBSessionFactory.getCurrentSession();

			ss.beginTransaction();

			Query qr = null;

			if (useSQL)
				qr = ss.createSQLQuery(query);
			else
				qr = ss.createQuery(query);

			List<Object> result = null;

			AnalysisLogger.getLogger().debug(
					"In DatabaseFactory->" + qr.getQueryString());
			try {
				result = qr.list();
				ss.getTransaction().commit();

				if (result == null)
					System.out
							.println("Hibernate doesn't return a valid object when org.gcube.contentmanagement.lexicalmatcher retrieve UserState Object");

				// if (result != null && result.size() == 0)
				// System.out.println(String.format("found nothing in database for query: "+query));

				if (result != null && result.size() != 0) {
					obj = result;
				}

				rollback(ss);

				return obj;

			} catch (Exception e) {

				if (e.getClass()
						.toString()
						.contains("org.hibernate.exception.SQLGrammarException")) {

					// System.out.println(e.getCause().getMessage());

					// AnalysisLogger.getLogger().debug("In ConnectionManager-> ERROR The query could not be executed: SQL grammar error in the query");

					// throw new
					// Exception("The query could not be executed: SQL grammar error in the query");

					AnalysisLogger.getLogger().debug(
							"In DatabaseFactory-> "
									+ e.getCause().getLocalizedMessage());
					throw new Exception(e.getCause().getMessage());

				}

				// if (e.getClass()
				// .toString()
				// .contains(
				// "com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException"))
				// {
				//
				// // System.out.println(e.getClass().toString());
				//
				// AnalysisLogger.getLogger().debug(
				// "In DatabaseFactory-> "
				// + e.getCause().getLocalizedMessage());
				//
				// throw new Exception(e.getCause().getMessage());
				// }

				else {
					throw e;

				}

			}

		} catch (Exception e) {

			throw e;
		}

	}

	public static void rollback(Session ss) {

		try {
			if (ss != null && ss.getTransaction() != null)
				ss.getTransaction().rollback();
		} catch (Exception ex) {
			// throw ex;

		} finally {
			try {
				ss.close();
			} catch (Exception ee) {

				// throw ee;
			}
		}
	}

	// public static void executeSQLUpdate(String query, SessionFactory
	// DBSessionFactory) throws Exception {
	// executeHQLUpdate(query, DBSessionFactory, true);
	// }

	// public static void executeHQLUpdate(String query, SessionFactory
	// DBSessionFactory, boolean useSQL) throws Exception{
	// // System.out.println("executing query: " + query);
	// Session ss = null;
	//
	// try {
	//
	// ss = DBSessionFactory.getCurrentSession();
	// // System.out.println("executing query");
	// ss.beginTransaction();
	// Query qr = null;
	//
	// if (useSQL)
	// qr = ss.createSQLQuery(query);
	// else
	// qr = ss.createQuery(query);
	//
	// qr.executeUpdate();
	// ss.getTransaction().commit();
	//
	// } catch (Exception e) {
	// AnalysisLogger.getLogger().debug(query);
	// rollback(ss);
	// // e.printStackTrace();
	// throw e;
	// }
	// }

}
