package org.gcube.portlets.user.workspace.server.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.datacatalogue.ckanutillibrary.CKanUtils;
import org.gcube.datacatalogue.ckanutillibrary.CKanUtilsImpl;
import org.gcube.datacatalogue.ckanutillibrary.models.RolesIntoOrganization;
import org.gcube.portlets.user.workspace.client.model.InfoContactModel;
import org.gcube.portlets.user.workspace.server.GWTWorkspaceServiceImpl;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.OrganizationBean;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.gcube.vomanagement.usermanagement.model.GatewayRolesNames;

import eu.trentorise.opendata.jackan.model.CkanOrganization;


/**
 * The Class UserUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 17, 2015
 */
public class UserUtil {

	static UserManager um = new LiferayUserManager();
	protected static Logger logger = Logger.getLogger(UserUtil.class);


	/**
	 * Gets the user full name.
	 *
	 * @param portalLogin the portal login
	 * @return the user full name if is available, the input parameter portalLogin otherwise
	 */
	public static String getUserFullName(String portalLogin){
		//		return user.getPortalLogin(); //for testing in eclipse

		//		logger.trace("Finding full name for: "+portalLogin);

		if(portalLogin==null)
			return "";

		if (WsUtil.isWithinPortal()) { //INTO PORTAL

			GCubeUser curr = null;

			try {
				try {
					curr = um.getUserByScreenName(portalLogin);

				} catch (UserManagementSystemException e) {

					logger.error("An error occurred in getUserFullName "+e,e);
				} catch (UserRetrievalFault e) {

					logger.error("An error occurred in getUserFullName "+e,e);
				}
			}catch (Exception e) {
				logger.error("An error occurred in getUserFullName "+e,e);
				logger.warn("Return portal login "+portalLogin);
				return portalLogin;
			}

			if (curr != null){
				//				logger.trace("Return "+curr.getFullname() +" full name for: "+portalLogin);
				return curr.getFullname();
			}
		}else{
			logger.trace("DEVELOPEMENT MODE ON");
			logger.trace("Returning input login: "+portalLogin);
			return portalLogin;
		}

		logger.trace("Return portal login as full name for: "+portalLogin);
		return portalLogin;
	}

	/**
	 * Gets the list login by info contact model.
	 *
	 * @param listContacts the list contacts
	 * @return the list login by info contact model
	 */
	public static List<String> getListLoginByInfoContactModel(List<InfoContactModel> listContacts){

		List<String> listUsers = new ArrayList<String>();

		for (InfoContactModel infoContactModel : listContacts) {
			listUsers.add(infoContactModel.getLogin());
		}

		return listUsers;
	}

	/**
	 * Separate users names to comma.
	 *
	 * @param listContacts the list contacts
	 * @return the string
	 */
	public static String separateUsersNamesToComma(List<InfoContactModel> listContacts){

		String users = "";

		for (int i = 0; i < listContacts.size()-1; i++) {
			users+= listContacts.get(i).getName() + ", ";
		}

		if(listContacts.size()>1)
			users += listContacts.get(listContacts.size()-1).getName();

		return users;
	}


	/**
	 * Separate full name to comma for portal login.
	 *
	 * @param listLogin the list login
	 * @return the string
	 */
	public static String separateFullNameToCommaForPortalLogin(List<String> listLogin){

		String users = "";

		logger.trace("SeparateFullNameToCommaForPortalLogin converting: "+listLogin);

		//N-1 MEMBERS
		for (int i = 0; i < listLogin.size()-1; i++) {
			//			logger.trace("Converting: "+i+") "+listLogin.get(i));
			users+= getUserFullName(listLogin.get(i)) + ", ";
		}

		//LAST MEMBER
		if(listLogin.size()>=1){
			//			logger.trace("Converting: "+(listLogin.size()-1)+") " +listLogin.get(listLogin.size()-1));
			users += getUserFullName(listLogin.get(listLogin.size()-1));
		}

		logger.trace("SeparateFullNameToCommaForPortalLogin returning: "+users);

		return users;
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		List<String> login = new ArrayList<String>();
		//		login.add("ale");
		//		login.add("pepe");
		System.out.println(separateFullNameToCommaForPortalLogin(login));

	}

