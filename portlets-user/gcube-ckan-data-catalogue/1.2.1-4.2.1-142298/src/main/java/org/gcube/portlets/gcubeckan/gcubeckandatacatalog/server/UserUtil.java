/**
 *
 */
package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.server.thread.AddUserToOrganizationThread;
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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import eu.trentorise.opendata.jackan.model.CkanOrganization;


/**
 * The Class UserUtil.
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @author Costantino Perciante costantino.perciante@isti.cnr.it
 * Jun 21, 2016
 */
public class UserUtil {

	//private static Logger logger = LoggerFactory.getLogger(UserUtil.class);
	private static final Log logger = LogFactoryUtil.getLog(UserUtil.class);

	/**
	 * Gets the list vre for user and the role the user has in them.
	 * retrieve the groups to whom a given user belongs (given the user EMail)
	 * @param userEMail the user e mail
	 * @return the list vre for user
	 */
	public static Map<String, String> getVreRoleForUser(String userEMail, String context, DataCatalogue instance){

		GroupManager groupManager = new LiferayGroupManager();
		UserManager userManager = new LiferayUserManager();
		RoleManager roleManager = new LiferayRoleManager();
		Map<String, String> mapRoleByGroupSingleVre = new HashMap<String, String>();
		Map<String, String> mapRoleByGroupExtrasVre = new HashMap<String, String>();
		GCubeUser user;
		try {
			user = userManager.getUserByEmail(userEMail);
			List<GCubeGroup> listOfGroups = groupManager.listGroupsByUser(user.getUserId());

			// filter according the current context: if it is a VO/VRE, we send all the VRES under the VO. If it is the root vo, we send all user's vres.
			long groupIdContext = groupManager.getGroupIdFromInfrastructureScope(context);
			GCubeGroup groupContext = groupManager.getGroup(groupIdContext);
			Iterator<GCubeGroup> iterator = listOfGroups.iterator();
			if(groupManager.isVRE(groupIdContext)){
				long parentId = groupContext.getParentGroupId();
				while (iterator.hasNext()) {
					GCubeGroup gCubeGroup = (GCubeGroup) iterator.next();
					if(gCubeGroup.getParentGroupId() != parentId)
						iterator.remove();
				}
			}else if(groupManager.isVO(groupIdContext)){
				// get the list of vres 
				while (iterator.hasNext()) {
					GCubeGroup gCubeGroup = (GCubeGroup) iterator.next();
					if(groupIdContext != gCubeGroup.getParentGroupId())
						iterator.remove();
				}
			}else{
				// only the vres
				while (iterator.hasNext()) {
					GCubeGroup gCubeGroup = (GCubeGroup) iterator.next();
					if(!groupManager.isVRE(gCubeGroup.getGroupId()))
						iterator.remove();
				}
			}

			// retrieve the role
			for (GCubeGroup vre: listOfGroups) {
				if(vre.getGroupId() == groupIdContext)
					mapRoleByGroupSingleVre.put(vre.getGroupName().toLowerCase(), 
							RolesCkanGroupOrOrg.convertToCkanCapacity(getLiferayHighestRoleInOrg(roleManager.listRolesByUserAndGroup(user.getUserId(), vre.getGroupId()))));
				else
					mapRoleByGroupExtrasVre.put(vre.getGroupName().toLowerCase(), 
							RolesCkanGroupOrOrg.convertToCkanCapacity(getLiferayHighestRoleInOrg(roleManager.listRolesByUserAndGroup(user.getUserId(), vre.getGroupId()))));
			}

			// schedule the thread for the groups other than the vre to be checked in the local ckan instance
			if(mapRoleByGroupExtrasVre.size() > 0)
				new AddUserToOrganizationThread(
						instance, 
						userManager.getUserByEmail(userEMail).getUsername(), 
						mapRoleByGroupExtrasVre).
						start();

			logger.debug("Returning Map to the ckan connector : " + mapRoleByGroupSingleVre);
			return mapRoleByGroupSingleVre;
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
	public static RolesCkanGroupOrOrg getHighestRole(String currentScope, String username, String groupName, GcubeCkanDataCatalogServiceImpl gcubeCkanDataCatalogServiceImpl, List<OrganizationBean> orgsInWhichAtLeastEditorRole){

		// base role as default value
		RolesCkanGroupOrOrg toReturn = RolesCkanGroupOrOrg.MEMBER;

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

					if(!groupManager.isVRE(gCubeGroup.getGroupId()))
						continue;

					// get the name of this group
					String gCubeGroupName = gCubeGroup.getGroupName();

					// get the role of the users in this group
					List<GCubeRole> roles = roleManager.listRolesByUserAndGroup(userid, groupManager.getGroupId(gCubeGroupName));

					// get highest role
					RolesCkanGroupOrOrg correspondentRoleToCheck = getLiferayHighestRoleInOrg(roles);

					// be sure it is so
					checkIfRoleIsSetInCkanInstance(username, gCubeGroupName, gCubeGroup.getGroupId(), 
							correspondentRoleToCheck, groupManager, gcubeCkanDataCatalogServiceImpl, orgsInWhichAtLeastEditorRole);

					toReturn = RolesCkanGroupOrOrg.getHigher(toReturn, correspondentRoleToCheck);

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

					// be sure it is so
					checkIfRoleIsSetInCkanInstance(username, gCubeGroupName, gCubeGroup.getGroupId(), 
							correspondentRoleToCheck, groupManager, gcubeCkanDataCatalogServiceImpl, orgsInWhichAtLeastEditorRole);

					toReturn = RolesCkanGroupOrOrg.getHigher(toReturn, correspondentRoleToCheck);
				}

			}else if(groupManager.isVRE(currentGroupId)){
				List<GCubeRole> roles = roleManager.listRolesByUserAndGroup(userManager.getUserId(username), groupManager.getGroupId(groupName));

				logger.debug("The current scope is the vre " + groupName);

				// get highest role
				RolesCkanGroupOrOrg correspondentRoleToCheck = getLiferayHighestRoleInOrg(roles);

				// the ckan connector already did the job for us but we need name and title 
				checkIfRoleIsSetInCkanInstance(username, groupName, currentGroupId, 
						correspondentRoleToCheck, groupManager, gcubeCkanDataCatalogServiceImpl, orgsInWhichAtLeastEditorRole);

				toReturn = correspondentRoleToCheck;

			}
		}catch(Exception e){
			logger.error("Unable to retrieve the role information for this user. Returning member role", e);
			return RolesCkanGroupOrOrg.MEMBER;
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
				CkanOrganization organization = catalogue.getOrganizationByName(gCubeGroupName.toLowerCase());
				orgsInWhichAtLeastEditorRole.add(new OrganizationBean(organization.getTitle(), organization.getName()));
			}
		}
		else
			logger.warn("It seems there is no ckan instance into scope " + groupManager.getInfrastructureScope(groupId));

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
}
