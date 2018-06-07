package org.gcube.portlets.user.geoexplorer.server.service.dao;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.portlets.user.geoexplorer.server.util.HttpSessionUtil;

public class DaoManager {
	
	/**
	 * 
	 */
	protected static final String PERSISTENCE_FACTORY = "GEOEXPLORER_PERSISTENCE_FACTORY";
	private static final String JAVAX_PERSISTENCE_JDBC_URL = "javax.persistence.jdbc.url";
	protected static final String PROPERTY_CATALINA_HOME = "catalina.home";
	protected static final String CATALINA_HOME = "CATALINA_HOME";
	protected static final String TEMP_H2DBGEOEXPLORER_H2 = "temp/h2dbgeoxplorer";
	protected static final String TMP_H2DBGEOEXPLORER_H2 = "tmp/h2dbgeoxplorer";
	private static final String JDBCDRIVER = "jdbc:h2:";
	public static Logger logger = Logger.getLogger(DaoManager.class);
	
//	public static EntityManagerFactory factoryGeoParameters;
	
	public static final String GEOPARAMETERS = "GEOPARAMETERS";
	
	public static EntityManagerFactory createEntityManagerFactory(HttpSession session, String scope){
		
		Map<String,String> properties = new HashMap<String, String>();
//		properties.put("javax.persistence.jdbc.driver", jdbcDriverH2);
		try{
			//SESSION ID IS THE NAME OF DATABASE
			properties.put(JAVAX_PERSISTENCE_JDBC_URL, getConnectionUrl(session.getId(), scope));
		}
		catch (Exception e) {
			logger.error("error on get connection url "+e, e);
			properties.put(JAVAX_PERSISTENCE_JDBC_URL, JDBCDRIVER+TMP_H2DBGEOEXPLORER_H2+"/"+scope+"/"+session.getId()+";create=true");
		}
		
		logger.info("Creating new EntityManagerFactory with id: "+JDBCDRIVER+TMP_H2DBGEOEXPLORER_H2+"/"+scope+"/"+session.getId());
		//emf = Persistence.createEntityManagerFactory("jpablogPUnit");
		return Persistence.createEntityManagerFactory(PERSISTENCE_FACTORY,properties);
		
	}
	
    public static String getStingConnectionToDatabase(String dbName, String scope){
    	scope = scope.replaceAll("/", "");
    	return getTomcatFolder()+TEMP_H2DBGEOEXPLORER_H2+"/"+scope+"/"+dbName;
    }
    
	/**
     * 
     * @return $CATALINA_HOME
	 * @throws Exception 
     */
    public static String getTomcatFolder(){
    	
    	String catalinaHome = System.getenv(CATALINA_HOME) != null ? System.getenv(CATALINA_HOME) : System.getProperty(PROPERTY_CATALINA_HOME);
    	
    	if(catalinaHome == null || catalinaHome.isEmpty()){
    		logger.error("CATALINA_HOME ENVIROMENT NOT FOUND -  RETURNED / PATH");
    		return "/";
    	}
    	
    	return catalinaHome.endsWith("/") ? catalinaHome : catalinaHome+"/";
    }
    
	public static EntityManagerFactory getEntityManagerFactory(HttpSession session, String scope){
		
		EntityManagerFactory factory;
		
		//TODO //THIS IS FOR TESTING... REMOVE********************
//				if(session==null)
//					return getEntityManagerFactoryForGeoParameters(scope);
				// REMOVE********************
	
		/*
		if(HttpSessionUtil.isScopeChanged(session, scope)){
			factory = createEntityManagerFactory(session, scope);
			HttpSessionUtil.setEntityManagerFactory(session, factory);
		}
		else
			factory = HttpSessionUtil.getEntityManagerFactory(session);
		*/
				
		factory = HttpSessionUtil.getEntityManagerFactory(session, scope);
				
		if(factory==null){
			factory = createEntityManagerFactory(session, scope);
			HttpSessionUtil.setEntityManagerFactory(session, factory, scope);
		}
		return factory;
	}
	

	
	//GEOPARAMETERS
	public static EntityManagerFactory getEntityManagerFactoryForGeoParameters(String scope, HttpSession httpSession){
		
		EntityManagerFactory factory = HttpSessionUtil.getEntityManagerFactoryForGeoParameters(httpSession, scope);
		
		if(factory==null){
			if(scope==null){
				logger.warn("getEntityManagerFactoryForGeoParameters scope is null, setting as empty");
				scope = "";
			}
			logger.info("instancing new EntityManagerFactory factoryGeoParameters with scope "+scope+ " and returning");
			factory = createEntityManagerFactory(GEOPARAMETERS, scope);
			HttpSessionUtil.setEntityManagerFactoryForGeoParameters(httpSession, factory, scope);
			return factory;
		}else{
			logger.info("EntityManagerFactory factoryGeoParameters already exists wiht scope "+scope+", returning");
			return factory;
		}
		
	}
	
	/**
	 * Used for GeoParameters
	 * @param dbName
	 * @param scope
	 * @return
	 */
	public static EntityManagerFactory createEntityManagerFactory(String dbName, String scope){
		
		Map<String,String> properties = new HashMap<String, String>();
//		properties.put("javax.persistence.jdbc.driver", jdbcDriverH2);
		
		try{
			properties.put(JAVAX_PERSISTENCE_JDBC_URL, getConnectionUrl(dbName, scope));
		}
		catch (Exception e) {
			logger.error("error on get connection url "+e, e);
			properties.put(JAVAX_PERSISTENCE_JDBC_URL, JDBCDRIVER+TMP_H2DBGEOEXPLORER_H2+"/"+scope+"/"+dbName+";create=true");
		}
		logger.info("Creating new EntityManagerFactory with id: "+JDBCDRIVER+TMP_H2DBGEOEXPLORER_H2+"/"+scope+"/"+dbName);
		//emf = Persistence.createEntityManagerFactory("jpablogPUnit");
		return Persistence.createEntityManagerFactory(PERSISTENCE_FACTORY,properties);
		
	}
	
    public static String getConnectionUrl(String dbName, String scope){

    	return JDBCDRIVER + getStingConnectionToDatabase(dbName, scope)+";create=true";
    }

}
