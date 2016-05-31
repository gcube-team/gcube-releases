package org.gcube.portlets.admin.usersmanagementportlet.gwt.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.client.exceptions.CurrentGroupRetrievalException;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.client.exceptions.GroupAssignmentException;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.client.exceptions.PendingUsersRetrievalException;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.client.exceptions.RegisteredUsersRetrievalException;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.client.exceptions.RolesRetrievalException;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.client.exceptions.UserInfoRetrievalException;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.client.exceptions.UserRemovalFailureException;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.client.interfaces.UsersManagementService;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.shared.RecipientTypeConstants;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.shared.UserInfo;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.shared.UserStatus;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GroupModel;
import org.gcube.vomanagement.usermanagement.model.RoleModel;
import org.gcube.vomanagement.usermanagement.model.UserModel;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * UsersManagement servlet
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class UsersManagementServlet extends RemoteServiceServlet implements UsersManagementService {

	/** Logger */
	private static Logger logger = Logger.getLogger(UsersManagementServlet.class);

	private static final long serialVersionUID = 1L;


	private UserManager userM = new LiferayUserManager();
	private RoleManager roleM = new LiferayRoleManager();
	private GroupManager groupM = new LiferayGroupManager();

	private static final String SESSION_VOMS_UNREGISTERED_USERS = "ump_session_voms_users";
	private static final String USER_ADDED = "user_added";
	private static final String USER_REMOVED = "user_removed";
	
	private static final String VRE_MANAGER_ROLE = "VRE-Manager";

	/**
	 * Class constructor
	 */
	public UsersManagementServlet() {
		try {
			super.init();
		} catch (ServletException e) {
			logger.error("Servlet failed to initialize");
		}
	}

	/**
	 * Gets the ASL session
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
	 * @return the current group ID or null if an exception is thrown
	 * @throws CurrentGroupRetrievalException 
	 */
	private String getCurrentGroupID() throws CurrentGroupRetrievalException {
		ASLSession session = getASLsession();
		logger.debug("The current group NAME is --> " + session.getGroupName());	
		try {
			try {
				return groupM.getGroupId(session.getGroupName());
			} catch (UserManagementSystemException e) {
				throw new CurrentGroupRetrievalException(e.getMessage(), e.getCause());
			}
		} catch (GroupRetrievalFault e) {
			throw new CurrentGroupRetrievalException(e.getMessage(), e.getCause());
		}
	}


	/**
	 * Returns the available roles of the current VO
	 * 
	 * @return An array with the available roles
	 */
	public String[] getAvailableRolesByCurrentGroup() {
		try {
			List<RoleModel> roles = roleM.listRolesByGroup(getCurrentGroupID());
			String[] rolesNames= new String[roles.size()];
			int i=0;
			for(RoleModel role: roles){
				rolesNames[i] = role.getRoleName();
				i++;
				logger.debug("Geting roles from group: " + getASLsession().getGroupName());
				logger.debug("Role " + i + " is --> " + role.getRoleName());
			}
			return rolesNames;
		} catch (Exception e) {
			logger.error("Failed to retrieve the available roles. An exception was thrown",e);
			return null;
		} 
	}

	/**
	 * Returns all the users that are registered in VOMS.
	 * These users may not be registered in any VO/VRE
	 * 
	 * @return A list with the users that are registered to portal independent of the VOs and VREs
	 */
	public ArrayList<UserInfo> getAllRegisteredUsers() {
		ArrayList<UserInfo> userInfos = new ArrayList<UserInfo>();
		List<UserModel> allUsers;
		try {
			allUsers = userM.listUsers();
			logger.debug("Number of Portal users --> " + allUsers.size());
			for (UserModel u : allUsers) {
				ArrayList<String> userRoles = new ArrayList<String>();
				List<RoleModel> allRoles = roleM.listRolesByUser(userM.getUserId(u.getScreenName()));
				for (RoleModel r : allRoles)
					userRoles.add(r.getRoleName());
				
				UserInfo user = new UserInfo(u.getScreenName(), u.getFullname(), u.getEmail(), userRoles, null, null); 
				logger.debug("Portal's user information....");
				logger.debug("email --> " + u.getEmail() + ", username --> " + u.getScreenName());
				userInfos.add(user);
			}
			logger.debug("Number of UserInfo users --> " + userInfos.size());
		} catch (Exception e) {
			logger.error("Failed to get all registered users. An exception was thrown", e);
		}
		return userInfos;
	}

	/**
	 * Retrieves all the users that are registered to portal but are not registered to the current VO
	 * 
	 * @return A list with the username of the unregistered users
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<UserInfo> getUnregisteredUsersForVO() {
		try {
			ASLSession session = getASLsession();
			ArrayList<UserInfo> sessionUnUsers =  (ArrayList<UserInfo>)(session.getAttribute(SESSION_VOMS_UNREGISTERED_USERS+session.getScopeName()));
			if (sessionUnUsers != null) {
				return sessionUnUsers;
			}
			else {	
				ArrayList<UserInfo> unregisteredUsers = new ArrayList<UserInfo>();
				List<UserModel> unVomsUsers = userM.listUnregisteredUsersByGroup(getCurrentGroupID());
				for (UserModel u : unVomsUsers) {
					UserInfo myUser = new UserInfo(u.getScreenName(), u.getFullname(), u.getEmail(), null, null, null);
					unregisteredUsers.add(myUser);
				}
				logger.debug("The total unregistered users are:  " + unregisteredUsers.size());
				// sort the list using 'username' in ascending order
				Collections.sort(unregisteredUsers);
				session.setAttribute(SESSION_VOMS_UNREGISTERED_USERS+session.getScopeName(), unregisteredUsers);
				return unregisteredUsers;
			}	
		} catch (Exception e) {
			logger.error("Failed to retrieve the unregistered users. An exception was thrown", e);
		}
		return null;
	}

	/**
	 * Returns all the users and their roles that are registered to the current VO
	 * 
	 * @return A Hash map that contains the registered users and the roles that each user has, or null
	 */
	public ArrayList<UserInfo> getRegisteredUsersForaVO() {
		ArrayList<UserInfo> usersInfo = new ArrayList<UserInfo>();
		try {
			HashMap<UserModel,List<RoleModel>> userInfoAndRoles = userM.listUsersAndRolesByGroup(getCurrentGroupID());
			if (userInfoAndRoles != null && userInfoAndRoles.size()>0) {
				// Create the data here that will be inserted into the grid
				Iterator<UserModel> it = userInfoAndRoles.keySet().iterator();
				while (it.hasNext()) {
					UserModel user = (UserModel) it.next();
					// List with user's assigned roles
					ArrayList<String> userRs = new ArrayList<String>();

					List<RoleModel> userRoles = userInfoAndRoles.get(user);
					Iterator<RoleModel> UserRoleIter = userRoles.iterator();
					while(UserRoleIter.hasNext()){
						String roleName = UserRoleIter.next().getRoleName();
						userRs.add(roleName);
					}
					// Create a new UserInfo object for each user. The userComment is null for this type of users
					UserInfo myUser = new UserInfo(user.getScreenName(), user.getFullname(), user.getEmail(), userRs, null, null);
					usersInfo.add(myUser);
				}
			}
			logger.debug("Number of registered users --> " + usersInfo.size());
			return usersInfo;
		} catch (Exception e) {
			logger.error("Failed to get the registered users of the current VO. An exception was thrown", e);
			return null;
		}
	}

	/**
	 * Removes the users with the given username from the current VO
	 * 
	 * @param users The users to be removed
	 * @return True if the users removed, else False
	 * @throws CurrentGroupRetrievalException 
	 * @throws RolesRetrievalException 
	 * @throws UserRemovalFailureException 
	 * @throws UserInfoRetrievalException 
	 */
	public Boolean removeUsersFromVO(String users[]) throws CurrentGroupRetrievalException, RolesRetrievalException, UserRemovalFailureException, UserInfoRetrievalException {
		Boolean ret = true;
		if (users != null) {
			for (int i=0; i<users.length; i++) {
				logger.debug("Going to remove user from the current Group: " + getCurrentGroupID() + ". Username is: " + users[i]);
				try {
					userM.dismissUserFromGroup(getCurrentGroupID(), userM.getUserId(users[i]));
					removeUserFromHLGroup(users[i], getASLsession().getScope());
				} catch (Exception e) {
					throw new UserRemovalFailureException(e.getMessage(), e.getCause());
				}
				try {
					updateSessionUnUsers(userM.getUser(userM.getUserId(users[i])), USER_REMOVED);
				} catch (Exception e) {
					throw new UserInfoRetrievalException(e.getMessage(), e.getCause());
				}
			}
		}
		return ret;
	}

	/**
	 * Denies a list of requests from users for registration
	 * @param The list of users' requests to deny
	 * @return True if everything is ok
	 * @throws CurrentGroupRetrievalException
	 * @throws RolesRetrievalException
	 * @throws UserRemovalFailureException
	 * @throws UserInfoRetrievalException
	 */
	public Boolean denyRequests(String users[], boolean sendCustomMailToRejectedUsers) throws CurrentGroupRetrievalException, RolesRetrievalException, UserRemovalFailureException, UserInfoRetrievalException {
		Boolean ret = true;
		if (users != null) {
			for (int i=0; i<users.length; i++) {
				logger.debug("Going to remove user from the current Group: " + getCurrentGroupID() + ". Username is: " + users[i]);
				try {
					userM.denyMembershipRequest(userM.getUserId(users[i]), getCurrentGroupID());
					UserModel allUserInfo = userM.getUser(userM.getUserId(users[i]));
					if (!sendCustomMailToRejectedUsers)
						notifyUserForVORejection(users[i], allUserInfo.getEmail());
				} catch (Exception e) {
					throw new UserRemovalFailureException(e.getMessage(), e.getCause());
				}
			}
		}
		return ret;
	}

	/**
	 * Add users to the current VO
	 * 
	 * @param The users to be added
	 * @return True if the users have been added, else False
	 * @throws UserInfoRetrievalException 
	 * @throws RolesRetrievalException 
	 * @throws CurrentGroupRetrievalException 
	 * @throws GroupAssignmentException 
	 */
	public Boolean addUsersToVO(ArrayList<String> users) throws RolesRetrievalException, UserInfoRetrievalException, CurrentGroupRetrievalException, GroupAssignmentException {
		Boolean ret = true;
		if (users != null) {
			for (String userToadd : users) {
				logger.debug("User to be added is --> " + userToadd);

				logger.debug("Going to add user as a member of the VO.........");
				try {
					// The user should be added by the library also at parent and root groups
					try {
						userM.assignUserToGroup(getCurrentGroupID(), userM.getUserId(userToadd));
						logger.debug("User --> " + userToadd + " is added to the VO.");
						addUserToHLGroup(userToadd, getASLsession().getScope());
						// User is added to the VO update the session UnUsers variable
						UserModel allUserInfo = userM.getUser(userM.getUserId(userToadd));
						updateSessionUnUsers( allUserInfo, USER_ADDED);
						notifyUserForVORegistration(userToadd, allUserInfo.getEmail());
					} catch (UserManagementSystemException e) {
						throw new GroupAssignmentException(e.getMessage(), e.getCause());
					} catch (GroupRetrievalFault e) {
						throw new CurrentGroupRetrievalException(e.getMessage(), e.getCause());
					} catch (UserManagementPortalException e) {
						throw new GroupAssignmentException(e.getMessage(), e.getCause());
					}
				} catch (UserRetrievalFault e) {
					logger.error("Failed to add the user with username: " + userToadd + " to the current VO", e);
					throw new UserInfoRetrievalException(e.getMessage(), e.getCause());
				} 
			}
		}
		return ret;
	}
	
	private void addUserToHLGroup(String username, String group) {
		 try {
			org.gcube.common.homelibrary.home.workspace.usermanager.UserManager um = HomeLibrary.getHomeManagerFactory().getUserManager();
			um.associateUserToGroup(group, username, getASLsession().getUsername());
		} catch (InternalErrorException e) {
			logger.error("Failed to get the usermanager from HL. Could not add user to the HL group");
		} catch (ItemNotFoundException e1) {
			
		}
	}
	
	private void removeUserFromHLGroup(String username, String group) {
		 try {
			org.gcube.common.homelibrary.home.workspace.usermanager.UserManager um = HomeLibrary.getHomeManagerFactory().getUserManager();
			um.removeUserFromGroup(group, username, getASLsession().getUsername());
		} catch (InternalErrorException e) {
			logger.error("Failed to get the usermanager from HL. Could not add remove user from the HL group");
		} catch (ItemNotFoundException e1) {
				
		}
	}

	/**
	 * Returns the roles that a user has in the current VO/VRE.
	 * 
	 * @param username The user's username
	 * @return The user's roles or null if an error occurred
	 * @throws RolesRetrievalException 
	 * @throws CurrentGroupRetrievalException 
	 * @throws UserInfoRetrievalException 
	 */
	private ArrayList<String> getUserRolesForCurrentGroup(String username) throws RolesRetrievalException, CurrentGroupRetrievalException, UserInfoRetrievalException {
		ArrayList<String> roles = new ArrayList<String>();
		List<RoleModel> usersRolesByGroup;
		try {
			usersRolesByGroup = roleM.listRolesByUserAndGroup(getCurrentGroupID(), userM.getUserId(username));
		} catch (UserManagementSystemException e) {
			throw new RolesRetrievalException(e.getMessage());
		} catch (UserRetrievalFault e) {
			throw new UserInfoRetrievalException(e.getMessage(), e.getCause());
		}
		catch (GroupRetrievalFault e) {
			throw new CurrentGroupRetrievalException(e.getMessage(), e.getCause());
		}
		for (RoleModel userRole : usersRolesByGroup) {
			roles.add(userRole.getRoleName());
		}
		return roles;
	}

	private ArrayList<String> getVREManagersEmailsForCurrentGroup() {
		ArrayList<String> managersEmails = new ArrayList<String>();
		HashMap<UserModel, List<RoleModel>> usersAndRoles = null;
		try {
			usersAndRoles = userM.listUsersAndRolesByGroup(getCurrentGroupID());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		Set<UserModel> users = usersAndRoles.keySet();
		for (UserModel usr:users) {
			List<RoleModel> roles = usersAndRoles.get(usr);
			for (int i = 0; i < roles.size(); i++) {
				if (roles.get(i).getRoleName().equals("VRE-Manager")) {
					managersEmails.add(usr.getEmail());
					logger.debug("VRE Manager email -> " + usr.getEmail());
					break;
				}
			}
		}
		return managersEmails;
	}

	/**
	 * This method is invoked to add new Users to a VO. These users can be either VOMS users that
	 * do not belong to this VO, either users that belong to this VO but hane no roles, or users that
	 * belong to this VO and have the "pending" role
	 * 
	 * @param users The users' username that will be added to the VO
	 * @return True if all users added, else false
	 * @throws CurrentGroupRetrievalException 
	 */
	public Boolean addNewUsersToVO(ArrayList<String> users) throws CurrentGroupRetrievalException {
		logger.debug("Adding new users to the VO. Go check for their state.");
		Boolean ret = true;
		for (String user : users) {
			try {
				userM.assignUserToGroup(getCurrentGroupID(), userM.getUserId(user));
				addUserToHLGroup(user, getASLsession().getScope());
				updateSessionUnUsers(userM.getUser(userM.getUserId(user)), USER_ADDED);
			} catch (Exception e) {
				logger.error("Failed to add the user to the current group ID.", e);
			} 
		}
		return ret;
	}

	/**
	 * Update the user's roles
	 * 
	 * @param username User's username
	 * @param rolesAndValues A 2-dimensional array with the roles names and the values for the specified user 
	 * @return True if the roles were updated, else False
	 */
	public String updateUserRoles(String username, String rolesAndValues[][]) {
		logger.debug("Going to update roles for the user --> " + username);
		for (int i=0; i<rolesAndValues.length; i++) {
			if (rolesAndValues[i][1].toLowerCase().equals("true")) {
				logger.debug("assign new role.... name --> " + rolesAndValues[i][0]);
				try {
					roleM.assignRoleToUser(getCurrentGroupID(), roleM.getRoleId(rolesAndValues[i][0], getASLsession().getGroupName()), userM.getUserId(username));
					
					if (rolesAndValues[i][0].equals(VRE_MANAGER_ROLE)) {
						org.gcube.common.homelibrary.home.workspace.usermanager.UserManager um = HomeLibrary.getHomeManagerFactory().getUserManager();
						um.setAdministrator(getASLsession().getScope(), username, getASLsession().getUsername());
					}
				} catch (Exception e) {
					logger.error("Failed to assign the new roles to the user. An exception was thrown.", e);
					return UserStatus.EDIT_FAILED;
				} 
			}
			else {
				logger.debug("dismiss role.... name --> " + rolesAndValues[i][0]);
				try {
					roleM.dismissRoleFromUser(getCurrentGroupID(), roleM.getRoleId(rolesAndValues[i][0], getASLsession().getGroupName()), userM.getUserId(username));
					
					if (rolesAndValues[i][0].equals(VRE_MANAGER_ROLE)) {
						org.gcube.common.homelibrary.home.workspace.usermanager.UserManager um = HomeLibrary.getHomeManagerFactory().getUserManager();
						um.removeAdministrator(getASLsession().getScope(), username, getASLsession().getUsername());
					}
					
				} catch (Exception e) {
					logger.error("Failed to dismiss the roles from the user. An exception was thrown.", e);
					return UserStatus.EDIT_FAILED;
				} 
			}
		}
		return UserStatus.EDIT_OK;
	}



	/**
	 * This method returns a hash map with key the username of the new user and value an array list with all the roles that this
	 * user requests
	 * 
	 * @return An array list with the Users that have made a request
	 * @throws PendingUsersRetrievalException 
	 */
	public ArrayList<UserInfo> getUsersRequests() throws PendingUsersRetrievalException {
		List<UserModel> pendingVOMSUsers;
		try {
			String currentGroupID = getCurrentGroupID();
			pendingVOMSUsers = userM.listPendingUsersByGroup(currentGroupID);
			ArrayList<UserInfo> pendingUsers = new ArrayList<UserInfo>();
			logger.debug("Users that have requested membership for the: " + currentGroupID + " are");
			for (UserModel pendingUser : pendingVOMSUsers) {
				String username = pendingUser.getScreenName();
				String uCom = userM.getMembershipRequestComment(userM.getUserId(username), currentGroupID);
				//TODO this may not have a value at that stage
				UserInfo ui = new UserInfo(username, pendingUser.getFullname(), pendingUser.getEmail(), null, uCom, null);
				logger.debug(username);
				pendingUsers.add(ui);
			}
			return pendingUsers;
		} catch (Exception e) {
			logger.error("Failed to retrieve the pending users for the current VO/VRE. An exception was thrown", e);
			throw new PendingUsersRetrievalException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * Sends an email to the user for roles's updating
	 * 
	 * @param username The user to be informed
	 * @param userEmail The user's email
	 * @param newRoles An ArrayList with the user's current assigned roles
	 *//*
	private void notifyUserForRolesChanging(String username, String userEmail, ArrayList<String> newRoles) {
		String subject = "D4Science portal - Your roles for the VO/VRE have been changed";
		String rec[] = new String[1];
		rec[0] = userEmail;
		RolesUpdateEmailMessageTemplate msgTemp = new RolesUpdateEmailMessageTemplate(getASLsession().getScopeName(), newRoles);
		EmailNotification emailNot = new EmailNotification(getUserEmailAddress(getASLsession().getUsername()), rec, subject, msgTemp.createBodyMessage());
		emailNot.sendEmail();
	}*/

	/**
	 * Sends an email to the user to notify him for being registered to a VO
	 * 
	 * @param username The user to be informed
	 * @param userEmail The user's email
	 * @throws UserInfoRetrievalException 
	 */
	private void notifyUserForVORegistration(String username, String userEmail) throws UserInfoRetrievalException {
		// <VRE> Virtual Research Environment Request Approved
		String rec[] = new String[1];
		rec[0] = userEmail;
		HttpServletRequest request = this.getThreadLocalRequest();
		String portalURL = request.getScheme()+"://"+request.getServerName();
		if (request.getScheme().equals("http"))
			portalURL += (request.getServerPort() == 80) ? "" : ":"+request.getServerPort() ;
		else
			portalURL += (request.getServerPort() == 443) ? "" : ":"+request.getServerPort() ;

		logger.debug("Current group name is -> " + getASLsession().getGroupName());
		try {
			GroupModel group = groupM.getGroup(getCurrentGroupID());
			Boolean isVO = groupM.isVO(group.getGroupId());
			UserModel managerUserInfo = userM.getUser(userM.getUserId(getASLsession().getUsername()));
			UserModel registeredUser = userM.getUser(userM.getUserId(username));
			
			String subject = group.getGroupName();
			subject += isVO ? " Virtual Organisation " : " Virtual Research Environment ";
			subject += "Request Approved";
			
			UserRegistrationEmailMessageTemplate msgTemp = new UserRegistrationEmailMessageTemplate(registeredUser, managerUserInfo, group.getGroupName(), isVO, portalURL);
			EmailNotification emailNot = new EmailNotification(null, rec, getVREManagersEmailsForCurrentGroup(), subject, msgTemp.createBodyMessage(), RecipientTypeConstants.EMAIL_TO, false, false);
			emailNot.sendEmail();
		} catch (Exception e) {
			logger.error("Failed to send the email to the user: " + username + " .Failed to get his email address. An exception was thrown.", e);
			throw new UserInfoRetrievalException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * Sends an email to the user to notify him for being registered to a VO
	 * 
	 * @param username The user to be informed
	 * @param userEmail The user's email
	 * @throws UserInfoRetrievalException 
	 */
	private void notifyUserForVORejection(String username, String userEmail) throws UserInfoRetrievalException {
		String subject = "Registration";
		String rec[] = new String[1];
		rec[0] = userEmail;
		HttpServletRequest request = this.getThreadLocalRequest();
		String portalURL = request.getScheme()+"://"+request.getServerName();
		if (request.getScheme().equals("http"))
			portalURL += (request.getServerPort() == 80) ? "" : ":"+request.getServerPort() ;
		else
			portalURL += (request.getServerPort() == 443) ? "" : ":"+request.getServerPort() ;

		try {
			GroupModel group = groupM.getGroup(getCurrentGroupID());
			Boolean isVO = groupM.isVO(group.getGroupId());
			UserModel managerUserInfo = userM.getUser(userM.getUserId(getASLsession().getUsername()));
			UserModel registeredUser = userM.getUser(userM.getUserId(username));
			UserRejectionEmailMessageTemplate msgTemp = new UserRejectionEmailMessageTemplate(registeredUser, managerUserInfo, group.getGroupName(), isVO, portalURL);
			EmailNotification emailNot = new EmailNotification(null, rec, getVREManagersEmailsForCurrentGroup(), subject, msgTemp.createBodyMessage(), RecipientTypeConstants.EMAIL_TO, false, false);
			emailNot.sendEmail();
		} catch (Exception e) {
			logger.error("Failed to send the email to the user: " + username + " .Failed to get his email address. An exception was thrown.", e);
			throw new UserInfoRetrievalException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * Sends an email to the user to notify him for being removed from a VO
	 * 
	 * @param username The user to be informed
	 * @param userEmail The user's email
	 *//*
	private void notifyUserForVORemoval(String username, String userEmail) {
		String subject = "D4Science portal - You have been unregistered from the VO/VRE";
		String rec[] = new String[1];
		rec[0] = userEmail;
		UserRemovalEmailMessageTemplate msgTemp = new UserRemovalEmailMessageTemplate(getASLsession().getScopeName());
		EmailNotification emailNot = new EmailNotification(getUserEmailAddress(getASLsession().getUsername()), rec, subject, msgTemp.createBodyMessage());
		emailNot.sendEmail();
	}*/

	/**
	 * Sends an email with the given subject and body to all registered users of the current VO/VRE
	 * 
	 * @param subject The email's subject
	 * @param body The email's body
	 * @throws CurrentGroupRetrievalException 
	 * @throws RegisteredUsersRetrievalException 
	 * @throws UserInfoRetrievalException 
	 */
	public void sendEmailToRegisteredUsers(String subject, String body) throws CurrentGroupRetrievalException, RegisteredUsersRetrievalException, UserInfoRetrievalException {
		List<UserModel> registeredUsers = new ArrayList<UserModel>();
		try {
			registeredUsers = userM.listUsersByGroup(getCurrentGroupID());
			String recipients[] = new String[registeredUsers.size()];
			int i = 0;
			for (UserModel u : registeredUsers) {
				recipients[i] = u.getEmail();
				i++;
			}
			//EmailNotification emailNot = new EmailNotification(senderEmail, recipients, subject, body, RecipientTypeConstants.EMAIL_BCC, false);
			//TODO this is for TEST only
			EmailNotification emailNot = new EmailNotification(null, recipients, null, subject, body, RecipientTypeConstants.EMAIL_BCC, false, true);
			emailNot.sendEmail();
		} catch (Exception e) {
			logger.error("Failed to get the user's email. An exception was thrown", e);
			throw new UserInfoRetrievalException(e.getMessage(), e.getCause());
		} 	

	}


	/**
	 * Sends an email with the given subject and body to all registered users of the current VO/VRE
	 * 
	 * @param subject The email's subject
	 * @param body The email's body
	 * @throws CurrentGroupRetrievalException 
	 * @throws RegisteredUsersRetrievalException 
	 * @throws UserInfoRetrievalException 
	 */
	public void sendEmail(ArrayList<String> emails, String subject, String body) {
		String recipients[] = new String[emails.size()];
		int i = 0;
		for (String e : emails) {
			recipients[i] = e;
			i++;
		}
		try {
			//EmailNotification emailNot = new EmailNotification(senderEmail, recipients, subject, body, RecipientTypeConstants.EMAIL_BCC, false);
			UserModel managerUserInfo = userM.getUser(userM.getUserId(getASLsession().getUsername()));
				EmailNotification emailNot = new EmailNotification(
					managerUserInfo.getEmail(), recipients, getVREManagersEmailsForCurrentGroup(), subject,
					body, RecipientTypeConstants.EMAIL_BCC, false, false);
			emailNot.sendEmail();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void sendEmailWithErrorToSupport(Throwable caught) {
		String subject = "Users Management Portlet - Error Notification";
		String rec[] = new String[1];
		rec[0] = "support_team@d4science.org";
		try {
			ErrorNotificationEmailMessageTemplate msgTemp = new ErrorNotificationEmailMessageTemplate(caught, getASLsession().getUsername());
			EmailNotification emailNot = new EmailNotification(null, rec, null, subject, msgTemp.createBodyMessage(), RecipientTypeConstants.EMAIL_TO, false, false);
			emailNot.sendEmail();
		} catch (Exception e) {
			logger.error("Failed to send the email to the support team.", e);
		} 
	}

	public Integer getNumberOfUserThatReceiveNots() throws RegisteredUsersRetrievalException {
		int numOfUsers;
		List<UserModel> registeredUsers = new ArrayList<UserModel>();
		try {
			registeredUsers = userM.listUsersByGroup(getCurrentGroupID());
			numOfUsers = registeredUsers.size();
			return numOfUsers;
		} catch (Exception e) {
			logger.error("Failed to get the number of registered users. An exception was thrown", e);
			throw new RegisteredUsersRetrievalException(e.getMessage(), e.getCause());
		} 

	}

	/**
	 * This method updated the session variable that holds the unregistered VOMS users of the current VO
	 * 
	 * @param username
	 * @param status
	 * @throws CurrentGroupRetrievalException 
	 * @throws RolesRetrievalException 
	 * @throws UserInfoRetrievalException 
	 */
	@SuppressWarnings("unchecked")
	private void updateSessionUnUsers(UserModel user, String status) throws RolesRetrievalException, CurrentGroupRetrievalException, UserInfoRetrievalException {
		ASLSession session = getASLsession();
		ArrayList<UserInfo> sessionUnUsers =  (ArrayList<UserInfo>)(session.getAttribute(SESSION_VOMS_UNREGISTERED_USERS+session.getScopeName()));
		if (sessionUnUsers != null) {
			// user is added to the VO. He should be removed from the unregistered users
			if (status.equals(USER_ADDED)) {
				logger.debug("A new user has been added to the VO. Going to update the session UnUsers attribute");
				int index = -1;
				int i=0;
				for (UserInfo unUser : sessionUnUsers) {
					if (unUser.getUsername().equals(user.getScreenName())) {
						index = i;
						break;
					}
					i++;
				}
				sessionUnUsers.remove(index);
				logger.debug("User has been removed from the UnUsers session attribute");
			}
			// If the user has been removed from the VO, he should be added to the unregistered users list
			else {
				logger.debug("A user has been removed from the VO. Going to update the session UnUsers attribute");
				UserInfo removedUser = new UserInfo(user.getScreenName(), user.getFullname(), user.getEmail(), getUserRolesForCurrentGroup(user.getScreenName()), null, null);
				sessionUnUsers.add(removedUser);
				// sort by 'username' in ascending order
				Collections.sort(sessionUnUsers);
				logger.debug("User has been added to the UnUsers session attribute");
			}
			session.setAttribute(SESSION_VOMS_UNREGISTERED_USERS+session.getScopeName(), sessionUnUsers);
		}
	}

}
