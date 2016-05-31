package org.gcube.portlets.widgets.guidedtour.server;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.widgets.guidedtour.client.TourService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.bean.BeanLocatorException;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TourServiceImpl extends RemoteServiceServlet implements TourService {

	private static final Logger _log = LoggerFactory.getLogger(TourServiceImpl.class);

	private static final String QUICK_TOUR_PREFIX = "_GuidedTour ";
	private static final String SHOW_ATTR = "DO-NOT-SHOW";
	public boolean withinPortal = false;
	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			user = "test.user";
		}
		else {
			withinPortal = true;
		}
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	/**
	 * check if a custom attribute with name portlet name exists
	 */
	@Override
	public Boolean showTour(String portletUniqueId) {
		String username = getASLSession().getUsername();
		String attrToCheck = getUniqueIdentifier(portletUniqueId);
		Boolean show = false;
//		System.out.println("\n\n\n\n\n\n*********** Checking custom attribute via ExpandoBridge = " + attrToCheck);
//		System.out.println("\n\n***********");
		if (username.compareTo("test.user") == 0) {
			_log.warn("Found test.user maybe you are in dev mode, returning showTour=true");
			return true;
		}
		try {
			
			long companyId = OrganizationsUtil.getCompany().getCompanyId();
			_log.trace("Setting Thread Permission");
			User user = UserLocalServiceUtil.getUserByScreenName(companyId, ScopeHelper.getAdministratorUsername());
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(user, false);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			_log.trace("Setting Permission ok!");

			User currUser = OrganizationsUtil.validateUser(username);
			if (currUser.getExpandoBridge().getAttribute(attrToCheck) == null)
				show = true;
			else {
				String currVal = (String) currUser.getExpandoBridge().getAttribute(attrToCheck);
				show = ! (currVal.compareTo(SHOW_ATTR) == 0);
				//System.out.println("\n\n***** Read Attr Value=" + currVal);			
			}

			_log.trace("Setting Thread Permission back to regular");			
			permissionChecker = PermissionCheckerFactoryUtil.create(currUser, false);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			_log.trace("Setting Permission ok! returning ...");
			//System.out.println(" returning *********** show=" + show);			
			return show;
		} catch (BeanLocatorException ex) {
			ex.printStackTrace();
			_log.warn("Could not read the property " + attrToCheck + " from LR DB, maybe you are in dev mode, returning true");
			return true;
		}
		catch (Exception e) { 
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public void setNotShowItAgain(String portletUniqueId) {
		String username = getASLSession().getUsername();
		String attrToSet = getUniqueIdentifier(portletUniqueId);
		if ( ! withinPortal) return;//if running into eclipse always return
		User currUser = null;
		try {
			long companyId = OrganizationsUtil.getCompany().getCompanyId();
			_log.trace("Setting Thread Permission");
			User user = UserLocalServiceUtil.getUserByScreenName(companyId, ScopeHelper.getAdministratorUsername());
			PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(user, false);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
			_log.trace("Setting Permission ok!");
			
			_log.debug("Creating and Setting custom attribute for colName " + attrToSet + " to " +SHOW_ATTR);
			//add the custom attrs
			currUser = UserLocalServiceUtil.getUserByScreenName(companyId, username);
			
			if (! currUser.getExpandoBridge().hasAttribute(attrToSet)) 	
				currUser.getExpandoBridge().addAttribute(attrToSet);
			
			currUser.getExpandoBridge().setAttribute(attrToSet, SHOW_ATTR);
			
			_log.trace("Setting Thread Permission back to regular");
			permissionChecker = PermissionCheckerFactoryUtil.create(currUser, false);
			PermissionThreadLocal.setPermissionChecker(permissionChecker);
		}
		catch (Exception e) {
			e.printStackTrace();
		} 

	}
	/**
	 * need to trunc to 75 char as declared in the schema ( type character varying(75))
	 * @param username
	 * @param portletid
	 * @return
	 */
	public static String getUniqueIdentifier(String portletid) {
		String toReturn = QUICK_TOUR_PREFIX+portletid;
		if (toReturn.length() > 74)
			return (QUICK_TOUR_PREFIX+portletid).substring(0, 74);
		else
			return toReturn;
	}
}