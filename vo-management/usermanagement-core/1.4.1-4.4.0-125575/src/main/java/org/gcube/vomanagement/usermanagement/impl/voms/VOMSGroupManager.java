package org.gcube.vomanagement.usermanagement.impl.voms;

import java.util.HashMap;
import java.util.List;

import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.model.GroupModel;
import org.gcube.vomanagement.usermanagement.model.RoleModel;

/**
 * VOMS plugin for the GroupManager interface.
 * 
 * @author Giulio Galiero
 *
 */
public class VOMSGroupManager implements GroupManager {

	public void assignSubGrouptoParentGroup(String subGroupId,
			String parentGroupId) {
		// TODO Auto-generated method stub
		
	}

	public GroupModel createRootVO(String RootVOName, String userId, String description) {
		// TODO Auto-generated method stub
		return null;
	}

	public GroupModel createVO(String VOName, String rootVOGroupId,
			String userId, String description) {
		// TODO Auto-generated method stub
		return null;
	}

	public GroupModel createVRE(String VREName, String VOGroupId, String userId, String description) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteGroup(String groupId) {
		// TODO Auto-generated method stub
		
	}

	public void dismissSubGroupFromParentGroup(String subGroupId,
			String parentGroupId) {
		// TODO Auto-generated method stub
		
	}

	public GroupModel getGroup(String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getGroupId(String groupName) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getGroupParentId(String groupId) {
		// TODO Auto-generated method stub
		return 0;
	}

	public GroupModel getRootVO() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRootVOName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getScope(String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isRootVO(String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isVO(String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean isVRE(String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<GroupModel> listGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<String, List<RoleModel>> listGroupsAndRolesByUser(
			String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<GroupModel> listGroupsByUser(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<GroupModel> listPendingGroupsByUser(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<GroupModel> listSubGroupsByGroup(String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateGroup(GroupModel group) {
		// TODO Auto-generated method stub
		
	}

	

}