	/**
	 * Retrieve the highest ckan role the user has and also retrieve the list of organizations (scopes) in which the user has the ckan-admin role
	 * @param currentScope the current scope 
	 * @param username  the current username
	 * @param groupName the current groupName
	 * @param gcubeCkanDataCatalogServiceImpl 
	 * @param orgsInWhichAdminRole 
	 * @param ckanUtils ckanUtils
	 */
	public static boolean hasAdminRole(String currentScope, String username, String groupName, GWTWorkspaceServiceImpl workspaceInstance, List<OrganizationBean> orgsInWhichAdminRole){

		// base role as default value
		boolean toReturn = false;

		try{

			UserManager userManager = new LiferayUserManager();
			RoleManager roleManager = new LiferayRoleManager();
			GroupManager groupManager = new LiferayGroupManager();

			// user id
			long userid = userManager.getUserId(username);

			// retrieve current group id
			long currentGroupId = groupManager.getGroupIdFromInfrastructureScope(currentScope);

			logger.debug("Group id is " + currentGroupId + " and scope is " + currentScope);

			// retrieve the flat list of organizations for the current user
			List<GCubeGroup> groups = groupManager.listGroupsByUser(userid);

			// root (so check into the root, the VOs and the VRES)
			if(groupManager.isRootVO(currentGroupId)){

				logger.info("The current scope is the Root Vo, so the list of organizations of the user " + username + " is " + groups);

				for (GCubeGroup gCubeGroup : groups) {

					// get the name of this group
					String gCubeGroupName = gCubeGroup.getGroupName();

					// get the role of the users in this group
					List<GCubeRole> roles = roleManager.listRolesByUserAndGroup(userid, groupManager.getGroupId(gCubeGroupName));

					// get highest role
					RolesIntoOrganization correspondentRoleToCheck = getLiferayHighestRoleInOrg(roles);

					// if the role is member, continue
					if(correspondentRoleToCheck.equals(RolesIntoOrganization.MEMBER))
						continue;

					checkIfRoleIsSetInCkanInstance(username, gCubeGroupName, correspondentRoleToCheck, workspaceInstance, 
							groupManager, gCubeGroup.getGroupId(), orgsInWhichAdminRole);
				}

			}else if(groupManager.isVO(currentGroupId)){

				logger.debug("The list of organizations of the user " + username + " to scan is the one under the VO " + groupName);

				for (GCubeGroup gCubeGroup : groups) {

					// if the gCubeGroup is not under the VO or it is not the VO continue
					if(currentGroupId != gCubeGroup.getParentGroupId() || currentGroupId != gCubeGroup.getGroupId())
						continue;

					String gCubeGroupName = gCubeGroup.getGroupName();

					List<GCubeRole> roles = roleManager.listRolesByUserAndGroup(userid, groupManager.getGroupId(gCubeGroupName));

					// get highest role
					RolesIntoOrganization correspondentRoleToCheck = getLiferayHighestRoleInOrg(roles);

					// if the role is member, continue
					if(correspondentRoleToCheck.equals(RolesIntoOrganization.MEMBER))
						continue;

					checkIfRoleIsSetInCkanInstance(username, gCubeGroupName, correspondentRoleToCheck, workspaceInstance, 
							groupManager, gCubeGroup.getGroupId(), orgsInWhichAdminRole);
				}

			}else if(groupManager.isVRE(currentGroupId)){
				List<GCubeRole> roles = roleManager.listRolesByUserAndGroup(userManager.getUserId(username), groupManager.getGroupId(groupName));

				logger.debug("The current scope is the vre " + groupName);

				// get highest role
				RolesIntoOrganization correspondentRoleToCheck = getLiferayHighestRoleInOrg(roles);

				// if it the role is ADMIN we have to be sure to set it 
				if(correspondentRoleToCheck.equals(RolesIntoOrganization.ADMIN)){

					checkIfRoleIsSetInCkanInstance(username, groupName, correspondentRoleToCheck, workspaceInstance, 
							groupManager, currentGroupId, orgsInWhichAdminRole);
				}
			}
		}catch(Exception e){
			logger.error("Unable to retrieve the role information for this user. Returning false", e);
			return false;
		}

		//ok, somewhere he is admin
		if(orgsInWhichAdminRole.size() > 0)
			toReturn = true;

		// return the role
		logger.debug("Returning role "  + toReturn + " for user " + username);
		return toReturn;
	}

	/**
	 * Check if the role admin is set or must be set into the ckan instance at this scope
	 * @param username
	 * @param gCubeGroupName
	 * @param correspondentRoleToCheck
	 * @param workspaceInstance
	 * @param groupManager
	 * @param groupId
	 * @param orgsInWhichAdminRole
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	private static void checkIfRoleIsSetInCkanInstance(String username,
			String gCubeGroupName,
			RolesIntoOrganization correspondentRoleToCheck,
			GWTWorkspaceServiceImpl workspaceInstance,
			GroupManager groupManager, long groupId, List<OrganizationBean> orgsInWhichAdminRole) throws UserManagementSystemException, GroupRetrievalFault {

		// with this invocation, we check if the role is present in ckan and if it is not it will be added
		CKanUtils ckanUtils = workspaceInstance.getCkanUtilsObj(groupManager.getInfrastructureScope(groupId));

		// if there is an instance of ckan in this scope..
		if(ckanUtils != null){
			boolean res = ckanUtils.checkRole(username, gCubeGroupName, correspondentRoleToCheck);
			if(res){

				// get the orgs of the user 
				List<CkanOrganization> ckanOrgs = ckanUtils.getOrganizationsByUser(username);
				for (CkanOrganization ckanOrganization : ckanOrgs) {
					if(ckanOrganization.getName().equals(gCubeGroupName.toLowerCase()) || ckanOrganization.getName().equals(CKanUtilsImpl.PRODUCTION_CKAN_ORGNAME_ROOT)){
						orgsInWhichAdminRole.add(new OrganizationBean(ckanOrganization.getTitle(), ckanOrganization.getName()));
						break;
					}
				}
			}
		}else
			logger.error("It seems there is no ckan instance into scope " + groupManager.getInfrastructureScope(groupId));

	}

	/**
	 * Retrieve the ckan roles among a list of liferay roles
	 * @param roles
	 * @return
	 */
	private static RolesIntoOrganization getLiferayHighestRoleInOrg(
			List<GCubeRole> roles) {
		// NOTE: it is supposed that there is just one role for this person correspondent to the one in the catalog
		for (GCubeRole gCubeRole : roles) {
			if(gCubeRole.getRoleName().equalsIgnoreCase(GatewayRolesNames.CATALOGUE_ADMIN.getRoleName())){
				return RolesIntoOrganization.ADMIN;
			}
		}
		return RolesIntoOrganization.MEMBER;
	}
}
