package org.gcube.portlets.widgets.wsexplorer.server;

import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.UserModel;
import org.slf4j.Logger;

import com.liferay.portal.service.UserLocalServiceUtil;



/**
 * The Class UserUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 30, 2015
 */
public class UserUtil {
	
	private static UserManager um = new LiferayUserManager();
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(UserUtil.class);

	/**
	 * Gets the user full name.
	 *
	 * @param portalLogin the portal login
	 * @return the user full name
	 */
	public static String getUserFullName(String portalLogin){
		logger.trace("Get user full name for: "+portalLogin);
		if(portalLogin==null)
			return "";
		
		if (isWithinPortal()) { //INTO PORTAL
			UserModel curr = null;
			try {
				try {
					curr = um.getUserByScreenName(portalLogin);
				} catch (UserManagementSystemException e) {
					logger.error("An error occurred in getUserFullName "+e,e);
				} catch (UserRetrievalFault e) {
					logger.error("An error occurred in getUserFullName "+e,e);
				}
			} catch (UserManagementPortalException ume) {
				logger.error("An error occurred in getUserFullName "+ume,ume);
			}catch (Exception e) {
				logger.error("An error occurred in getUserFullName "+e,e);
				logger.warn("Return portal login "+portalLogin);
				return portalLogin;
			}
			
			if (curr != null)
				return curr.getFullname();	
		}else{
			logger.trace("DEVELOPEMENT MODE ON");
			logger.trace("Returning portal login: "+portalLogin);
			return portalLogin;
		}
		
		logger.trace("Returning portal login: "+portalLogin);
		return portalLogin;
	}
	
	/**
	 * Checks if is within portal.
	 *
	 * @return true if you're running into the portal, false if in development
	 */
	public static boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (Exception ex) {			
			logger.trace("Is within portal: no! Development Mode ON");
			return false;
		}			
	}
}
