package org.gcube.portal.trainingmodule.database;



import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: Auto-generated Javadoc
/**
 * The Class EntityManagerFactoryCreator.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 9, 2018
 */
public class EntityManagerFactoryCreator {


	/** The Constant TRAINING_COURSES_DB. */
	public static final String TRAINING_COURSES_DB = "TrainingCourseAppDatabase";
	
	/** The Constant PERSISTENCE_DB_GCUBE_RELEASES. */
	public static final String PERSISTENCE_TRAINING_COURSES = "PERSISTENCE_TRAINING_COURSES";
	
	/** The Constant CATALINA_HOME. */
	public static final String CATALINA_HOME = "CATALINA_HOME";
	
	/** The Constant PROPERTY_CATALINA_HOME. */
	public static final String PROPERTY_CATALINA_HOME = "catalina.home";
	
	/** The logger. */
	public static Logger logger = LoggerFactory.getLogger(EntityManagerFactoryCreator.class);

	/** The factory gcube releases. */
	private static EntityManagerFactory factoryTraninigCourses;
	
	/** The db parameters. */
	private static ServerParameters dbParameters;


	
	/**
	 * Inits the DB parameters.
	 *
	 * @param scope the scope
	 * @throws Exception the exception
	 */
	private static void initDBParameters(String scope) throws Exception {
		ServiceEndpointReader serviceEndPoint = new ServiceEndpointReader(scope, TRAINING_COURSES_DB);

		try {
			dbParameters = serviceEndPoint.readResource(true);
			logger.info("Read the "+serviceEndPoint +" to instance EntityManagerFactory");
		} catch (Exception e) {
			logger.error("Error on reading the resource: "+TRAINING_COURSES_DB +" in the scope: "+scope, e);
			throw new Exception("An error occurred on reading the resource: "+TRAINING_COURSES_DB +" in the scope: "+scope);
		}

	}

	/**
	 * Instance factory creator.
	 *
	 * @param scope the scope
	 * @return the entity manager factory
	 * @throws Exception the exception
	 */
	public static synchronized EntityManagerFactory instanceFactoryCreator(String scope) throws Exception{

		return getEntityManagerFactory(scope);
	}
	

	/**
	 * USE ONLY IN TEST MODE.
	 *
	 * @param scope the scope
	 * @return the instance test mode
	 * @throws Exception the exception
	 */
	public static synchronized EntityManagerFactory instanceFactoryTestMode(String scope) throws Exception{

		if(factoryTraninigCourses==null){
			try{
				//INSTANCE = new EntityManagerFactoryCreator(scope);
				factoryTraninigCourses = createEntityManagerFactoryTestMode();
			}catch(Exception e){
				logger.warn("EntityManagerFactory created in TEST MODE");
			}
		}

		return factoryTraninigCourses;
	}

	/**
	 * Gets the entity manager factory.
	 *
	 * @param scope the scope
	 * @return the entity manager factory
	 * @throws Exception the exception
	 */
	public static synchronized EntityManagerFactory getEntityManagerFactory(String scope) throws Exception{

		if (factoryTraninigCourses == null){
			logger.info("EntityManagerFactory is null, creating..");
			initDBParameters(scope);
			factoryTraninigCourses = createEntityManagerFactory();
		}
		else if(!factoryTraninigCourses.isOpen()){
			logger.info("EntityManagerFactory is not open, closing and creating..");
			factoryTraninigCourses.close();
			initDBParameters(scope);
			factoryTraninigCourses = createEntityManagerFactory();
		}
		logger.info("Returning EntityManagerFactory");
		return factoryTraninigCourses;
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
			properties.put("javax.persistence.jdbc.url", dbParameters.getUrl());
			properties.put("javax.persistence.jdbc.user", dbParameters.getUser());
			properties.put("javax.persistence.jdbc.password", dbParameters.getPassword());
			logger.debug("Instancing new Entity Manager using: [jdbc.url "+dbParameters.getUrl() + ", jdbc.user: "+dbParameters.getUser()+"]");
			emf = Persistence.createEntityManagerFactory(PERSISTENCE_TRAINING_COURSES, properties);
		} catch (Exception e) {
			logger.error("error on get createEntityManagerFactory " + e, e);
		}
		// emf = Persistence.createEntityManagerFactory("jpablogPUnit");
//		logger.info("created entity manager factory on: "+ properties.get(JAVAX_PERSISTENCE_JDBC_URL));
		return emf;
	}
	
	/**
	 * Gets the tomcat folder.
	 *
	 * @return $CATALINA_HOME
	 */
    public static String getTomcatFolder(){

    	String catalinaHome = System.getenv(CATALINA_HOME) != null ? System.getenv(CATALINA_HOME) : System.getProperty(PROPERTY_CATALINA_HOME);

    	if(catalinaHome == null || catalinaHome.isEmpty())
    		logger.error("CATALINA_HOME ENVIROMENT NOT FOUND -  RETURNED / PATH");


    	return catalinaHome.endsWith("/") ? catalinaHome : catalinaHome+"/";
    }

	/**
	 * Creates the entity manager factory test mode.
	 *
	 * @return the entity manager factory
	 */
	private static EntityManagerFactory createEntityManagerFactoryTestMode(){

		Map<String, String> properties = new HashMap<String, String>();
		EntityManagerFactory emf = null;
		try {
			properties.put("javax.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/trainingcourse");
//			properties.put("javax.persistence.jdbc.url", "jdbc:postgresql://dev.d4science.org:5432/gcube-releases-dev");
			properties.put("javax.persistence.jdbc.user", "traininguser");
			properties.put("javax.persistence.jdbc.password", "training");
			emf = Persistence.createEntityManagerFactory(PERSISTENCE_TRAINING_COURSES, properties);
		} catch (Exception e) {
			logger.error("Error on get createEntityManagerFactoryTestMode: ", e);
		}
		return emf;
	}
	
	/**
	 * Gets the DB parameters.
	 *
	 * @return the DB parameters
	 * @throws Exception 
	 */
	public static ServerParameters getDBParameters(String scope) throws Exception {
		if(dbParameters==null) {
			initDBParameters(scope);
		}
		return dbParameters;
	}


}
