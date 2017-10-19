package org.gcube.contentmanagement.lexicalmatcher.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseFactory {

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

	public static SessionFactory initDBConnection(String configurationFile, LexicalEngineConfiguration config) throws Exception {

		LOGGER.debug("init DB configuration from {}",configurationFile);

		if (config==null)
			return initDBConnection(configurationFile);
		
		LOGGER.debug("config is not null !!");
		
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

		} catch (Throwable e) {

			//			System.out.println(String.format("Error while executing query: %1$s %2$s", query, e.getMessage()));
			//			e.printStackTrace();
			LOGGER.error(String.format("Error while executing query: %1$s ", query), e);
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

		} catch (Throwable e) {
			rollback(ss);
			LOGGER.error("ERROR IN UPDATE: ",e);
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
