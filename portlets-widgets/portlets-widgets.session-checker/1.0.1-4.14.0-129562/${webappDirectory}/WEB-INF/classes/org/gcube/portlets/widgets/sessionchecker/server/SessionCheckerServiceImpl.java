package org.gcube.portlets.widgets.sessionchecker.server;

import java.util.Date;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.widgets.sessionchecker.client.SessionCheckerService;
import org.gcube.portlets.widgets.sessionchecker.shared.SessionInfoBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SessionCheckerServiceImpl extends RemoteServiceServlet implements  SessionCheckerService {

	private static final Logger _log = LoggerFactory.getLogger(SessionCheckerServiceImpl.class);


	/**
	 * the current ASLSession
	 * @return .
	 */
	private ASLSession getASLSession() {	
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		SessionManager manager = SessionManager.getInstance();
		ASLSession toReturn = manager.getASLSession(sessionID, user);
		return toReturn;		
	}

	@Override
	public SessionInfoBean checkSession() {
		SessionManager manager = null;
		try { 
			manager = SessionManager.getInstance();
		}
		catch (NullPointerException e) {
			_log.error("SessionManager#getInstance null returning session expired");
			return new SessionInfoBean("",""); //tells session expired
		}
		if (manager == null) {
			_log.warn("Liferay Portal Detected but session Expired");
			return new SessionInfoBean("",""); //tells session expired
		}
		
		
		ASLSession session = null;
		try { 
			session = getASLSession();
			if (session == null || session.getUsername() == null) {
				UserLocalServiceUtil.getService();
				_log.warn("Liferay Portal Detected but session Expired");
				return new SessionInfoBean(null,null); //tells session expired
			}
		}
		catch (Exception e) {
			if (isWithinPortal()) {
				_log.warn("Liferay Portal Detected but session Expired");
				return new SessionInfoBean(null,null); //tells session expired
			}
			else {
				_log.warn("Stopping session polling as i think you are in development mode");
				return new SessionInfoBean("","", true); //tells that you are in development mode 				
			}
		}	
		String user = session.getUsername();
		String scope = session.getScope();
		//else
		_log.trace("Session check OK for " + user + " at " + new Date());
		if (user == null || user.compareTo("") == 0) {
			_log.warn("User is null at " + new Date());			
		}
		if (scope == null || scope.compareTo("") == 0) {
			_log.warn("Scope is null at " + new Date());
		}
		return new SessionInfoBean(session.getUsername(), session.getScope());
	}

	/**
	 * 
	 * @return true if you're running into the portal, false if in development
	 */
	private boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (com.liferay.portal.kernel.bean.BeanLocatorException ex) {			
			_log.trace("Development Mode ON");
			return false;
		}			
	}
}
