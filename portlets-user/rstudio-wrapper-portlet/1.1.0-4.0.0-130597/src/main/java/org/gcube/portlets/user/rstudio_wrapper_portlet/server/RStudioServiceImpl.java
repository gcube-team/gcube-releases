package org.gcube.portlets.user.rstudio_wrapper_portlet.server;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.rstudio_wrapper_portlet.client.RStudioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.gcube.common.authorization.client.Constants.authorizationService;
import static org.gcube.data.analysis.rconnector.client.Constants.rConnector;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class RStudioServiceImpl extends RemoteServiceServlet implements RStudioService {

	private static final Logger _log = LoggerFactory.getLogger(RStudioServiceImpl.class);
	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			_log.warn("USER IS NULL setting testing user and Running OUTSIDE PORTAL");
			user = getDevelopmentUser();
		}		
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	/**
	 * when packaging test will fail if the user is not set to test.user
	 * @return .
	 */
	public String getDevelopmentUser() {
		String user = "test.user";
//		user = "costantino.perciante";
		return user;
	}
	@Override
	public String retrieveRStudioSecureURL() throws IllegalArgumentException {
		String toReturn = "";
		String token = getASLSession().getSecurityToken();
		try {
			_log.debug("calling rConnector with token = "+token);
			toReturn = rConnector().build().connect().toURL().toExternalForm();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} 
		_log.debug("returning URL from rConnector = "+toReturn);
		return toReturn;
	}

}
