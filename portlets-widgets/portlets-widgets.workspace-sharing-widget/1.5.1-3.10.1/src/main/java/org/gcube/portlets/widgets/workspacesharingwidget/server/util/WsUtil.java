/**
 * 
 */
package org.gcube.portlets.widgets.workspacesharingwidget.server.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.widgets.workspacesharingwidget.server.GWTWorkspaceSharingBuilder;
import org.gcube.portlets.widgets.workspacesharingwidget.server.notifications.NotificationsProducer;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.InfoContactModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.system.VO;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.system.VRE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.service.UserLocalServiceUtil;


/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 18, 2014
 *
 */
public class WsUtil {

	public static final String USERNAME_ATTRIBUTE = ScopeHelper.USERNAME_ATTRIBUTE;
	public static final String WORKSPACEBUILDER_ATTRIBUTE = "WORKSPACE_SHARING_BUILDER_ATTRIBUTE";
	public static final String NOTIFICATION_MANAGER = "NOTIFICATION_SHARING_MANAGER";
	public static final String NOTIFICATION_PRODUCER = "NOTIFICATION_SHARING_PRODUCER";
	public static final String WORKSPACE_SCOPE_UTIL = "WORKSPACE_SHARING_SCOPE_UTIL";
	public static final String NOTIFICATION_PORTLET_CLASS_ID = "org.gcube.portlets.user.workspace.server.GWTWorkspaceServiceImpl"; //USE THE SAME OF WORKSPACE
	
	
//	public static final String TEST_SCOPE = "/gcube/devsec";
//	public static final String TEST_USER = "pasquale.pagano";
	
	public static final String TEST_SCOPE = "/gcube/devsec";
//	public static final String TEST_USER = "federico.defaveri";
//	public static final String TEST_USER = "massimiliano.assante";
//	public static final String TEST_USER = "pasquale.pagano";
//	public static final String TEST_USER = "francesco.mangiacrapa";
//	public static final String TEST_USER_FULL_NAME = "Francesco Mangiacrapa";
//	public static final String TEST_USER = "aureliano.gentile";
	public static final String TEST_USER = "test.user";
//	public static final String TEST_USER = "antonio.gioia";
	public static final String TEST_USER_FULL_NAME = "Test User";
	
	
	protected static Logger logger = LoggerFactory.getLogger(WsUtil.class);

//	public static boolean withoutPortal = false;
	
