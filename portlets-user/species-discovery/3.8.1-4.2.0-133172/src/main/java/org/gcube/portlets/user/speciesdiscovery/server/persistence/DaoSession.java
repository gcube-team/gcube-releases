package org.gcube.portlets.user.speciesdiscovery.server.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portlets.user.speciesdiscovery.server.asl.SessionUtil;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.OccurrenceJobPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.OccurrenceRowPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.ResultRowPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.TaxonRowPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.TaxonomyJobPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.TaxonomyRowPersistence;
import org.gcube.portlets.user.speciesdiscovery.shared.DatabaseServiceException;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class DaoSession {
	
	protected static final String PROPERTY_CATALINA_HOME = "catalina.home";
	protected static final String CATALINA_HOME = "CATALINA_HOME";
	protected static final String PERSISTENCE_H2DBSPECIES_H2 = "persistence/h2dbspecies/h2";
	public static Logger logger = Logger.getLogger(DaoSession.class);
	private static final String JDBCDRIVER = "jdbc:h2:";
	
	
	public static String getUsernameScopeValue(String username, String scope){
		
		scope = scope.replaceAll("/","");
		username = username.replaceAll("\\.","");

		return username+scope;
	}
	
	
	

	/**
     * 
     * @return $CATALINA_HOME
	 * @throws Exception 
     */
    public static String getTomcatFolder(){
    	
    	String catalinaHome = System.getenv(CATALINA_HOME) != null ? System.getenv(CATALINA_HOME) : System.getProperty(PROPERTY_CATALINA_HOME);
    	
    	if(catalinaHome == null || catalinaHome.isEmpty())
    		logger.error("CATALINA_HOME ENVIROMENT NOT FOUND -  RETURNED / PATH");
    	
    	
    	return catalinaHome.endsWith("/") ? catalinaHome : catalinaHome+"/";
    }
    
    
    public static String getConnectionUrl(ASLSession session){
    	
		String username = session.getUsername();
		String scope = session.getScopeName();
    	return JDBCDRIVER +getTomcatFolder()+PERSISTENCE_H2DBSPECIES_H2+getUsernameScopeValue(username,scope)+";create=true";
    }


	public static void initSessionDaoObjects(ASLSession session) throws DatabaseServiceException{

		try{
			getTaxonDAO(session);
			getResultRowDAO(session);
			getTaxonomyDAO(session);
			getOccurrencesJobDAO(session);
			getTaxonomyJobDAO(session);
		}catch (Exception e) {
			logger.error("An error occurred on init DB: ", e);
			e.printStackTrace();
			throw new DatabaseServiceException("initializing species database");
		}
	}

	
	//MODIFIED**************************************************************************************+
	
	
	
	public static EntityManager newEntityManager(ASLSession session){
		
		return getEntityManagerFactory(session).createEntityManager();
	}
	
	public static EntityManagerFactory getEntityManagerFactory(ASLSession session){
		

		EntityManagerFactory factory = SessionUtil.getEntityManagerFactory(session);
		
		if(factory==null){
			factory = createEntityManagerFactory(session);
			SessionUtil.setEntityManagerFactory(session, factory);
		}
		
		return factory;
	}

	public static OccurrenceJobPersistence getOccurrencesJobDAO(ASLSession session) throws Exception
	{

		OccurrenceJobPersistence occurrencesJobDao = SessionUtil.getCurrentDaoOccurrencesJob(session);
		
		if(occurrencesJobDao==null){
			occurrencesJobDao = initOccurrencesJobsDao(session);
			SessionUtil.setCurrentEJBOccurrencesJob(session, occurrencesJobDao);
		}
	
		return occurrencesJobDao;
	}
	


	public static TaxonomyJobPersistence getTaxonomyJobDAO(ASLSession session) throws Exception
	{

		TaxonomyJobPersistence taxonomyJobPeristence = SessionUtil.getCurrentDAOTaxonomyJob(session);
		
		if(taxonomyJobPeristence==null){
			taxonomyJobPeristence = initTaxonomyJobsDao(session);
			SessionUtil.setCurrentDAOTaxonomyJob(session, taxonomyJobPeristence);
		}

		return taxonomyJobPeristence;
	}
	
	public static EntityManagerFactory createEntityManagerFactory(ASLSession session){
		
		Map<String,String> properties = new HashMap<String, String>();
//		properties.put("javax.persistence.jdbc.driver", jdbcDriverH2);
		
		try{
			properties.put("javax.persistence.jdbc.url", getConnectionUrl(session));
		}
		catch (Exception e) {
			logger.error("error on get connection url "+e, e);
			String username = session.getUsername();
			String scope = session.getScopeName();
			properties.put("javax.persistence.jdbc.url", JDBCDRIVER+getTomcatFolder()+"/temp/h2dbspecies/h2"+getUsernameScopeValue(username,scope)+";create=true");
		}
		//emf = Persistence.createEntityManagerFactory("jpablogPUnit");
		return Persistence.createEntityManagerFactory("SPD_PERSISTENCE_FACTORY",properties);
		
	}
	
	
	public static OccurrenceJobPersistence initOccurrencesJobsDao(ASLSession session) throws Exception
	{
		
		String username = session.getUsername();
		String scope = session.getScopeName();
		return createOccurrencesJobDao(username, scope, session);
		
	}
	

	public static TaxonomyJobPersistence initTaxonomyJobsDao(ASLSession session) throws Exception{
		TaxonomyJobPersistence occurrenceJobPersistence = new TaxonomyJobPersistence(getEntityManagerFactory(session));
		SessionUtil.setCurrentDAOTaxonomyJob(session, occurrenceJobPersistence);
		return occurrenceJobPersistence;

	}
	
	public static OccurrenceJobPersistence createOccurrencesJobDao(String username, String scope, ASLSession session) throws Exception{
		
		OccurrenceJobPersistence occurrenceJobPersistence = new OccurrenceJobPersistence(getEntityManagerFactory(session));
		SessionUtil.setCurrentEJBOccurrencesJob(session, occurrenceJobPersistence);
	
//		logger.trace("ResultRow Dao IS READY AT CONNECTION URL: " + jdbcDriver + " table name "+ resultRowDAO.getTableInfo().getTableName());
		
		return occurrenceJobPersistence;
	}
	
	public static ResultRowPersistence initResultRowDao(ASLSession session) throws Exception
	{
		
		ResultRowPersistence resultRowPersistence = new ResultRowPersistence(getEntityManagerFactory(session));
		SessionUtil.setCurrentEJBResultRow(session, resultRowPersistence);
	
//		logger.trace("ResultRow Dao IS READY AT CONNECTION URL: " + jdbcDriver + " table name "+ resultRowDAO.getTableInfo().getTableName());
		
		return resultRowPersistence;

	}
	
	public static OccurrenceRowPersistence getOccurrenceDAO(ASLSession session) throws Exception
	{

		OccurrenceRowPersistence occurrenceEJB = SessionUtil.getCurrentEJBOccurrence(session);
		
		if(occurrenceEJB==null){
			occurrenceEJB = initOccurrenceDao(session);
			SessionUtil.setCurrentEJBOccurrence(session, occurrenceEJB);
		}

		return occurrenceEJB;
	}

	public static TaxonRowPersistence initTaxonDao(ASLSession session) throws Exception
	{
	
		TaxonRowPersistence taxonRowPersistence = new TaxonRowPersistence(getEntityManagerFactory(session));
		SessionUtil.setCurrentEJBTaxonRow(session, taxonRowPersistence);
	
//		logger.trace("ResultRow Dao IS READY AT CONNECTION URL: " + jdbcDriver + " table name "+ resultRowDAO.getTableInfo().getTableName());
		
		return taxonRowPersistence;

	}
	
	private static OccurrenceRowPersistence initOccurrenceDao(ASLSession session) throws Exception {

		OccurrenceRowPersistence occurrenceRowPersistence = new OccurrenceRowPersistence(getEntityManagerFactory(session));
		SessionUtil.setCurrentEJBOccurrence(session, occurrenceRowPersistence);
	
//		logger.trace("ResultRow Dao IS READY AT CONNECTION URL: " + jdbcDriver + " table name "+ resultRowDAO.getTableInfo().getTableName());
		
		return occurrenceRowPersistence;
		
	}
	
	public static TaxonRowPersistence getTaxonDAO(ASLSession session) throws Exception
	{

		TaxonRowPersistence taxonDao = SessionUtil.getCurrentEJBTaxonRow(session);
		
		if(taxonDao==null){
			taxonDao = initTaxonDao(session);
			SessionUtil.setCurrentEJBTaxonRow(session, taxonDao);
		}
		
//		System.out.println("Return taxon Dao : " + taxonDao + " table name: " + taxonDao.getTableConfig().getTableName());
//		System.out.println("session id: " + session.getExternalSessionID() + " sessione username: " +session.getUsername() + " session scope: "+session.getScopeName());

		return taxonDao;
	}
	
	public static TaxonomyRowPersistence getTaxonomyDAO(ASLSession session) throws Exception
	{
		logger.trace("In getTaxonomyDAO...");
		
		TaxonomyRowPersistence taxonItemDAO = SessionUtil.getCurrentEJBTaxonomyItem(session);
		
		if(taxonItemDAO==null){
			taxonItemDAO = initTaxonomyItemDao(session, "taxonomyrow");
			SessionUtil.setCurrentEJBTaxonomyItem(session, taxonItemDAO);
		}
		
//		logger.trace("Return taxonomy row Dao : " + taxonItemDAO + " table name: " + taxonItemDAO.getTableConfig().getTableName());
//		logger.trace("session id: " + session.getExternalSessionID() + " sessione username: " +session.getUsername() + " session scope: "+session.getScopeName());

		return taxonItemDAO;
	}
	
	private static TaxonomyRowPersistence initTaxonomyItemDao(ASLSession session, String tableName) throws Exception{

		TaxonomyRowPersistence taxonomyRP = new TaxonomyRowPersistence(getEntityManagerFactory(session));
		SessionUtil.setCurrentEJBTaxonomyItem(session, taxonomyRP);

		return taxonomyRP;
	}
	
	public static ResultRowPersistence getResultRowDAO(ASLSession session) throws Exception
	{	
		
		logger.trace("In getResultRowDAO...");
		
		ResultRowPersistence resultRowEJB = SessionUtil.getCurrentEJBResultRow(session);
		
		if(resultRowEJB==null){
			resultRowEJB = initResultRowDao(session);
			SessionUtil.setCurrentEJBResultRow(session, resultRowEJB);
		}
		
//		logger.trace("Return result row Dao : " + resultRowEJB + " table name: " + resultRowDAO.getTableConfig().getTableName());
//		logger.trace("session id: " + session.getExternalSessionID() + " sessione username: " +session.getUsername() + " session scope: "+session.getScopeName());

		return resultRowEJB;
	}
	
	
	

}
