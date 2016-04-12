/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.asl;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.OccurrenceJobPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.OccurrenceRowPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.ResultRowPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.TaxonRowPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.TaxonomyJobPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.TaxonomyRowPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.service.SpeciesService;
import org.gcube.portlets.user.speciesdiscovery.server.session.FetchingSession;
import org.gcube.portlets.user.speciesdiscovery.shared.FetchingElement;
import org.gcube.portlets.user.speciesdiscovery.shared.Occurrence;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyRow;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ClusterCommonNameDataSourceForResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.cluster.ClusterCommonNameDataSourceForTaxonomyRow;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class SessionUtil {
	
	public static final String SPECIES_SERVICE = "SPECIES_SERVICE";
	public static final String SEARCH_SESSION = "SEARCH_SESSION";
	public static final String TAXONOMY_OCCURRENCE = "TAXONOMY_OCCURRENCE";
	public static final String CONNECTION_SOURCE = "CONNECTION_SOURCE";
	public static final String USERNAME_ATTRIBUTE = "username";
	public static final String ENTITY_FACTORY = "ENTITY_FACTORY";
	public static final String EJB_RESULT_ROW = "EJB_RESULT_ROW";
	public static final String EJB_TAXON_ROW = "EJB_TAXON_ROW";
	public static final String EJB_OCCURENCE = "EJB_OCCURENCE";
	public static final String EJB_TAXONOMY = "EJB_TAXONOMY";
	public static final String EJB_OCCURRENCES_JOB = "EJB_OCCURRENCES_JOB";
	public static final String EJB_TAXONOMY_JOB = "EJB_TAXONOMY_JOB";
	public static final String CURRENT_SPD_QUERY = "CURRENT_SPD_QUERY";
	private static final String CLUSTER_COMMONNAME_FOR_RESULTROW = "CLUSTER_COMMONNAME_FOR_RESULTROW";
	private static final String CLUSTER_COMMONNAME_FOR_TAXONOMYROW = "CLUSTER_COMMONNAME_FOR_TAXONOMYROW";
	private static final String CACHE_HASH_MAP_CHILDREN_TAXONOMY = "CACHE_HASH_MAP_CHILDREN_TAXONOMY";
	private static final String CACHE_HASH_MAP_SYNONYMS_TAXONOMY = "CACHE_HASH_MAP_SYNONYMS_TAXONOMY";
	private static final String CACHE_HASH_MAP_TAXONOMYID_TAXONOMY = "CACHE_HASH_MAP_TAXONOMYID_TAXONOMY";
	
	protected static Logger logger = Logger.getLogger(SessionUtil.class);
	
	
	public static EntityManagerFactory getEntityManagerFactory(ASLSession session) {
		return (EntityManagerFactory) session.getAttribute(ENTITY_FACTORY);
	}

	public static void setEntityManagerFactory(ASLSession session, EntityManagerFactory factory) {
		 session.setAttribute(ENTITY_FACTORY, factory);
	}
	
	public static SpeciesService getService(ASLSession session) throws Exception
	{
		SpeciesService service = (SpeciesService) session.getAttribute(SPECIES_SERVICE);
		if (service==null) {
			logger.trace("Service not found, creating it");
			String scope = session.getScope();
			service = new SpeciesService(scope, session);
			session.setAttribute(SPECIES_SERVICE, service);
		}
		
		return service;
	}
	
	@SuppressWarnings("unchecked")
	public static FetchingSession<? extends FetchingElement> getCurrentSearchSession(ASLSession session)
	{
		return (FetchingSession<? extends FetchingElement>) session.getAttribute(SEARCH_SESSION);
	}
	
	public static void setCurrentSearchSession(ASLSession session, FetchingSession<? extends FetchingElement> searchSession){
		session.setAttribute(SEARCH_SESSION, searchSession);
	}
	
	
	@SuppressWarnings("unchecked")
	public static FetchingSession<Occurrence> getCurrentOccurrenceSession(ASLSession session){
		return (FetchingSession<Occurrence>) session.getAttribute(TAXONOMY_OCCURRENCE);
	}
	
	
	public static void setCurrentOccurrenceSession(ASLSession session, FetchingSession<Occurrence> searchSession){
		session.setAttribute(TAXONOMY_OCCURRENCE, searchSession);
	}
	
//	public static void setCurrentTaxonomyChildrenSession(ASLSession session, FetchingSession<TaxonomyRow> searchSession)
//	{
//		session.setAttribute(TAXONOMY_CHILD, searchSession);
//	}

	public static ASLSession getAslSession(HttpSession httpSession)
	{
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute(USERNAME_ATTRIBUTE);

		if (user == null) {

			//for test only
			user = "test.user";
//			user = "lucio.lelii";
//			user = "pasquale.pagano";
//			user = "francesco.mangiacrapa";
			
			String scope = "/gcube/devsec"; //Development
//			String scope = "/d4science.research-infrastructures.eu/gCubeApps/BiodiversityResearchEnvironment"; //Production
			
			httpSession.setAttribute(USERNAME_ATTRIBUTE, user);
			ASLSession session = SessionManager.getInstance().getASLSession(sessionID, user);
			session.setScope(scope);

			logger.warn("TaxonomySearchServiceImpl STARTING IN TEST MODE - NO USER FOUND");
			logger.warn("Created fake Asl session for user "+user + " with scope "+scope);
//			session.setScope("/gcube/devsec");
			
			return session;
		}

		return SessionManager.getInstance().getASLSession(sessionID, user);
	}

	//MODIFIED******************************************************************************************
	

	public static void setCurrentEJBResultRow(ASLSession session, ResultRowPersistence resultRowPersistence) {
		session.setAttribute(EJB_RESULT_ROW, resultRowPersistence);
	}
	
	public static ResultRowPersistence getCurrentEJBResultRow(ASLSession session) {
		return (ResultRowPersistence) session.getAttribute(EJB_RESULT_ROW);
	}

	public static void setCurrentEJBTaxonRow(ASLSession session,TaxonRowPersistence taxonRowPersistence) {
		session.setAttribute(EJB_TAXON_ROW, taxonRowPersistence);
	}
	
	public static TaxonRowPersistence getCurrentEJBTaxonRow(ASLSession session) {
		return (TaxonRowPersistence) session.getAttribute(EJB_TAXON_ROW);
	}
	
	public static void setCurrentEJBOccurrence(ASLSession session, OccurrenceRowPersistence occurrenceRowPersistence) {
		session.setAttribute(EJB_OCCURENCE, occurrenceRowPersistence);
	}
	
	public static OccurrenceRowPersistence getCurrentEJBOccurrence(ASLSession session) {
		return  (OccurrenceRowPersistence) session.getAttribute(EJB_OCCURENCE);
	}
	
	@SuppressWarnings("unchecked")
	public static TaxonomyRowPersistence getCurrentEJBTaxonomyItem(ASLSession session) {
		return  (TaxonomyRowPersistence) session.getAttribute(EJB_TAXONOMY);
	}
	
	public static void setCurrentEJBTaxonomyItem(ASLSession session, TaxonomyRowPersistence taxonomyItemDAO) {
		session.setAttribute(EJB_TAXONOMY, taxonomyItemDAO);
		
	}

	public static void setCurrentEJBOccurrencesJob(ASLSession session, OccurrenceJobPersistence occurrenceJobPersistence) {
		session.setAttribute(EJB_OCCURRENCES_JOB, occurrenceJobPersistence);
		
	}
	
	@SuppressWarnings("unchecked")
	public static OccurrenceJobPersistence getCurrentDaoOccurrencesJob(ASLSession session) {
		return  (OccurrenceJobPersistence) session.getAttribute(EJB_OCCURRENCES_JOB);
	}
	
	@SuppressWarnings("unchecked")
	public static TaxonomyJobPersistence getCurrentDAOTaxonomyJob(ASLSession session) {
		return  (TaxonomyJobPersistence) session.getAttribute(EJB_TAXONOMY_JOB);
	}

	public static void setCurrentDAOTaxonomyJob(ASLSession session, TaxonomyJobPersistence taxonomyJobPeristence) {
		session.setAttribute(EJB_TAXONOMY_JOB, taxonomyJobPeristence);
	}

	/**
	 * @param aslSession
	 * @param lastQuery
	 */
	public static void setCurrentQuery(ASLSession aslSession, String lastQuery) {
		logger.trace("Last query in session: "+lastQuery);
		aslSession.setAttribute(CURRENT_SPD_QUERY, lastQuery);
		
	}
	

	/**
	 * 
	 * @param aslSession
	 */
	public static String getCurrentQuery(ASLSession aslSession) {
		return (String) aslSession.getAttribute(CURRENT_SPD_QUERY);
		
	}

	/**
	 * @param hashClusterCommonNameDataSource
	 */
	public static void setCurrentClusterCommonNameForResultRow(ASLSession session, HashMap<String, ClusterCommonNameDataSourceForResultRow> hashClusterCommonNameDataSource) {
		session.setAttribute(CLUSTER_COMMONNAME_FOR_RESULTROW, hashClusterCommonNameDataSource);
		
	}
	
	/**
	 * @param hashClusterCommonNameDataSource
	 * @return 
	 */
	public static HashMap<String, ClusterCommonNameDataSourceForResultRow> getCurrentClusterCommonNameForResultRow(ASLSession session) {
		return (HashMap<String, ClusterCommonNameDataSourceForResultRow>) session.getAttribute(CLUSTER_COMMONNAME_FOR_RESULTROW);
		
	}
	
	
	/**
	 * @param hashClusterCommonNameDataSource
	 */
	public static void setCurrentClusterCommonNameForTaxonomyRow(ASLSession session, HashMap<String, ClusterCommonNameDataSourceForTaxonomyRow> hashClusterCommonNameDataSource) {
		session.setAttribute(CLUSTER_COMMONNAME_FOR_TAXONOMYROW, hashClusterCommonNameDataSource);
		
	}
	
	/**
	 * @param hashClusterCommonNameDataSource
	 * @return 
	 */
	public static HashMap<String, ClusterCommonNameDataSourceForTaxonomyRow> getCurrentClusterCommonNameForTaxonomyRow(ASLSession session) {
		return (HashMap<String, ClusterCommonNameDataSourceForTaxonomyRow>) session.getAttribute(CLUSTER_COMMONNAME_FOR_TAXONOMYROW);
		
	}

	/**
	 * @return 
	 * 
	 */
	public static HashMap<String, TaxonomyRow> getHashMapChildrenTaxonomyCache(ASLSession session) {
		return (HashMap<String, TaxonomyRow>) session.getAttribute(CACHE_HASH_MAP_CHILDREN_TAXONOMY);
		
	}
	
	/**
	 * @return 
	 * 
	 */
	public static void setHashMapChildrenTaxonomyCache(ASLSession session, Map<String, TaxonomyRow> list) {
		session.setAttribute(CACHE_HASH_MAP_CHILDREN_TAXONOMY, list);
		
	}
	
	
	/**
	 * @return 
	 * 
	 */
	public static HashMap<String, TaxonomyRow> getHashMapSynonymsTaxonomyCache(ASLSession session) {
		return (HashMap<String, TaxonomyRow>) session.getAttribute(CACHE_HASH_MAP_SYNONYMS_TAXONOMY);
		
	}
	
	/**
	 * @return 
	 * 
	 */
	public static void setHashMapSynonymsTaxonomyCache(ASLSession session, Map<String, TaxonomyRow> list) {
		session.setAttribute(CACHE_HASH_MAP_SYNONYMS_TAXONOMY, list);
		
	}
	
	
	/**
	 * @return 
	 * 
	 */
	public static HashMap<String, TaxonomyRow> getHashMapTaxonomyByIdsCache(ASLSession session) {
		return (HashMap<String, TaxonomyRow>) session.getAttribute(CACHE_HASH_MAP_TAXONOMYID_TAXONOMY);
		
	}
	
	/**
	 * @return 
	 * 
	 */
	public static void setHashMapTaxonomyByIdsCache(ASLSession session, Map<String, TaxonomyRow> list) {
		session.setAttribute(CACHE_HASH_MAP_TAXONOMYID_TAXONOMY, list);
		
	}
}
