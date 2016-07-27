/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.server;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.dataminermanager.server.smservice.SClient;
import org.gcube.portlets.user.dataminermanager.server.smservice.SClient4WPSBuilder;
import org.gcube.portlets.user.dataminermanager.server.smservice.SClientBuilder;
import org.gcube.portlets.user.dataminermanager.server.smservice.SClientDirector;
import org.gcube.portlets.user.dataminermanager.server.util.ServiceCredential;
import org.gcube.portlets.user.dataminermanager.shared.Constants;
import org.gcube.portlets.user.dataminermanager.shared.exception.SessionExpiredServiceException;
import org.gcube.portlets.user.dataminermanager.shared.exception.ServiceException;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SessionUtil {

	private static final Logger logger = Logger.getLogger(SessionUtil.class);
	
	public static ASLSession getASLSession(HttpSession httpSession)
			throws ServiceException {
		String username = (String) httpSession
				.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		ASLSession aslSession;
		if (username == null) {
			if (Constants.DEBUG_MODE) {
				logger.info("no user found in session, use test user");
				username = Constants.DEFAULT_USER;
				String scope = Constants.DEFAULT_SCOPE;

				httpSession.setAttribute(ScopeHelper.USERNAME_ATTRIBUTE,
						username);
				aslSession = SessionManager.getInstance().getASLSession(
						httpSession.getId(), username);
				aslSession.setScope(scope);
			} else {
				logger.info("no user found in session!");
				throw new SessionExpiredServiceException("Session Expired!");

			}
		} else {
			aslSession = SessionManager.getInstance().getASLSession(
					httpSession.getId(), username);

		}

		logger.info("SessionUtil: aslSession " + aslSession.getUsername() + " "
				+ aslSession.getScope());

		return aslSession;
	}

	public static String getToken(ASLSession aslSession) {
		String token=null;
		if (Constants.DEBUG_MODE) {
			List<String> userRoles = new ArrayList<>();
			userRoles.add(Constants.DEFAULT_ROLE);
			/*if (aslSession.getUsername().compareTo("lucio.lelii") == 0)
				userRoles.add("VRE-Manager");*/
			token = authorizationService().build().generate(
					aslSession.getUsername(), userRoles);
			
		} else {
			token = aslSession.getSecurityToken();
		}
		logger.info("received token: " + token);
		return token;
		
		
	}	
	
	public static SClient getSClient(ASLSession aslSession, HttpSession session)
			throws Exception {

		if(aslSession==null){
			logger.error("ASLSession is null!");
			throw new SessionExpiredServiceException("Session Expired!");
		} 
		SClient sClient; 
				
		Object obj=session.getAttribute(Constants.SClientMap);
		if(obj==null){
			logger.info("Create new SClientMap");
			HashMap<String,SClient> sClientMap=new HashMap<>();
			logger.info("Create new SClient");
			ServiceCredential serviceCredential=new ServiceCredential(aslSession.getUsername(), aslSession.getScope(), 
					SessionUtil.getToken(aslSession));
			SClientBuilder sBuilder = new SClient4WPSBuilder(serviceCredential);
			SClientDirector director = new SClientDirector();
			director.setSClientBuilder(sBuilder);
			director.constructSClient();
			sClient = director.getSClient();
			
			sClientMap.put(aslSession.getScope(), sClient);
			session.setAttribute(Constants.SClientMap, sClientMap);
		} else {
			if (obj instanceof HashMap<?, ?>) {
				@SuppressWarnings("unchecked")
				HashMap<String,SClient> sClientMap=(HashMap<String,SClient>) obj;	
				if(sClientMap.containsKey(aslSession.getScope())){
					logger.info("Use SClient in session");
					sClient=sClientMap.get(aslSession.getScope());
				} else {
					logger.info("Create new SClient");
					ServiceCredential serviceCredential=new ServiceCredential(aslSession.getUsername(), aslSession.getScope(), 
							SessionUtil.getToken(aslSession));
					SClientBuilder sBuilder = new SClient4WPSBuilder(serviceCredential);

					SClientDirector director = new SClientDirector();
					director.setSClientBuilder(sBuilder);
					director.constructSClient();
					sClient = director.getSClient();
					sClientMap.put(aslSession.getScope(), sClient);
					session.setAttribute(Constants.SClientMap, sClientMap);
				}
				
			} else {
				logger.error("Attention no SClientMap in Session!");
				throw new ServiceException("Sign Out, portlet is changed, a new session is required!");
			}
		}

		return sClient;

	}

}
