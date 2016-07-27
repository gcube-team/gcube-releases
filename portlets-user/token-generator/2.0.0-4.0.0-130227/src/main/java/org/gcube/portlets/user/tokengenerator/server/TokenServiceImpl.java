package org.gcube.portlets.user.tokengenerator.server;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.user.tokengenerator.client.TokenService;
import org.gcube.portlets.user.tokengenerator.shared.UserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TokenServiceImpl extends RemoteServiceServlet implements  TokenService {
	private final static Logger _log = LoggerFactory.getLogger(TokenServiceImpl.class);
	private final static String DEFAULT_ROLE = "OrganizationMember";
	private final static String TEST_USER = "test.user";
	private final static String TEST_SCOPE = "/gcube/devsec";
	
	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			_log.warn("USER IS NULL setting test.user");
			user = getTestUser();
			SessionManager.getInstance().getASLSession(sessionID, user).setScope(TEST_SCOPE);
		}
		else {
			_log.info("LIFERAY PORTAL DETECTED user=" + user);
		}
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	
	/**
	 * Get the test user
	 * @return
	 */
	private String getTestUser(){
		
		String user = TEST_USER;
		//user = "costantino.perciante";
		return user;
	}

	@Override
	public UserBean getServiceToken() {
		String username = getTestUser();
		if (isWithinPortal()) {
			username = getASLSession().getUsername();
		}
		_log.debug("Generating token");
		if (username.compareTo(TEST_USER) == 0)
			return null;
		String scope = getASLSession().getScope();
		ScopeProvider.instance.set(scope);
		_log.debug("calling service token on scope " + scope);
		List<String> userRoles = new ArrayList<>();
		userRoles.add(DEFAULT_ROLE);
		String token = authorizationService().build().generate(getASLSession().getUsername(), userRoles);
		_log.debug("received token: "+token);
		
		return new UserBean(username, token);
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
