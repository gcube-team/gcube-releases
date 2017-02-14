/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.server.session;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.td.taskswidget.client.ConstantsTdTasks;
import org.gcube.portlets.user.td.taskswidget.server.service.TaskTabularDataService;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdTaskModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 18, 2013
 *
 */
public class SessionUtil {
	
	
	
	public static final String TD_TASKS_CACHE = "Tabular_Data_Tasks_Cache";
	
	public static final String TD_TASKS_SERVICE_CLENT = "TD_TASKS_SERVICE_CLENT";

	private static final String TD_TASKS_MAP_OPERATIONS = "TD_TASKS_MAP_OPERATIONS";
	
	public static Logger logger = LoggerFactory.getLogger(SessionUtil.class);
	
	
	public static ASLSession getAslSession(HttpSession httpSession)
	{
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);

		if (user == null) {

			//for test only
			user = ConstantsTdTasks.TEST_USER;
//			user = "lucio.lelii";
//			user = "pasquale.pagano";
//			user = "francesco.mangiacrapa";
//			user = "giancarlo.panichi";
			String scope = ConstantsTdTasks.TEST_SCOPE;
//			String scope = "/d4science.research-infrastructures.eu/gCubeApps/BiodiversityResearchEnvironment"; //Production
			
			httpSession.setAttribute(ScopeHelper.USERNAME_ATTRIBUTE, user);
			ASLSession session = SessionManager.getInstance().getASLSession(sessionID, user);
			session.setScope(scope);

			logger.warn("SessionUtil STARTING IN TEST MODE - NO USER FOUND");
			logger.warn("Created fake Asl session for user "+user + " with scope "+scope);
			
			return session;
		}

		return SessionManager.getInstance().getASLSession(sessionID, user);
	}

	/**
	 * @return 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static LinkedHashMap<String, TdTaskModel> getTasksCache(ASLSession aslSession) {
		
		return (LinkedHashMap<String, TdTaskModel>) aslSession.getAttribute(TD_TASKS_CACHE);
		
	}

	/**
	 * @param aslSession
	 * @param hashTaskModel
	 */
	public static void setTasksCache(ASLSession aslSession, LinkedHashMap<String, TdTaskModel> hashTaskModel) {
		aslSession.setAttribute(TD_TASKS_CACHE, hashTaskModel);
		
	}

	/**
	 * 
	 */
	public static TaskTabularDataService getTaskTdServiceClient(ASLSession aslSession) {
		return (TaskTabularDataService) aslSession.getAttribute(TD_TASKS_SERVICE_CLENT);
		
	}

	/**
	 * @param aslSession
	 */
	public static void setTaskServiceClient(ASLSession aslSession, TaskTabularDataService service) {
		aslSession.setAttribute(TD_TASKS_SERVICE_CLENT, service);
		
	}

	/**
	 * @param aslSession
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<Long, OperationDefinition> getMapOperationDescription(ASLSession aslSession) {
		return (Map<Long, OperationDefinition>) aslSession.getAttribute(TD_TASKS_MAP_OPERATIONS);
	}
	

	public static void setMapOperationDescription(ASLSession aslSession, Map<Long, OperationDefinition> map) {
		 aslSession.setAttribute(TD_TASKS_MAP_OPERATIONS,map);
	}

}
