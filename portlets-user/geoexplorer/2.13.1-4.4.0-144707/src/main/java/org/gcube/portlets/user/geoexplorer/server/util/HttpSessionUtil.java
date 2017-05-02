/**
 *
 */
package org.gcube.portlets.user.geoexplorer.server.util;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;

import org.gcube.portlets.user.geoexplorer.client.Constants;
import org.gcube.portlets.user.geoexplorer.server.service.GeonetworkInstance;
import org.gcube.portlets.user.geoexplorer.server.service.GisPublisherSearch;


/**
 * The Class HttpSessionUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 24, 2016
 */
public class HttpSessionUtil {

	public static final String GEOINSTANCE_ATTRIBUTE = "GEOINSTANCE_ATTRIBUTE";
	public static final String LOGGER_GEOEXPLORER = "LOGGER_GEOEXPLORER";
	public static final String GEOEXPLORER_SEARCH_SESSION = "GEOEXPLORER_SEARCH_SESSION";
	public static final String GISPUBLISHER_SEARCH_SESSION = "GISPUBLISHER_SEARCH_SESSION";
	public static final String GISPUBLISHER_SCOPE_SESSION = "GISPUBLISHER_SCOPE_SESSION";
	public static boolean withoutPortal = false;

	public static final String ENTITY_FACTORY_GEOXPLORER = "ENTITY_FACTORY_GEOXPLORER";
	public static final String ENTITY_FACTORY_GEOPARAMETERS = "ENTITY_FACTORY_GEOPARAMETERS";

	public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(HttpSessionUtil.class);


	public static HashMap<String, GeonetworkInstance> hashGeonetworkInstance = new HashMap<String, GeonetworkInstance>();
	public static HashMap<String, EntityManagerFactory> hashEntityManagerFactory = new HashMap<String, EntityManagerFactory>();
	public static HashMap<String, EntityManagerFactory> hashEntityManagerFactoryGeoParameters = new HashMap<String, EntityManagerFactory>();
	public static HashMap<String, GisPublisherSearch> hashGisPublisherSearch = new HashMap<String, GisPublisherSearch>();
	public static HashMap<String, String> hashScope = new HashMap<String, String>();


	/**
	 * Gets the geonetwork instance.
	 *
	 * @param httpSession the http session
	 * @param scope the scope
	 * @return the geonetwork instance
	 * @throws Exception the exception
	 */
	public static GeonetworkInstance getGeonetworkInstance(HttpSession httpSession, String scope) throws Exception{
		scope = replaceAll(scope,"/");
		String id = httpSession.getId()+GEOINSTANCE_ATTRIBUTE+scope;
		GeonetworkInstance hashGeoInstance = hashGeonetworkInstance.get(id);
		logger.debug("GeonetworkInstance in hash with id "+id+" is null? "+(hashGeoInstance==null));
		return hashGeoInstance;
	}

	/**
	 * Close entity manager factory.
	 *
	 * @param session the session
	 * @param scope the scope
	 * @return the entity manager factory
	 */
	public static EntityManagerFactory closeEntityManagerFactory(HttpSession session, String scope){
		logger.trace("Closing EntityManagerFactory session id "+session.getId() +" scope: "+scope);

		if(hashEntityManagerFactory !=null){
			EntityManagerFactory factory = getEntityManagerFactory(session, scope);
			if(factory!=null){
				logger.trace("EntityManagerFactory is not null, closing");
				factory.close();
			}

			return factory;
		}

		return null;

	}

	/**
	 * Close entity manager factory for geo parameters.
	 *
	 * @param session the session
	 * @param scope the scope
	 * @return the entity manager factory
	 */
	public static EntityManagerFactory closeEntityManagerFactoryForGeoParameters(HttpSession session, String scope){
		logger.trace("Closing ManagerFactoryForGeoParameters session id "+session.getId() +" scope: "+scope);

		if(hashEntityManagerFactoryGeoParameters!=null){
			EntityManagerFactory factory = getEntityManagerFactoryForGeoParameters(session, scope);
			if(factory!=null){
				logger.trace("EntityManagerFactoryForGeoParameters is not null, closing");
				factory.close();
			}

			return factory;
		}

		return null;

	}

