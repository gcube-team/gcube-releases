package org.gcube.portlets.admin.rolesmanagementportlet.gwt.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.exceptions.CurrentGroupRetrievalException;
import org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.exceptions.RetrieveAllowedRolesException;
import org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.exceptions.RoleCreationException;
import org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.exceptions.RoleUpdateException;
import org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.exceptions.RolesRetrievalException;
import org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.interfaces.RolesManagementService;
import org.gcube.portlets.admin.rolesmanagementportlet.gwt.shared.RecipientTypeConstants;
import org.gcube.portlets.admin.rolesmanagementportlet.gwt.shared.RoleInfo;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementFileNotFoundException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementIOException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.model.RoleModel;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class RolesManagementServlet extends RemoteServiceServlet implements RolesManagementService {

	/** Logger */
	private static Logger logger = Logger.getLogger(RolesManagementServlet.class);
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * A LiferayRoleManager to interact with the liferay's roles
	 */
	private RoleManager rolesManager = new LiferayRoleManager();
	private GroupManager groupM = new LiferayGroupManager();
	
	/**
	 * Class constructor
	 */
	public RolesManagementServlet() {
		try {
			super.init();
		} catch (ServletException e) {
			logger.error("Servlet failed to initialize");
		}
	}
	
	/**
	 * Get the ASL session
	 * 
	 * @return the ASL session
	 */
	private ASLSession getASLsession() {
		HttpSession httpSession = this.getThreadLocalRequest().getSession();
		String username = httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
		ASLSession session = SessionManager.getInstance().getASLSession(httpSession.getId(), username);
		return session;
	}
	
	/**
	 * Get the current group ID
	 * 
	 * @return The current group ID
	 * @throws UserManagementSystemException
	 * @throws CurrentGroupRetrievalException
	 */
	private String getCurrentGroupID() throws UserManagementSystemException, CurrentGroupRetrievalException {
		ASLSession session = getASLsession();
		logger.debug("The current group NAME is --> " + session.getGroupName());
		logger.debug("The current groupID is --> " + session.getGroupId());
		
		try {
			return groupM.getGroupId(session.getGroupName());
		} catch (GroupRetrievalFault e) {
			logger.error("Failed to get the current group's ID. An exception was thrown", e);
			throw new CurrentGroupRetrievalException(e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Lists the allowed roles that can be created for the current VO/VRE
	 * 
	 * @return An array with the allowed roles' names
	 */
	public ArrayList<RoleInfo> listAllowedRoles() throws CurrentGroupRetrievalException, RetrieveAllowedRolesException {
		ArrayList<RoleInfo> allowedRoles = new ArrayList<RoleInfo>();
		HashMap<String, String> aRoles;
		try {
			aRoles = rolesManager.listAllowedRoles(getASLsession().getGroupName());
			Iterator<String> it = aRoles.keySet().iterator();
			while (it.hasNext()) {
				String roleName = it.next();
				String roleDesc = aRoles.get(roleName);
				RoleInfo ri = new RoleInfo(roleName, roleDesc);
				allowedRoles.add(ri);
			}		
		} catch (UserManagementSystemException e) {
			logger.error("Failed to list the allowed roles for the current VO/VRE. An exception was thrown", e);
			throw new RetrieveAllowedRolesException(e.getMessage(), e.getCause());
		} catch (GroupRetrievalFault e) {
			throw new CurrentGroupRetrievalException(e.getMessage(), e.getCause());
		} catch (UserManagementFileNotFoundException e) {
			logger.error("Failed to list the allowed roles for the current VO/VRE. An exception was thrown", e);
			throw new RetrieveAllowedRolesException(e.getMessage(), e.getCause());
		} catch (UserManagementIOException e) {
			logger.error("Failed to list the allowed roles for the current VO/VRE. An exception was thrown", e);
			throw new RetrieveAllowedRolesException(e.getMessage(), e.getCause());
		}
		return allowedRoles;
	}
	
	
	/**
	 * Returns the available roles of the current VO/VRE
	 * 
	 * @return An array with the available roles
	 * @throws RolesRetrievalException 
	 * @throws CurrentGroupRetrievalException 
	 */
	public ArrayList<RoleInfo> getAvailableRoles() throws RolesRetrievalException, CurrentGroupRetrievalException {
		ArrayList<RoleInfo> allRoles = new ArrayList<RoleInfo>();

		// returns a distinct list with the role names of the current group
		List<RoleModel> roles;
		try {
			roles = rolesManager.listRolesByGroup(getCurrentGroupID());
		} catch (UserManagementSystemException e) {
			throw new RolesRetrievalException(e.getMessage(), e.getCause());
		} catch (GroupRetrievalFault e) {
			throw new CurrentGroupRetrievalException(e.getMessage(), e.getCause());
		} catch (UserManagementFileNotFoundException e) {
			throw new RolesRetrievalException(e.getMessage(), e.getCause());
		} catch (UserManagementIOException e) {
			throw new RolesRetrievalException(e.getMessage(), e.getCause());
		}

		if (roles!=null && roles.size()>0) {
			logger.debug("Printing the available roles for the current group...");
			for (int i=0; i<roles.size();i++){
				String roleName = roles.get(i).getRoleName();
				String roleDesc = roles.get(i).getDescription();
				logger.debug("Role Name --> " + roleName + " Role Desc --> " + roleDesc);
				RoleInfo ri = new RoleInfo(roleName, roleDesc);
				allRoles.add(ri);
			}
			return allRoles;
		}
		return null;
	}
	
	
	/**
	 * Deletes all the given roles
	 * 
	 * @param roleNames The names of roles to be deleted
	 * @return True if all roles deleted, or False if something failed
	 */
	public Boolean deleteRoles(ArrayList<String> roleNames) {
		Boolean ret = true;
		int rolesDeleted = 0;
		logger.info("Deleting roles........");
		if (roleNames != null && roleNames.size() > 0) {
			for (String roleName : roleNames) {
				try {
					rolesManager.deleteRole(roleName, getASLsession().getGroupName());
					logger.info("Role with name --> " + roleName + " deleted.");
					rolesDeleted++;
				} catch (UserManagementSystemException e) {
					logger.error("Failed to delete the role with id --> " + roleName);
				} catch (RoleRetrievalFault e) {
					logger.error("Failed to delete the role with id --> " + roleName);
				}
			}
			if (rolesDeleted == roleNames.size())
				logger.info("All roles have been deleted");
			else {
				logger.info("Some roles were not deleted due to unexpected errors. An exception was thrown");
				ret = false;
			}
		}
		return ret;
	}
	
	/**
	 * Creates a role with the given name
	 * 
	 * @param roleName The name of the role to be created
	 * @return True if the role is created, or False if something failed
	 * @throws RoleCreationException 
	 */
	public Boolean createNewRole(String roleName, String roleDesc) throws RoleCreationException {
		Boolean ret = true;
			try {
				ret = rolesManager.createRole(roleName, roleDesc, getASLsession().getGroupName());
				logger.debug("The returned value of the createRole method is: " + ret);
				logger.info("A new role has been created with name --> " + roleName);	
			} catch (Exception e) {
				logger.error("Failed to create the role. An exception was thrown", e);
				throw new RoleCreationException(e.getMessage(), e.getCause());
			} 
		return ret;
	}
	
	public Boolean updateRole(String roleName, String newRoleName, String newRoleDescription) throws RoleUpdateException, RolesRetrievalException, CurrentGroupRetrievalException {
		Boolean ret = true;
		try {
			logger.debug("Trying to update role with initial name --> " + roleName);
			logger.debug("new name: " + newRoleName);
			logger.debug("new Desc: " + newRoleDescription);
			//roleId = rolesManager.getRoleId(roleName, getASLsession().getGroupName());
			//RoleModel rModel = new RoleModel(newRoleName, roleId, newRoleDescription);
			rolesManager.updateRole(roleName, newRoleName, newRoleDescription, getASLsession().getGroupName());
			return ret;
		} catch (UserManagementSystemException e) {
			logger.error("Failed to update the Role. An exception was thrown", e);
			throw new RoleUpdateException(e.getMessage(), e.getCause());
		} catch (GroupRetrievalFault e) {
			logger.error("Failed to get the current group's ID. An exception was thrown", e);
			throw new CurrentGroupRetrievalException(e.getMessage(), e.getCause());
		} catch (Exception e) {
			logger.error("Failed to retrieve the role that needs update", e);
			throw new RolesRetrievalException(e.getMessage(), e.getCause());
		}
	}
	
	public void sendEmailWithErrorToSupport(Throwable caught) {
		String subject = "[PORTAL-iMarine] Roles Management Portlet - Error Notification";
		String rec[] = new String[1];
		rec[0] = "support_team@d4science.org";
		try {
			ErrorNotificationEmailMessageTemplate msgTemp = new ErrorNotificationEmailMessageTemplate(caught, getASLsession().getUsername());
			EmailNotification emailNot = new EmailNotification(null, rec, subject, msgTemp.createBodyMessage(), RecipientTypeConstants.EMAIL_TO, false);
			emailNot.sendEmail();
		} catch (Exception e) {
			logger.error("Failed to send the email to the support team.", e);
		} 
	}
    
   
}
