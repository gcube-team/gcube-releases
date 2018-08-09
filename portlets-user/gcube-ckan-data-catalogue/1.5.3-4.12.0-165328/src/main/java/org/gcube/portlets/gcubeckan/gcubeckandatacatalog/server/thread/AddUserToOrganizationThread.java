package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.server.thread;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.shared.RolesCkanGroupOrOrg;
import org.gcube.portlets.gcubeckan.gcubeckandatacatalog.server.UserUtil;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * Add the user to the other organizations present in the local instance of ckan.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class AddUserToOrganizationThread extends Thread {

	private static final Log logger = LogFactoryUtil.getLog(AddUserToOrganizationThread.class);
	private DataCatalogue instance;
	private GCubeUser user;
	private List<GCubeGroup> listOfGroups;
	private boolean isViewPerVREEnabled;
	private long groupIdContext;
	private GroupManager groupManager;
	private RoleManager roleManager;
	private String localRoleInThisVre;

	/**
	 * @param instance
	 * @param user
	 * @param listOfGroups 
	 * @param isViewPerVREEnabled 
	 * @param groupIdContext 
	 * @param groupManager 
	 * @param roleManager 
	 * @param localRoleInThisVre 
	 */
	public AddUserToOrganizationThread(DataCatalogue instance, GCubeUser user,
			List<GCubeGroup> listOfGroups, 
			boolean isViewPerVREEnabled, 
			long groupIdContext, RoleManager roleManager, GroupManager groupManager, String localRoleInThisVre) {
		super();
		this.instance = instance;
		this.user = user;
		this.listOfGroups = listOfGroups;
		this.isViewPerVREEnabled= isViewPerVREEnabled;
		this.groupIdContext = groupIdContext;
		this.roleManager = roleManager;
		this.groupManager = groupManager;
		this.localRoleInThisVre = localRoleInThisVre;
	}

	@Override
	public void run() {

		try{
			GCubeGroup currentVRE = groupManager.getGroup(groupIdContext);
			
			// check if extra roles must be assigned
			logger.debug("Checking if there is the need to extend role " + localRoleInThisVre + " for user " + user.getUsername() + " in other vres");
			instance.assignRolesOtherOrganization(user.getUsername(), 
					currentVRE.getGroupName().toLowerCase(), 
					RolesCkanGroupOrOrg.convertFromCapacity(localRoleInThisVre));
			
			Map<String, String> orgAndCapacity = new HashMap<String, String>();
			String username = user.getUsername();

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
				if(vre.getGroupId() == groupIdContext){
					continue;
				}else if(!isViewPerVREEnabled)
					orgAndCapacity.put(vre.getGroupName().toLowerCase(), 
							RolesCkanGroupOrOrg.convertToCkanCapacity(UserUtil.getLiferayHighestRoleInOrg(roleManager.listRolesByUserAndGroup(user.getUserId(), vre.getGroupId()))));
			}


			logger.debug("Thread for role association started. Organizations and roles are in the map: " + orgAndCapacity);
			Set<Entry<String, String>> entrySet = orgAndCapacity.entrySet();
			for (Entry<String, String> entry : entrySet) {
				if(instance.getOrganizationByName(entry.getKey()) != null){
					instance.checkRoleIntoOrganization(username, entry.getKey(), RolesCkanGroupOrOrg.convertFromCapacity(entry.getValue()));
					instance.assignRolesOtherOrganization(username, entry.getKey(), RolesCkanGroupOrOrg.convertFromCapacity(entry.getValue()));
				}
				else if(instance.getGroupByName(entry.getKey()) != null){
					RolesCkanGroupOrOrg roleInGroup = RolesCkanGroupOrOrg.convertFromCapacity(entry.getValue()); 
					roleInGroup = roleInGroup.equals(RolesCkanGroupOrOrg.EDITOR) ? RolesCkanGroupOrOrg.MEMBER : roleInGroup;
					instance.checkRoleIntoGroup(username, entry.getKey(), roleInGroup);
				}
			}
			logger.debug("Thread for role association ended");
		}catch(Exception e){
			logger.error("Failed while adding user to other organization", e);
		}
	}
}
