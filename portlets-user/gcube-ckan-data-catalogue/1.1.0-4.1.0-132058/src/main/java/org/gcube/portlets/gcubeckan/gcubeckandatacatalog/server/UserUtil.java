/**
 *
 */
package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.server;

import java.util.ArrayList;
import java.util.List;

import org.gcube.datacatalogue.ckanutillibrary.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.models.RolesCkanGroupOrOrg;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.shared.CkanRole;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanOrganization;


/**
 * The Class UserUtil.
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @author Costantino Perciante costantino.perciante@isti.cnr.it
 * Jun 21, 2016
 */
public class UserUtil {

	private static Logger logger = LoggerFactory.getLogger(UserUtil.class);

	/**
	 * Gets the list vre for user.
	 * retrieve the groups to whom a given user belongs (given the user EMail)
	 *
	 * @param userEMail the user e mail
	 * @return the list vre for user
	 */
	public static List<String> getListVreForUser(String userEMail){

		// Instanciate the manager
		GroupManager groupManager = new LiferayGroupManager();
		// Instanciate the manager
		UserManager userManager = new LiferayUserManager();
		GCubeUser user;
		try {
			user = userManager.getUserByEmail(userEMail);
			// retrieve the groups to whom a given user belongs (given the user identifier)
			List<GCubeGroup> listOfGroups = groupManager.listGroupsByUser(user.getUserId());
			logger.info("List of VREs for "+userEMail+ " is/are: "+listOfGroups.size());
			List<String> vreNames = new ArrayList<String>(listOfGroups.size());
			for (GCubeGroup gCubeGroup : listOfGroups) {

				// TODO: why only the VRES??...however check for the production root vo
				//				if(gCubeGroup.getGroupName().equals(CKanUtilsImpl.PRODUCTION_LIFERAY_ORGNAME_ROOT))
				//					vreNames.add(CKanUtilsImpl.PRODUCTION_CKAN_ORGNAME_ROOT);

				//if(groupManager.isVRE(gCubeGroup.getGroupId())) //Is it a VRE?
				//				else
				vreNames.add(gCubeGroup.getGroupName());
			}
			logger.debug("Returning VRE names: "+vreNames);
			return vreNames;
		}catch (UserManagementSystemException | UserRetrievalFault | GroupRetrievalFault e) {
			logger.error("An error occurred during get list of VREs for user: "+userEMail, e);
			return null;
		}
	}

