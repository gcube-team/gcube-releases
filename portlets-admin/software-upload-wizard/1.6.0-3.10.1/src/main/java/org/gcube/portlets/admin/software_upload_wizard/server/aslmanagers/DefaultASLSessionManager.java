package org.gcube.portlets.admin.software_upload_wizard.server.aslmanagers;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.software_upload_wizard.server.util.ScopeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * An ASL Session Manager that can be used both in development and in production
 * 
 * @author Luigi Fortunati
 * 
 */
@Singleton
public class DefaultASLSessionManager implements ASLSessionManager {
	
	private Logger logger = LoggerFactory.getLogger(DefaultASLSessionManager.class);

	private Provider<HttpSession> httpSessionProvider;

	@Inject
	public DefaultASLSessionManager(Provider<HttpSession> httpSessionProvider) {
		this.httpSessionProvider = httpSessionProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.portlets.user.softwaremanagementwidget.server.sessions.
	 * ASLSessionManager#getASLSession()
	 */
	@Override
	public ASLSession getASLSession() {
		logger.trace("Getting ASL session...");

		HttpSession httpSession = httpSessionProvider.get();
		logger.trace("Got HttpSession with id: " + httpSession.getId());

		String sessionID = httpSession.getId();

		String user = (String) httpSession
				.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);

		if (user == null) {
			// for test onlyscope
			user = "guest";
//			String scope = "/gcube/devNext";
//			String scope = "/gcube/devNext/NextNext";
//			String scope = "/gcube/devsec/devVRE";
//			String scope = "/d4science.research-infrastructures.eu/FARM";
			
			String scope = ScopeUtil.getScope();

			httpSession.setAttribute(ScopeHelper.USERNAME_ATTRIBUTE, user);
			ASLSession session = SessionManager.getInstance().getASLSession(
					sessionID, user);
			session.setScope(scope);

			logger.trace("Running in guest user mode.");
			return session;

		} else
			logger.trace("User found in session.");
		
		ASLSession session = SessionManager.getInstance().getASLSession(
				sessionID, user);
		logger.trace("Returning user " + session.getUsername()
				+ " working with scope " + session.getScope().toString());

		return SessionManager.getInstance().getASLSession(sessionID, user);

	}
}