	/**
	 * Reset all hashs for scope.
	 *
	 * @param session the session
	 * @param scope the scope
	 */
	public static void resetAllHashsForScope(HttpSession session, String scope){

		logger.trace("resetting all cache..");

		if(hashGisPublisherSearch !=null){
			hashGisPublisherSearch.put(session.getId()+GISPUBLISHER_SEARCH_SESSION+scope, null);
		}

		if(hashEntityManagerFactory !=null){
			hashEntityManagerFactory.put(session.getId()+ENTITY_FACTORY_GEOXPLORER+scope, null);
		}

		if(hashGeonetworkInstance!=null){
			hashGeonetworkInstance.put(session.getId()+GEOINSTANCE_ATTRIBUTE+scope, null);
		}

		if(hashScope!=null){
			hashScope.put(session.getId()+GISPUBLISHER_SCOPE_SESSION, null);
		}

		if(hashEntityManagerFactoryGeoParameters!=null){
			hashEntityManagerFactoryGeoParameters.put(session.getId()+ENTITY_FACTORY_GEOPARAMETERS+scope, null);
		}

		logger.trace("resetting all cache completed");

	}

	/**
	 * Sets the geonetork instance.
	 *
	 * @param httpSession the http session
	 * @param gn the gn
	 * @param scope the scope
	 * @throws Exception the exception
	 */
	public static void setGeonetorkInstance(HttpSession httpSession, GeonetworkInstance gn, String scope) throws Exception{
		scope = replaceAll(scope,"/");
		logger.debug("Setting GeonetworkInstance istance in hash with session id: "+httpSession.getId()+" as value: "+GEOINSTANCE_ATTRIBUTE+scope);
		String id = httpSession.getId()+GEOINSTANCE_ATTRIBUTE+scope;
		hashGeonetworkInstance.put(id,gn);
	}

	/**
	 * Sets the entity manager factory.
	 *
	 * @param session the session
	 * @param factory the factory
	 * @param scope the scope
	 */
	public static void setEntityManagerFactory(HttpSession session, EntityManagerFactory factory, String scope) {
		scope = replaceAll(scope,"/");
		logger.debug("Setting EntityManagerFactory istance in http session id "+session.getId()+" as value: "+ENTITY_FACTORY_GEOXPLORER+scope);
		String id = session.getId()+ENTITY_FACTORY_GEOXPLORER+scope;
		hashEntityManagerFactory.put(id, factory);
	}

	/**
	 * Gets the entity manager factory.
	 *
	 * @param session the session
	 * @param scope the scope
	 * @return the entity manager factory
	 */
	public static EntityManagerFactory getEntityManagerFactory(HttpSession session, String scope) {
		scope = replaceAll(scope,"/");
		String id = session.getId()+ENTITY_FACTORY_GEOXPLORER+scope;
		logger.trace("Getting EntityManagerFactory istance in http session id "+session.getId()+" as value: "+ENTITY_FACTORY_GEOXPLORER+scope);
		return hashEntityManagerFactory.get(id);
	}

	/**
	 * Gets the entity manager factory for geo parameters.
	 *
	 * @param httpSession the http session
	 * @param scope the scope
	 * @return the entity manager factory for geo parameters
	 */
	public static EntityManagerFactory getEntityManagerFactoryForGeoParameters(HttpSession httpSession, String scope) {
		scope = replaceAll(scope,"/");
		String id = httpSession.getId()+ENTITY_FACTORY_GEOPARAMETERS+scope;
		return hashEntityManagerFactoryGeoParameters.get(id);
	}

	/**
	 * Sets the entity manager factory for geo parameters.
	 *
	 * @param session the session
	 * @param factory the factory
	 * @param scope the scope
	 */
	public static void setEntityManagerFactoryForGeoParameters(HttpSession session, EntityManagerFactory factory, String scope) {
		scope = replaceAll(scope,"/");
		logger.trace("Setting ForGeoParameters istance in http session id "+session.getId()+" as value: "+ENTITY_FACTORY_GEOPARAMETERS+scope);

		String id = session.getId()+ENTITY_FACTORY_GEOPARAMETERS+scope;

		hashEntityManagerFactoryGeoParameters.put(id, factory);
	}