	/**
	 * Retrieve the highest ckan role the user has and also retrieve the list of organizations (scopes) in which the user has the ckan-admin or ckan-editor role
	 * @param currentScope the current scope 
	 * @param username  the current username
	 * @param groupName the current groupName
	 * @param gcubeCkanDataCatalogServiceImpl 
	 * @param orgsInWhichAdminRole 
	 * @param ckanUtils ckanUtils
	 */
	public static CkanRole getHighestRole(String currentScope, String username, String groupName, GcubeCkanDataCatalogServiceImpl gcubeCkanDataCatalogServiceImpl, List<OrganizationBean> orgsInWhichAtLeastEditorRole){

		// base role as default value
		CkanRole toReturn = CkanRole.MEMBER;

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
					RolesCkanGroupOrOrg correspondentRoleToCheck = getLiferayHighestRoleInOrg(roles);

					// if the role is member, continue
					//if(correspondentRoleToCheck.equals(RolesCkanGroupOrOrg.MEMBER))
					//continue;

					// be sure it is so
					checkIfRoleIsSetInCkanInstance(username, gCubeGroupName, gCubeGroup.getGroupId(), 
							correspondentRoleToCheck, groupManager, gcubeCkanDataCatalogServiceImpl, orgsInWhichAtLeastEditorRole);

					if(toReturn.equals(CkanRole.ADMIN))
						continue;
					else if(toReturn.equals(CkanRole.EDITOR) && correspondentRoleToCheck.equals(RolesCkanGroupOrOrg.ADMIN))
						toReturn = CkanRole.ADMIN;
					else // it was MEMBER
						toReturn =  mapRolesCkanGroupOrOrgToCkanRole(correspondentRoleToCheck);

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
					RolesCkanGroupOrOrg correspondentRoleToCheck = getLiferayHighestRoleInOrg(roles);

					// if the role is member, continue
					//if(correspondentRoleToCheck.equals(RolesCkanGroupOrOrg.MEMBER))
					//continue;

					// be sure it is so
					checkIfRoleIsSetInCkanInstance(username, gCubeGroupName, gCubeGroup.getGroupId(), 
							correspondentRoleToCheck, groupManager, gcubeCkanDataCatalogServiceImpl, orgsInWhichAtLeastEditorRole);

					if(toReturn.equals(CkanRole.ADMIN))
						continue;
					else if(toReturn.equals(CkanRole.EDITOR) && correspondentRoleToCheck.equals(RolesCkanGroupOrOrg.ADMIN))
						toReturn = CkanRole.ADMIN;
					else 
						toReturn =  mapRolesCkanGroupOrOrgToCkanRole(correspondentRoleToCheck);
				}

			}else if(groupManager.isVRE(currentGroupId)){
				List<GCubeRole> roles = roleManager.listRolesByUserAndGroup(userManager.getUserId(username), groupManager.getGroupId(groupName));

				logger.debug("The current scope is the vre " + groupName);

				// get highest role
				RolesCkanGroupOrOrg correspondentRoleToCheck = getLiferayHighestRoleInOrg(roles);

				// be sure it is so
				checkIfRoleIsSetInCkanInstance(username, groupName, currentGroupId, 
						correspondentRoleToCheck, groupManager, gcubeCkanDataCatalogServiceImpl, orgsInWhichAtLeastEditorRole);

				toReturn = mapRolesCkanGroupOrOrgToCkanRole(correspondentRoleToCheck);

			}
		}catch(Exception e){
			logger.error("Unable to retrieve the role information for this user. Returning member role", e);
			return CkanRole.MEMBER;
		}

		// return the role
		logger.debug("Returning role "  + toReturn + " for user " + username);
		return toReturn;
	}

	/**
	 * Check if the role admin/editor is set or must be set into the ckan instance at this scope
	 * @param username
	 * @param gCubeGroupName
	 * @param groupId
	 * @param correspondentRoleToCheck
	 * @param groupManager
	 * @param gcubeCkanDataCatalogServiceImpl
	 * @param orgsInWhichAdminRole
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	private static void checkIfRoleIsSetInCkanInstance(String username,
			String gCubeGroupName, long groupId,
			RolesCkanGroupOrOrg correspondentRoleToCheck,
			GroupManager groupManager,
			GcubeCkanDataCatalogServiceImpl gcubeCkanDataCatalogServiceImpl, List<OrganizationBean> orgsInWhichAtLeastEditorRole) throws UserManagementSystemException, GroupRetrievalFault {

		// with this invocation, we check if the role is present in ckan and if it is not it will be added
		DataCatalogue catalogue = gcubeCkanDataCatalogServiceImpl.getCatalogue(groupManager.getInfrastructureScope(groupId));

		// if there is an instance of ckan in this scope..
		if(catalogue != null){
			boolean res = catalogue.checkRoleIntoOrganization(username, gCubeGroupName, correspondentRoleToCheck);

			if(res && !correspondentRoleToCheck.equals(RolesCkanGroupOrOrg.MEMBER)){
				// get the orgs of the user and retrieve its title and name
				List<CkanOrganization> ckanOrgs = catalogue.getOrganizationsByUser(username);
				for (CkanOrganization ckanOrganization : ckanOrgs) {
					if(ckanOrganization.getName().equals(gCubeGroupName.toLowerCase())){
						orgsInWhichAtLeastEditorRole.add(new OrganizationBean(ckanOrganization.getTitle(), ckanOrganization.getName()));
						break;
					}
				}
			}
		}else
			logger.error("It seems there is no ckan instance into scope " + groupManager.getInfrastructureScope(groupId));

	}

	/**
	 * Retrieve the ckan role among a list of liferay roles
	 * @param roles
	 * @return MEMBER/EDITOR/ADMIN role
	 */
	private static RolesCkanGroupOrOrg getLiferayHighestRoleInOrg(
			List<GCubeRole> roles) {

		// NOTE: it is supposed that there is just one role for this person correspondent to the one in the catalog
		for (GCubeRole gCubeRole : roles) {
			if(gCubeRole.getRoleName().equalsIgnoreCase(GatewayRolesNames.CATALOGUE_ADMIN.getRoleName())){
				return RolesCkanGroupOrOrg.ADMIN;
			}
			if(gCubeRole.getRoleName().equalsIgnoreCase(GatewayRolesNames.CATALOGUE_EDITOR.getRoleName())){
				return RolesCkanGroupOrOrg.EDITOR;
			}
		}
		return RolesCkanGroupOrOrg.MEMBER;
	}

	/**
	 * Return the correspondent CkanRole
	 * @param correspondentRoleToCheck
	 * @return
	 */
	private static CkanRole mapRolesCkanGroupOrOrgToCkanRole(
			RolesCkanGroupOrOrg correspondentRoleToCheck) {
		switch(correspondentRoleToCheck){

		case ADMIN: return CkanRole.ADMIN;
		case EDITOR: return CkanRole.EDITOR;
		case MEMBER: return CkanRole.MEMBER;
		default:return null;

		}
	}
}
