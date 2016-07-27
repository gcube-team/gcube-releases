/**
 * 
 */
package org.gcube.portlets.admin.accountingmanager.server;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.accountingmanager.server.state.AccountingState;
import org.gcube.portlets.admin.accountingmanager.server.state.AccountingStateData;
import org.gcube.portlets.admin.accountingmanager.shared.Constants;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.gcube.portlets.admin.accountingmanager.shared.exception.SessionExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class SessionUtil {

	private static Logger logger = LoggerFactory.getLogger(SessionUtil.class);

	public static ASLSession getASLSession(HttpSession httpSession)
			throws ServiceException {
		String username = (String) httpSession
				.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		ASLSession aslSession;
		if (username == null) {
			if (Constants.DEBUG_MODE) {
				logger.info("no user found in session, use test user");

				// Remove comment for Test
				username = org.gcube.portlets.admin.accountingmanager.shared.Constants.DEFAULT_USER;
				String scope = Constants.DEFAULT_SCOPE;

				httpSession.setAttribute(ScopeHelper.USERNAME_ATTRIBUTE,
						username);
				aslSession = SessionManager.getInstance().getASLSession(
						httpSession.getId(), username);
				aslSession.setScope(scope);
			} else {
				logger.info("No user found in session");
				throw new SessionExpiredException("Session Expired!");

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
		String token = null;
		if (Constants.DEBUG_MODE) {
			List<String> userRoles = new ArrayList<>();
			userRoles.add(Constants.DEFAULT_ROLE);
			/*
			 * if (aslSession.getUsername().compareTo("lucio.lelii") == 0)
			 * userRoles.add("VRE-Manager");
			 */
			token = authorizationService().build().generate(
					aslSession.getUsername(), userRoles);

		} else {
			token = aslSession.getSecurityToken();
		}
		logger.info("received token: " + token);
		return token;

	}

	public static void setAccountingStateData(HttpSession httpSession,
			AccountingType accountingType,
			AccountingStateData accountingStateData) {
		AccountingState accountingState = (AccountingState) httpSession
				.getAttribute(Constants.SESSION_ACCOUNTING_STATE);

		if (accountingState == null) {
			accountingState = new AccountingState();
			accountingState.setState(accountingType, accountingStateData);
			httpSession.setAttribute(Constants.SESSION_ACCOUNTING_STATE,
					accountingState);
		} else {
			accountingState.setState(accountingType, accountingStateData);
		}

		return;
	}
	
	public static AccountingStateData getAccountingStateData(HttpSession httpSession,
			AccountingType accountingType) {
		AccountingState accountingState = (AccountingState) httpSession
				.getAttribute(Constants.SESSION_ACCOUNTING_STATE);
		if (accountingState == null) {
			return null;
		} else {
			return accountingState.getState(accountingType);
		}
	}

}
