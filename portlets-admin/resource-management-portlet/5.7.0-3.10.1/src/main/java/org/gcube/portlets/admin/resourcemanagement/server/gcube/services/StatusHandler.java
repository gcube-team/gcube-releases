/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: StatusHandler.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.server.gcube.services;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.resourcemanagement.server.gcube.services.configuration.ConfigurationLoader;
import org.gcube.resourcemanagement.support.client.utils.CurrentStatus;
import org.gcube.resourcemanagement.support.server.utils.ServerConsole;
import org.gcube.resourcemanagement.support.shared.types.RunningMode;
import org.gcube.resourcemanagement.support.shared.types.UserGroup;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.RoleModel;
import org.gcube.vomanagement.usermanagement.model.UserModel;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class StatusHandler {
	private static final String STATUS_KEY = "current_status";
	public static final String USER_EMAIL_KEY = "theusermail";
	private static final String LOG_PREFIX = "[StatusHandler]";
	private static final String ADMIN_ROLE = "VO-Admin";

	public static final void setStatus(final HttpSession session, final CurrentStatus status) {
		session.setAttribute(STATUS_KEY, status);
	}

	public static final void clearStatus(final HttpSession session) {
		if (session != null && session.getAttribute(STATUS_KEY) != null) {
			session.removeAttribute(STATUS_KEY);
		}
	}

	/**
	 * Initializes the status if running in portal mode otherwise returns null.
	 * @param session
	 * @return
	 */
	private static CurrentStatus initStatus(final HttpSession session) {
		if (session == null || session.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE) == null) {
			return null;
		}

		String username =  session.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();

		// inside portal
		if (username != null) {
			ServerConsole.info(LOG_PREFIX, "Running inside portal... [OK]");
			CurrentStatus status =  new CurrentStatus();
			status.setCurrentUser(username);
			status.setCredentials(UserGroup.USER);
			status.setRunningMode(RunningMode.PORTAL);
			ASLSession aslSession = SessionManager.getInstance().getASLSession(session.getId(), username);
			ServerConsole.error(LOG_PREFIX, "The portal scope is [" + aslSession.getScopeName() + "]");
			
			status.setCurrentScope(aslSession.getScopeName());
		
			aslSession.getGroupId();
			RoleManager rman = new LiferayRoleManager();
			UserManager uman = new LiferayUserManager();

	
			try {
				List<RoleModel> roles = null;

				// Gets the user email. If not provided the default one will be used.
				// The mail will be store inside the session.
				UserModel umodel = uman.getUserByScreenName(username);
				session.setAttribute(USER_EMAIL_KEY, umodel.getEmail());

				roles = rman.listRolesByUserAndGroup(
						String.valueOf(aslSession.getGroupId()),
						uman.getUserByScreenName(username).getUserId());
				for (RoleModel role : roles) {
					if (role.getRoleName().equals(ADMIN_ROLE)) {
						status.setCredentials(UserGroup.ADMIN);
					}
				}
			} catch (Exception e) {
				ServerConsole.error(LOG_PREFIX, e);
			}
			return status;
		}
		// not running in a portal
		return null;
	}

	public static final CurrentStatus getStatus(final HttpSession session) {
		// CREATING THE STATUS
		// Status initialization
		if (session.getAttribute(STATUS_KEY) == null) {
			CurrentStatus status = initStatus(session);

			// Not in portal mode
			if (status == null) {
				ServerConsole.info(LOG_PREFIX, "Running outside portal... [OK]");
				status = new CurrentStatus();

				// LOADS THE DEFAULTS
				try {
					String runningMode = ConfigurationLoader.getProperty("RUNNING_MODE");
					status.setRunningMode(RunningMode.valueOf(runningMode));
					ServerConsole.debug(LOG_PREFIX, "Setting RUNNING_MODE to: " + status.getRunningMode());
				} catch (Exception e) {
					ServerConsole.error(LOG_PREFIX, "Loading defaults", e);
				}
				try {
					String credentials = ConfigurationLoader.getProperty("USER_CREDENTIALS");
					status.setCredentials(UserGroup.valueOf(credentials));
					ServerConsole.debug(LOG_PREFIX, "Setting USER_CREDENTIALS to: " + status.getCredentials());
				} catch (Exception e) {
					ServerConsole.error(LOG_PREFIX, "Loading defaults", e);
				}
				try {
					status.setCurrentUser(ConfigurationLoader.getProperty("DEFAULT_USER"));
					ServerConsole.debug(LOG_PREFIX, "Setting DEFAULT_USER to: " + status.getCurrentUser());
				} catch (Exception e) {
					ServerConsole.error(LOG_PREFIX, "Loading defaults", e);
				}
				try {
					status.setCurrentScope(ConfigurationLoader.getProperty("DEFAULT_SCOPE"));
					ServerConsole.debug(LOG_PREFIX, "Setting DEFAULT_SCOPE to: " + status.getCurrentScope());
				} catch (Exception e) {
					ServerConsole.error(LOG_PREFIX, "Loading defaults", e);
				}
				try {
					if (ConfigurationLoader.getProperty("LIST_GHN_STARTUP").equalsIgnoreCase("NO"))
						status.setLoadGHNatStartup(false);
					ServerConsole.debug(LOG_PREFIX, "*********************\n\n\nSetting LIST_GHN_STARTUP to: " + status.isLoadGHNatStartup());
				} catch (Exception e) {
					ServerConsole.error(LOG_PREFIX, "Loading defaults", e);
				}
			}
			session.setAttribute(STATUS_KEY, status);
			return status;
		}  else {
			return (CurrentStatus) session.getAttribute(STATUS_KEY);
		}
	}
}
