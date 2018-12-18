/**
 *
 */
package org.gcube.portlets.user.td.taskswidget.server.session;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.common.portal.PortalContext;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.portlets.user.td.taskswidget.server.service.TaskTabularDataService;
import org.gcube.portlets.user.td.taskswidget.shared.PortalContextInfo;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdTaskModel;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class SessionUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 30, 2017
 */
public class SessionUtil {

	public static final String TD_TASKS_CACHE = "Tabular_Data_Tasks_Cache";
	public static final String TD_TASKS_SERVICE_CLENT = "TD_TASKS_SERVICE_CLENT";
	private static final String TD_TASKS_MAP_OPERATIONS = "TD_TASKS_MAP_OPERATIONS";
	public static Logger logger = LoggerFactory.getLogger(SessionUtil.class);

	/**
	 * Gets the portal context.
	 *
	 * @param request the request
	 * @return the portal context
	 */
	public static PortalContextInfo getPortalContext(HttpServletRequest request)
	{

		PortalContext pContext = PortalContext.getConfiguration();
		//USER
		GCubeUser user = pContext.getCurrentUser(request);
		String username = user.getUsername();
		String fullName = user.getFullname();
		String email = user.getEmail();
		String avatarID = user.getUserAvatarId();
		String avatarURL = user.getUserAvatarURL();
		//SESSION
		String currentScope = pContext.getCurrentScope(request);
		String userToken = pContext.getCurrentUserToken(request);
		long currGroupId = pContext.getCurrentGroupId(request);

		return new PortalContextInfo(username, fullName, email, avatarID, avatarURL, currentScope, userToken, currGroupId);
	}

	/**
	 * Gets the portal context.
	 *
	 * @param request the request
	 * @return the portal context having minimal parameters: username, scope and user-token
	 * All other attributes are null
	 */
	public static PortalContextInfo getMinPortalContext(HttpServletRequest request)
	{

		PortalContext pContext = PortalContext.getConfiguration();
		//USER
		GCubeUser user = pContext.getCurrentUser(request);
		String username = user.getUsername();
		//SESSION
		String currentScope = pContext.getCurrentScope(request);
		String userToken = pContext.getCurrentUserToken(request);

		return new PortalContextInfo(username,currentScope, userToken);
	}

	/**
	 * Gets the key to session.
	 *
	 * @param key the key
	 * @param scope the scope
	 * @return the key to session
	 */
	private static String getKeyToSession(String key, String scope){
		return key+scope;
	}

	/**
	 * Gets the tasks cache.
	 *
	 * @param aslSession the asl session
	 * @param scope the scope
	 * @return the tasks cache
	 */
	@SuppressWarnings("unchecked")
	public static LinkedHashMap<String, TdTaskModel> getTasksCache(HttpSession aslSession, String scope) {

		return (LinkedHashMap<String, TdTaskModel>) aslSession.getAttribute(getKeyToSession(TD_TASKS_CACHE, scope));
	}

	/**
	 * Sets the tasks cache.
	 *
	 * @param aslSession the asl session
	 * @param scope the scope
	 * @param hashTaskModel the hash task model
	 */
	public static void setTasksCache(HttpSession aslSession, String scope, LinkedHashMap<String, TdTaskModel> hashTaskModel) {
		aslSession.setAttribute(getKeyToSession(TD_TASKS_CACHE, scope), hashTaskModel);

	}

	/**
	 * Gets the task td service client.
	 *
	 * @param aslSession the asl session
	 * @param scope the scope
	 * @return the task td service client
	 */
	public static TaskTabularDataService getTaskTdServiceClient(HttpSession aslSession, String scope) {

		return (TaskTabularDataService) aslSession.getAttribute(getKeyToSession(TD_TASKS_SERVICE_CLENT, scope));

	}

	/**
	 * Sets the task service client.
	 *
	 * @param aslSession the asl session
	 * @param scope the scope
	 * @param service the service
	 */
	public static void setTaskServiceClient(HttpSession aslSession, String scope, TaskTabularDataService service) {
		aslSession.setAttribute(getKeyToSession(TD_TASKS_SERVICE_CLENT, scope), service);

	}

	/**
	 * Gets the map operation description.
	 *
	 * @param aslSession the asl session
	 * @param scope the scope
	 * @return the map operation description
	 */
	@SuppressWarnings("unchecked")
	public static Map<Long, OperationDefinition> getMapOperationDescription(HttpSession aslSession, String scope) {

		return (Map<Long, OperationDefinition>) aslSession.getAttribute(getKeyToSession(TD_TASKS_MAP_OPERATIONS, scope));
	}


	/**
	 * Sets the map operation description.
	 *
	 * @param aslSession the asl session
	 * @param scope the scope
	 * @param map the map
	 */
	public static void setMapOperationDescription(HttpSession aslSession, String scope, Map<Long, OperationDefinition> map) {

		 aslSession.setAttribute(		getKeyToSession(TD_TASKS_MAP_OPERATIONS, scope),map);
	}

}
