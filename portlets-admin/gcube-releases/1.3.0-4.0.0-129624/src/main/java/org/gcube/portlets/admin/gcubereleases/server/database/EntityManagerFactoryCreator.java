package org.gcube.portlets.admin.gcubereleases.server.database;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.gcube.portlets.admin.gcubereleases.server.util.ServerParameters;
import org.gcube.portlets.admin.gcubereleases.server.util.ServiceEndpointReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class EntityManagerFactoryCreator is a Singleton.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 20, 2015
 */
public class EntityManagerFactoryCreator {

	public static final String GCUBE_RELEASES_ENDPOINT_NAME = "GcubeReleasesDB";
	public static final String PERSISTENCE_DB_GCUBE_RELEASES = "PERSISTENCE_GCUBE_RELEASES";
	public static Logger logger = LoggerFactory.getLogger(EntityManagerFactoryCreator.class);

	private static EntityManagerFactory factoryGcubeReleases;
	private static ServerParameters dbParameters;
	private static EntityManagerFactoryCreator INSTANCE;
	
	/**
	 * Instantiates a new entity manager factory creator.
	 *
	 * @param scope the scope
	 * @throws Exception the exception
	 */
	private EntityManagerFactoryCreator(String scope) throws Exception {
		
		ServiceEndpointReader serviceEndPoint = new ServiceEndpointReader(scope, GCUBE_RELEASES_ENDPOINT_NAME);
		
		try {
			dbParameters = serviceEndPoint.readResource(true);
			logger.info("Read "+serviceEndPoint +" to instance EntityManagerFactory");
		} catch (Exception e) {
			logger.error("Error on reading "+GCUBE_RELEASES_ENDPOINT_NAME, e);
			throw new Exception("An error occurred on contacting Gcube Release DB");
		}
	
	}
	
	/**
	 * Gets the single instance of EntityManagerFactoryCreator.
	 *
	 * @param rootScope the root scope
	 * @return single instance of EntityManagerFactoryCreator
	 * @throws Exception the exception
	 */
	public static synchronized EntityManagerFactoryCreator getInstance(String rootScope) throws Exception{
		
		if(INSTANCE==null)
			INSTANCE = new EntityManagerFactoryCreator(rootScope);

		return INSTANCE;
	}
	
	/**
	 * USE ONLY IN TEST MODE
	 * @param rootScope
	 * @return
	 * @throws Exception
	 */
	public static synchronized EntityManagerFactoryCreator getInstanceTestMode(String rootScope) throws Exception{
		
		if(INSTANCE==null){
			try{
				INSTANCE = new EntityManagerFactoryCreator(rootScope);
				factoryGcubeReleases = createEntityManagerFactoryTestMode();
			}catch(Exception e){
				logger.warn("EntityManagerFactory created in TEST MODE");
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

		if (factoryGcubeReleases == null){
			logger.info("EntityManagerFactory is null, creating..");
			factoryGcubeReleases = createEntityManagerFactory();
		}
		else if(!factoryGcubeReleases.isOpen()){
			logger.info("EntityManagerFactory is not open, closing and creating..");
			factoryGcubeReleases.close();
			factoryGcubeReleases = createEntityManagerFactory();
		}
		logger.info("Returning EntityManagerFactory");
		return factoryGcubeReleases;
	}
	
	/**
	 * Creates the entity manager factory.
	 *
	 * @param dbName the db name
	 * @return the entity manager factory
	 */
	private static EntityManagerFactory createEntityManagerFactory() {

		Map<String, String> properties = new HashMap<String, String>();
		EntityManagerFactory emf = null;
		try {
			properties.put("javax.persistence.jdbc.url", "jdbc:postgresql://"+dbParameters.getUrl());
			properties.put("javax.persistence.jdbc.user", dbParameters.getUser());
			properties.put("javax.persistence.jdbc.password", dbParameters.getPassword());
			emf = Persistence.createEntityManagerFactory(PERSISTENCE_DB_GCUBE_RELEASES, properties);
		} catch (Exception e) {
			logger.error("error on get createEntityManagerFactory " + e, e);
		}
		// emf = Persistence.createEntityManagerFactory("jpablogPUnit");
//		logger.info("created entity manager factory on: "+ properties.get(JAVAX_PERSISTENCE_JDBC_URL));
		return emf;
	}
	
	private static EntityManagerFactory createEntityManagerFactoryTestMode(){
		
		Map<String, String> properties = new HashMap<String, String>();
		EntityManagerFactory emf = null;
		try {
			
			properties.put("javax.persistence.jdbc.url", "jdbc:postgresql://dev.d4science.org:5432/gcube-releases");
			properties.put("javax.persistence.jdbc.user", "postgres");
			properties.put("javax.persistence.jdbc.password", "8gridsphere1");
			
			
//			properties.put("javax.persistence.jdbc.url", "jdbc:postgresql://node7.d.d4science.research-infrastructures.eu:5432/gcubereleases");
//			properties.put("javax.persistence.jdbc.user", "francesco");
//			properties.put("javax.persistence.jdbc.password", "testgcube");
			
//			properties.put("eclipselink.jdbc.connection_pool.default.wait", "6000");
//			properties.put("eclipselink.connection-pool.default.initial", "2");
//			properties.put("eclipselink.connection-pool.node7.d.d4science.research-infrastructures.eu.min", "60");
//			properties.put("eclipselink.connection-pool.node7.d.d4science.research-infrastructures.eu.max", "60");
//			properties.put("eclipselink.jdbc.connection_pool.default.max", "60");
//			properties.put("eclipselink.jdbc.connection_pool.default.min", "60");
//			<property name="eclipselink.jdbc.connection_pool.read.shared" value="true"/>
//			<property name="eclipselink.jdbc.connection_pool.default.max" value="60"/>
//			<property name="eclipselink.jdbc.connection_pool.default.min" value="60"/>
			emf = Persistence.createEntityManagerFactory(PERSISTENCE_DB_GCUBE_RELEASES, properties);
		} catch (Exception e) {
			logger.error("error on get createEntityManagerFactory " + e, e);
		}
		// emf = Persistence.createEntityManagerFactory("jpablogPUnit");
//		logger.info("created entity manager factory on: "+ properties.get(JAVAX_PERSISTENCE_JDBC_URL));
		return emf;
	}

}