	/**
	 * Replace all.
	 *
	 * @param input the input
	 * @param replacement the replacement
	 * @return the string
	 */
	public static String replaceAll(String input, String replacement){
		if(input==null)
			return "";

		return input.replaceAll(replacement, "");
	}


	/**
	 * Gets the gis publisher search session.
	 *
	 * @param session the session
	 * @param scope the scope
	 * @return the gis publisher search session
	 */
	@SuppressWarnings("unchecked")
	public static GisPublisherSearch getGisPublisherSearchSession(HttpSession session, String scope){
		scope = replaceAll(scope,"/");

		String id = session.getId()+GISPUBLISHER_SEARCH_SESSION+scope;
		logger.trace("Getting GisPublisherSearch instance with id "+id + ", scope is: "+scope + ", http session id: "+session.getId());
		return hashGisPublisherSearch.get(id);
	}

	/**
	 * Sets the gis publisher search session.
	 *
	 * @param search the search
	 * @param session the session
	 * @param scope the scope
	 */
	public static void setGisPublisherSearchSession(GisPublisherSearch search, HttpSession session, String scope){
		scope = replaceAll(scope,"/");
		String id = session.getId()+GISPUBLISHER_SEARCH_SESSION+scope;

		logger.trace("Setting GisPublisherSearch instance in hashGisPublisherSearch, http session id: "+session.getId() + ", as id: "+id);
		hashGisPublisherSearch.put(id,search);
	}



	/**
	 * Sets the scope instance.
	 *
	 * @param httpSession the http session
	 * @param scope the scope
	 */

	public static void setScopeInstance(HttpSession httpSession, String scope) {
		String id = httpSession.getId()+GISPUBLISHER_SCOPE_SESSION;
		logger.trace("Setting: "+scope+", in hashScope with id: " +id+ ". Http session id "+httpSession.getId());
		hashScope.put(id, scope);
	}

	/**
	 * Gets the scope instance.
	 *
	 * @param httpSession the http session
	 * @return the scope instance
	 */
	public static String getScopeInstance(HttpSession httpSession) {

		if(httpSession==null){
			logger.error("HTTP SESSION is NULL, getScopeInstance returning encoded scope into code: "+Constants.defaultScope);
			return Constants.defaultScope;
		}

		String id = httpSession.getId()+GISPUBLISHER_SCOPE_SESSION;
		logger.trace("Getting scope instance with id: "+id);
		return hashScope.get(id);
	}


	/**
	 * Checks if is scope changed.
	 *
	 * @param httpSession the http session
	 * @param scope the scope
	 * @return true, if is scope changed
	 */
	public static boolean isScopeChanged(HttpSession httpSession, String scope){

		String scopeInstance = getScopeInstance(httpSession);
		logger.trace("Scope is changed: compare scopeInstance: "+scopeInstance+" and passed scope: "+scope);

		if(scope!=null && scopeInstance!=null && scopeInstance.compareTo(scope)!=0){
			logger.trace("Scope changed from "+scopeInstance+" to "+scope+ "at http session id: "+httpSession.getId());

//			removeAllAttributesFromSession(httpSession,scope);
//			logger.trace("remove all attribute from http session: "+httpSession.getId()+" completed");
			logger.trace("returing true");
			return true;
		}

		return false;
	}

//	public static void removeAllAttributesFromSession(HttpSession httpSession, String scope){
//		logger.trace("Remove all attribute from httpSession: "+httpSession.getId());
//		httpSession.removeAttribute(GISPUBLISHER_SCOPE_SESSION); //SCOPE
//		httpSession.removeAttribute(GISPUBLISHER_SEARCH_SESSION+scope);
//		httpSession.removeAttribute(GEOEXPLORER_SEARCH_SESSION+scope);
//		httpSession.removeAttribute(GEOINSTANCE_ATTRIBUTE+scope);
//		httpSession.removeAttribute(ENTITY_FACTORY_GEOXPLORER+scope);
//		httpSession.removeAttribute(ENTITY_FACTORY_GEOPARAMETERS+scope);
//	}


}
