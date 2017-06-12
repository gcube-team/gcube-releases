package org.gcube.data.simulfishgrowthdata.util;

import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {

	private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);

	private static HibernateUtil inst = null;

	private SessionFactory sessionFactory;

	public static final String COUNT_COLUMN_NAME = "COUNT_VALUE";

	private static String dbEndpoint = null;
	private static String scope = null;

	String defaultConfigFileName = "hibernate.cfg.xml";
	static public String debugLoadFromLocalXml = null;

	public static synchronized SessionFactory getSessionFactory() {
		return getInstance().sessionFactory;
	}

	public static String getCountColumnName() {
		return COUNT_COLUMN_NAME;
	}

	public static void closeSession(Session session) {
		try {
			if ((session != null) && session.isOpen()) {
				session.close();
			}
		} catch (HibernateException he) {
			logger.error(he.getMessage());
		}
	}

	public static Session openSession() throws HibernateException {
		return openSession(getSessionFactory());
	}

	public static Session openSession(SessionFactory sessionFactory) throws HibernateException {

		return sessionFactory.getCurrentSession();
	}

	private static synchronized HibernateUtil getInstance() {
		if (inst == null)
			throw new RuntimeException("Not configured yet. Please call config");
		return inst;
	}

	synchronized public static void config(String dbEndpoint, String scope) {
		if (logger.isTraceEnabled())
			logger.trace(
					String.format("forced hibernate configuration for endpoint [%s] scope [%s]", dbEndpoint, scope));
		HibernateUtil.dbEndpoint = dbEndpoint;
		HibernateUtil.scope = scope;
		if (inst != null && inst.sessionFactory != null) {
			inst.sessionFactory.close();
			inst.sessionFactory = null;
		}
		inst = new HibernateUtil();
	}

	synchronized public static void configGently(String dbEndpoint, String scope) {
		if (logger.isTraceEnabled())
			logger.trace(String.format("dbEndpoint [%s] scope [%s]", dbEndpoint, scope));

		if (inst == null || inst.sessionFactory == null) {
			config(dbEndpoint, scope);
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Hibernate already configured");
			}
		}

	}

	private HibernateUtil() {
		try {
			Configuration configuration = new Configuration();
			configuration = configuration.configure();
			String configFileName = defaultConfigFileName;
			if (debugLoadFromLocalXml != null)
				configFileName = debugLoadFromLocalXml;

			configuration.configure(configFileName);

			// the following two lines cause errors when put in the cfg file
			configuration.addResource("entities.hbm.xml");
			configuration.setProperty("hibernate.current_session_context_class", "thread");
			// slow first connection, http://stackoverflow.com/a/10109005/874502
			configuration.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");
			// configuration.setProperty("hibernate.jdbc.use_get_generated_keys","true");
			if (debugLoadFromLocalXml != null) {
				if (logger.isTraceEnabled())
					logger.trace("configure on local xml");
				// nothing more to do
			} else if (GCubeUtils.isPrefilledDBCredentials()) {
				if (logger.isTraceEnabled())
					logger.trace("configure on prefilled values");

				fillConfiguration(configuration, GCubeUtils.getPrefilledCredentials());
			} else {
				if (logger.isTraceEnabled())
					logger.trace("configure on endpoint discovery");

				fillConfiguration(configuration, GCubeUtils.getCredentials(dbEndpoint, scope));
			}

			StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
					.applySettings(configuration.getProperties());
			sessionFactory = configuration.buildSessionFactory(builder.build());
			if (logger.isTraceEnabled())
				logger.trace(String.format("sessionFactory %s", sessionFactory));

		} catch (Exception e) {
			logger.error("Problem Connecting to the database", e);
			e.printStackTrace();
		}
		if (logger.isTraceEnabled())
			logger.trace(String.format("after hibernate ctor sessionFactory %s", sessionFactory));

	}

	static private void fillConfiguration(Configuration configuration, Map<String, String> confProperties)
			throws Exception {
		for (Map.Entry<String, String> entry : confProperties.entrySet()) {
			configuration.setProperty(entry.getKey(), entry.getValue());
		}

	}

}