	/**
	 * 
	 * @return true if you're running into the portal, false if in development
	 */
	public static boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (Exception ex) {			
			logger.info("Development Mode ON");
			return false;
		}			
	}
	
	public static ASLSession getAslSession(HttpSession httpSession)
	{
		String sessionID = httpSession.getId();
		String user = (String) httpSession.getAttribute(USERNAME_ATTRIBUTE);
		ASLSession session;
		
		if (user == null) {
			
			/*USE ANOTHER ACCOUNT (OTHERWHISE BY TEST_USER) FOR RUNNING
			 * COMMENT THIS IN DEVELOP ENVIROMENT (UNCOMMENT IN PRODUCTION)*/
			user=TEST_USER;
			
			//UNCOMMENT THIS IN DEVELOP ENVIROMENT
//			user = "francesco.mangiacrapa";
			
			logger.warn("WORKSPACE PORTLET STARTING IN TEST MODE - NO USER FOUND - PORTLETS STARTING WITH FOLLOWING SETTINGS:");
			logger.warn("session id: "+sessionID);
			logger.warn("TEST_USER: "+user);
			logger.warn("TEST_SCOPE: "+TEST_SCOPE);
			logger.warn("USERNAME_ATTRIBUTE: "+USERNAME_ATTRIBUTE);
			session = SessionManager.getInstance().getASLSession(sessionID, user);
			session.setScope(TEST_SCOPE);
	
			//MANDATORY FOR SOCIAL LIBRARY
			session.setUserAvatarId(user + "Avatar");
			session.setUserFullName(TEST_USER_FULL_NAME);
			session.setUserEmailAddress(user + "@mail.test");
			
			//SET HTTP SESSION ATTRIBUTE
			httpSession.setAttribute(USERNAME_ATTRIBUTE, user);
			
//			withoutPortal = true;
			
			return session;
			
		}else if(user.compareToIgnoreCase(TEST_USER)==0){
			
//			withoutPortal = true;
			
			//COMMENT THIS IN PRODUCTION ENVIROMENT
//		}else if(user.compareToIgnoreCase("francesco.mangiacrapa")==0){
//			
//			withoutPortal = false;
//			END UNCOMMENT
		}else{
			
//			withoutPortal = false;
		}
		
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	
	/**
	 * 
	 * @param httpSession
	 * @return true if current username into ASL session is WsUtil.TEST_USER, false otherwise
	 * @throws Exception
	 */
	public static boolean isSessionExpired(HttpSession httpSession) throws Exception {
		logger.info("workspace session validating...");
		//READING USERNAME FROM ASL SESSION
		String userUsername = getAslSession(httpSession).getUsername();
		boolean isTestUser = userUsername.compareTo(WsUtil.TEST_USER)==0;
		
		//TODO UNCOMMENT THIS FOR RELEASE
		logger.info("Is "+WsUtil.TEST_USER+" test user? "+isTestUser);
		
		if(isTestUser){
			logger.error("workspace session is expired! username is: "+WsUtil.TEST_USER);
			return true; //is TEST_USER, session is expired
		}

		logger.info("workspace session is valid! current username is: "+userUsername);
		
		return false;
		
	}
	
	/**
	 * 
	 * @param infrastructure the infrastructure name
	 * @param startScopes the starting scopes
	 * @return an arraylist of <class>VO</class> containing their child VREs
	 */
	public static List<VO> getVresFromInfrastructure(String infrastructure, String startScopes) {
		List<VO> toReturn = new ArrayList<VO>();

		String[] organizations;
		if (startScopes.contains(",")) {
			organizations = startScopes.split(",");
			for (int i = 0; i < organizations.length; i++) {
				VO toAdd = new VO();
				toAdd.setName(organizations[i]);
				toAdd.setVres((ArrayList<VRE>) LoginServiceUtil.getVREsFromInfrastructure(infrastructure+"/"+organizations[i]));
				toReturn.add(toAdd);
			}
		}
		else {
			organizations = new String[1];
			organizations[0] = startScopes;
			VO toAdd = new VO();
			toAdd.setName(organizations[0]);
			toAdd.setVres((ArrayList<VRE>) LoginServiceUtil.getVREsFromInfrastructure(infrastructure+"/"+organizations[0]));
			toReturn.add(toAdd);
		}
		return toReturn;
	}
	

	public static Workspace getWorkspace(final HttpSession httpSession) throws InternalErrorException, HomeNotFoundException, WorkspaceFolderNotFoundException
	{
		
		logger.info("Get Workspace");
		final ASLSession session = getAslSession(httpSession);
		logger.info("ASLSession scope: "+session.getScope() + " username: "+session.getUsername());

		ScopeProvider.instance.set(session.getScope());
		logger.info("Scope provider instancied");
		
		Workspace workspace = HomeLibrary.getUserWorkspace(session.getUsername());

		if (session.getAttribute(WORKSPACEBUILDER_ATTRIBUTE) == null)
		{
			logger.info("Initializing the workspace area builder");
			
			GWTWorkspaceSharingBuilder builder = new GWTWorkspaceSharingBuilder();
			
			//ADDED 03/09/2013 
			//TODO IS GROUP?
			builder.setUserLogged(new InfoContactModel(session.getUsername(), session.getUsername(), session.getUserFullName(), false));
			
			session.setAttribute(WORKSPACEBUILDER_ATTRIBUTE, builder);
		}

		return workspace;

	}

	public static NotificationsManager getNotificationManager(ASLSession session)
	{
		
		NotificationsManager notifMng = (NotificationsManager) session.getAttribute(NOTIFICATION_MANAGER);
		
		if (notifMng == null) {
			try{
				logger.info("Create new NotificationsManager for user: "+session.getUsername());
				logger.info("New ApplicationNotificationsManager with portlet class name: "+NOTIFICATION_PORTLET_CLASS_ID);
				notifMng = new ApplicationNotificationsManager(session, NOTIFICATION_PORTLET_CLASS_ID);
				session.setAttribute(NOTIFICATION_MANAGER, notifMng);
			}catch (Exception e) {
				logger.error("An error occurred instancing ApplicationNotificationsManager for user: "+session.getUsername(),e);
			}
		}
		
		return notifMng;
	}
	
	public static NotificationsProducer getNotificationProducer(ASLSession session)
	{
		
		NotificationsProducer notifProducer = (NotificationsProducer) session.getAttribute(NOTIFICATION_PRODUCER);
		
		if (notifProducer == null) {
			logger.info("Create new Notification Producer for user: "+session.getUsername());
			notifProducer = new NotificationsProducer(session);
			session.setAttribute(NOTIFICATION_PRODUCER, notifProducer);
		}
		
		return notifProducer;
	}

	public static String getUserId(HttpSession httpSession) {
		
		ASLSession session = getAslSession(httpSession);
		
		return session.getUsername();
	}
	
	public static boolean isVRE(ASLSession session){
		
		String currentScope = session.getScopeName();
		
		int slashCount = StringUtils.countMatches(currentScope, "/");
		
		if(slashCount < 3){
			logger.info("currentScope is not VRE");
			return false;
		}
		
		logger.info("currentScope is VRE");
		return true;
		
	}
	
	public static ScopeUtility getScopeUtilFilter(HttpSession httpSession){
		
		ASLSession session = getAslSession(httpSession);
		ScopeUtility scopeUtil = null;
		try{
			scopeUtil = (ScopeUtility) session.getAttribute(WsUtil.WORKSPACE_SCOPE_UTIL);
			
			if(scopeUtil==null){
				scopeUtil = new ScopeUtility(session.getScopeName());
				
			}
		}catch (Exception e) {
			logger.error("an error occurred in getscope filter ",e);
		}
		
		return scopeUtil;
	}

	/**
	 * @param session
	 * @return
	 */
	public static GWTWorkspaceSharingBuilder getGWTWorkspaceSharingBuilder(HttpSession httpSession) {
		ASLSession session = getAslSession(httpSession);
		GWTWorkspaceSharingBuilder builder = null;
		
		try{
			builder = (GWTWorkspaceSharingBuilder) session.getAttribute(WsUtil.WORKSPACEBUILDER_ATTRIBUTE);
			
			if(builder==null)
				return new GWTWorkspaceSharingBuilder();
		}catch (Exception e) {
			logger.error("an error occurred in get builder ",e);
		}
		
		return new GWTWorkspaceSharingBuilder();
	}
}
