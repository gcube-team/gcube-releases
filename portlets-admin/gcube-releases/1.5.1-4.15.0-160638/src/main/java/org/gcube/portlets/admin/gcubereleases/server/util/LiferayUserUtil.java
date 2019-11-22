package org.gcube.portlets.admin.gcubereleases.server.util;

import java.util.List;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeTeam;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;


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

		try{
			RoleManager rm = new LiferayRoleManager();
			GCubeUser theUser = new LiferayUserManager().getUserByUsername(aslSession.getUsername());
			long groupId = new LiferayGroupManager().getGroupIdFromInfrastructureScope(aslSession.getScope());
			List<GCubeTeam> roles = rm.listTeamsByUserAndGroup(theUser.getUserId(), groupId);
			for (GCubeTeam team : roles) {
				logger.info("VRE Group " + team.getTeamName() + " is "+RELEASE_MANAGER + "?");
				if (team.getTeamName().equals(RELEASE_MANAGER)) {
					logger.info("returning true?");
					return true;
				}
			}
			logger.info("The logged USER is not a "+RELEASE_MANAGER+"!");
			return false;

		}catch (UserManagementSystemException | UserRetrievalFault | GroupRetrievalFault e) {
			logger.error("Cannot find root organziation, please check gcube-data.properties file in $CATALINA_HOME/conf folder");
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
