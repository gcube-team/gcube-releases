package org.gcube.portlets.admin.gcubereleases.server.util;

import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.OrganizationLocalServiceUtil;


/**
 * The Class LiferayUserUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class LiferayUserUtil {
	
	protected static Logger logger = LoggerFactory.getLogger(LiferayUserUtil.class);
	protected static final String RELEASE_MANAGER = "Release-Manager";
	
	/**
	 * tell if the user is a portal administrator or not.
	 *
	 * @param aslSession the asl session
	 * @return true if is admin
	 */
	public static boolean isReleaseManager(ASLSession aslSession)  {
		if (!ScopeUtil.isWithinPortal())
			return false;

		try {
			User currUser = OrganizationsUtil.validateUser(aslSession.getUsername());
			List<Organization> organizations = OrganizationLocalServiceUtil.getOrganizations(0, OrganizationLocalServiceUtil.getOrganizationsCount());
			Organization rootOrganization = null;
			for (Organization organization : organizations) {
				if (organization.getName().equals(OrganizationsUtil.getRootOrganizationName())) {
					rootOrganization = organization;
					break;
				}
			}		
			logger.info("root: " + rootOrganization.getName() );
			return (hasRole(RELEASE_MANAGER, rootOrganization.getName(), currUser));
		}
		catch (NullPointerException e) {
			logger.error("Cannot find root organziation, please check gcube-data.properties file in $CATALINA_HOME/conf folder");
			return false;
		} catch (com.liferay.portal.kernel.exception.PortalException e) {
			logger.error("PortalException: ", e);
			return false;
		} catch (com.liferay.portal.kernel.exception.SystemException e) {
			logger.error("SystemException: ", e);
			return false;
		}
	}
	
	/**
	 * Checks for role.
	 *
	 * @param rolename the rolename
	 * @param organizationName the organization name
	 * @param user the user
	 * @return true, if successful
	 * @throws SystemException the system exception
	 */
	private static boolean hasRole(String rolename, String organizationName, User user) throws com.liferay.portal.kernel.exception.SystemException {
		for (Role role : user.getRoles()) 
			if (role.getName().compareTo(rolename) == 0 ) 
				return true;
		return false;
	}
}
