/**
 * 
 */
package org.gcube.portlets.user.messages.server.util;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.messages.server.GWTMessagesBuilder;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class SessionUtil {

	public static final String USERNAME_ATTRIBUTE = "username";
	public static final String MESSAGESBUILDER_ATTRIBUTE = "MESSAGESBUILDERS";
	
	public static Logger logger = Logger.getLogger(SessionUtil.class);
	
//	public static final String TEST_SCOPE = "/gcube/devsec";
//	public static final String TEST_USER = "pasquale.pagano";
	
//	public static final String TEST_SCOPE = "/gcube/devsec";
////	public static final String TEST_USER = "federico.defaveri";
//	public static final String TEST_USER = "test.user";
//	public static final String TEST_USER_FULLNAME = "Test User";
//	public static final String TEST_USER = "francesco.mangiacrapa";
//	public static final String TEST_USER_FULLNAME = "Francesco Mangiacrapa";
	
//	public static final String TEST_USER = "pasquale.pagano";

//	public static ASLSession getAslSession(HttpSession httpSession)
//	{
//		String sessionID = httpSession.getId();
//		String user = (String) httpSession.getAttribute(USERNAME_ATTRIBUTE);
//
//		if (user == null) {
//
//			logger.error("WORKSPACE PORTLET STARTING IN TEST MODE - NO USER FOUND");
//
//			//for test only
//			httpSession.setAttribute(USERNAME_ATTRIBUTE, TEST_USER);
//			ASLSession session = SessionManager.getInstance().getASLSession(sessionID, TEST_USER);
//			session.setScope(TEST_SCOPE);
//			//session.setScope("/d4science.research-infrastructures.eu/Ecosystem/TryIt");
//
//			return session;
//		}
//
//		return SessionManager.getInstance().getASLSession(sessionID, user);
//	}

	public static Workspace getWorkspace(ASLSession session) throws Exception 
	{
		
		if(session==null)
			throw new Exception("ASL Session is null");
		
		ScopeProvider.instance.set(session.getScope());
		
		Workspace workspace = HomeLibrary.getUserWorkspace(session.getUsername());
			
		return workspace;

	}
	
	public static GWTMessagesBuilder getGWTWorkspaceBuilder(ASLSession session)
	{
		
		GWTMessagesBuilder builder = (GWTMessagesBuilder) session.getAttribute(MESSAGESBUILDER_ATTRIBUTE);
		
		if (builder == null)
		{
			logger.trace("Initializing the workspace area builder");
			
			builder = new GWTMessagesBuilder();
			session.setAttribute(MESSAGESBUILDER_ATTRIBUTE, builder);
		}
		
		return builder;
	}
}
