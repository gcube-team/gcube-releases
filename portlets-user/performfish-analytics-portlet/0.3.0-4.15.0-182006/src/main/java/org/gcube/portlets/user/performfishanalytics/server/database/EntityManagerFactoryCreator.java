package org.gcube.portlets.user.performfishanalytics.server.database;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.jpa.PersistenceProvider;
import org.gcube.portlets.user.performfishanalytics.server.util.ServiceParameters;
import org.gcube.portlets.user.performfishanalytics.shared.exceptions.SessionExpired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class EntityManagerFactoryCreator is a Singleton.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 20, 2015
 */
public class EntityManagerFactoryCreator {

	public static final String PERFORM_FISH_ANALYTICS_DB_ENDPOINT_NAME = "PerformFISH-AnalyticsDB";
	public static final String PERFORM_FISH_ANALYTICS_PERSISTENCE_FACTORY = "PERFORMFISH_ANALYTICS_PERSISTENCE_FACTORY";
	public static Logger log = LoggerFactory.getLogger(EntityManagerFactoryCreator.class);


	protected static final String PROPERTY_CATALINA_HOME = "catalina.home";
	protected static final String CATALINA_HOME = "CATALINA_HOME";

	private static EntityManagerFactory factoryPerformFishAnalytics;
	private static ServiceParameters dbParameters;
	private static EntityManagerFactoryCreator INSTANCE;

	/**
	 * Gets the instance local mode.
	 *
	 * @return the instance local mode
	 * @throws Exception the exception
	 */
	public static synchronized EntityManagerFactoryCreator instanceLocalMode() throws Exception{

		if(INSTANCE==null){
			try{
				//INSTANCE = new EntityManagerFactoryCreator(scope);
				factoryPerformFishAnalytics = createEntityManagerFactoryLocalMode();
			}catch(Exception e){
				log.warn("EntityManagerFactory created in TEST MODE");
			}
		}

		return INSTANCE;
	}


	/**
	 * Gets the entity manager factory.
	 *
	 * @return the entity manager factory
	 */
	public static synchronized EntityManagerFactory getEntityManagerFactory(){

		if (factoryPerformFishAnalytics == null){
			log.info("EntityManagerFactory is null, creating..");
			factoryPerformFishAnalytics = createEntityManagerFactory();
		}
		else if(!factoryPerformFishAnalytics.isOpen()){
			log.info("EntityManagerFactory is not open, closing and creating..");
			factoryPerformFishAnalytics.close();
			factoryPerformFishAnalytics = createEntityManagerFactory();
		}
		log.info("Returning EntityManagerFactory");
		return factoryPerformFishAnalytics;
	}

	/**
	 * Creates the entity manager factory.
	 *
	 * @return the entity manager factory
	 */
	private static EntityManagerFactory createEntityManagerFactory() {

		Map<String, String> properties = new HashMap<String, String>();
		EntityManagerFactory emf = null;
		try {
			properties.put("javax.persistence.jdbc.url", "jdbc:h2://"+dbParameters.getUrl());
			properties.put("javax.persistence.jdbc.user", dbParameters.getUser());
			properties.put("javax.persistence.jdbc.password", dbParameters.getPassword());
			log.debug("Instancing new Entity Manager using properties: "+properties);
			emf = Persistence.createEntityManagerFactory(PERFORM_FISH_ANALYTICS_PERSISTENCE_FACTORY, properties);
		} catch (Exception e) {
			log.error("error on get createEntityManagerFactory " + e, e);
		}
		// emf = Persistence.createEntityManagerFactory("jpablogPUnit");
//		logger.info("created entity manager factory on: "+ properties.get(JAVAX_PERSISTENCE_JDBC_URL));
		return emf;
	}

	/**
	 * Creates the entity manager factory test mode.
	 *
	 * @return the entity manager factory
	 */
	private static EntityManagerFactory createEntityManagerFactoryLocalMode(){

		Map<String, String> properties = new HashMap<String, String>();
		EntityManagerFactory emf = null;
		try {
			String jdbcURL = getJDBCConnectionUrl(true);
			log.info("JDBC URL IS: "+jdbcURL);
			properties.put("javax.persistence.jdbc.url", jdbcURL);
//			properties.put("javax.persistence.jdbc.user", "postgres");
//			properties.put("javax.persistence.jdbc.password", "8gridsphere1");

			//emf = Persistence.createEntityManagerFactory(PERFORM_FISH_ANALYTICS_PERSISTENCE_FACTORY, properties);
			emf = new PersistenceProvider().createEntityManagerFactory(PERFORM_FISH_ANALYTICS_PERSISTENCE_FACTORY, properties);
			log.info("DB created at persistence.jdbc.url: "+jdbcURL+" isOpen? "+emf.isOpen());
		} catch (Exception e) {
			log.error("error on get createEntityManagerFactory " + e, e);
		}
		return emf;
	}

    /**
     * Gets the tomcat folder.
     *
     * @return the tomcat folder
     * @throws Exception the exception
     */
    public static String getTomcatFolder() throws Exception{

    	String catalinaHome = System.getenv(CATALINA_HOME) != null ? System.getenv(CATALINA_HOME) : System.getProperty(PROPERTY_CATALINA_HOME);

    	if(catalinaHome == null || catalinaHome.isEmpty()){
    		log.error("****\n\n\nCATALINA_HOME ENVIROMENT NOT FOUND -  RETURNED / PATH \n\n\n*****");
    		throw new Exception(CATALINA_HOME +" not found in the ENVIRONMENT");
    	}

    	return catalinaHome;
    }


    /**
     * Gets the JDBC connection url.
     *
     * @param localMode the local mode
     * @return the JDBC connection url
     * @throws SessionExpired the session expired
     * @throws Exception the exception
     */
    public static String getJDBCConnectionUrl(boolean localMode) throws SessionExpired, Exception{

    	if(localMode){
			log.info("DB EMBEDDED MODE ACTIVATED");
			return "jdbc:h2:"+getPersistenceFolderPath()+"/H2AnalyticsDB;create=true";
    	}

    	throw new Exception("Only JDBC local mode is working");
    }


    /**
     * Gets the persistence folder path.
     *
     * @return the persistence folder path
     * @throws Exception the exception
     */
    public static String getPersistenceFolderPath() throws Exception{
    	String tomcatFolder = getTomcatFolder();
    	tomcatFolder = tomcatFolder.endsWith("/") ? tomcatFolder.substring(0, tomcatFolder.length()-1) : tomcatFolder;
    	return String.format("%s/%s/%s", getTomcatFolder(), "persistence", "PerformFISH");

    }


}
