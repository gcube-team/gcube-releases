package org.gcube.vomanagement.usermanagement;

import java.util.HashMap;
import java.util.List;

import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementNameException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.model.GroupModel;
import org.gcube.vomanagement.usermanagement.model.RoleModel;


/**
 * This interface defines the class that manages the groups.
 * 
 * @author Giulio Galiero
 *
 */
public interface GroupManager {
	
	//public GroupModel createGroup(String groupName, String userId) throws UserManagementException;
	
	public GroupModel createRootVO(String RootVOName, String userId, String description)throws UserManagementNameException, UserManagementSystemException, UserRetrievalFault ,GroupRetrievalFault, UserManagementPortalException;
	
	public GroupModel createVO(String VOName, String rootVOGroupId,  String userId, String description)throws UserManagementNameException, UserManagementSystemException, UserRetrievalFault, GroupRetrievalFault, UserManagementPortalException;
	
	public GroupModel createVRE(String VREName, String VOGroupId, String userId, String description)throws UserManagementNameException, UserManagementSystemException, UserRetrievalFault, GroupRetrievalFault, UserManagementPortalException;

	public void deleteGroup(String groupId) throws UserManagementSystemException, GroupRetrievalFault;
	
	public void updateGroup(GroupModel group) throws UserManagementSystemException, GroupRetrievalFault;
	
	public GroupModel getGroup(String groupId) throws UserManagementSystemException, GroupRetrievalFault;
	
	public GroupModel getRootVO() throws UserManagementSystemException, GroupRetrievalFault;
	
	public String getRootVOName() throws UserManagementSystemException, GroupRetrievalFault;
	
	public List<GroupModel> listGroups() throws UserManagementSystemException;
	
	public List<GroupModel> listGroupsByUser(String userId) throws UserManagementSystemException;
	
	public List<GroupModel> listPendingGroupsByUser(String userId) throws UserManagementSystemException;

	public List<GroupModel> listSubGroupsByGroup(String groupId) throws UserManagementSystemException, GroupRetrievalFault;
	
	public HashMap<String, List<RoleModel>> listGroupsAndRolesByUser(String userId) throws UserManagementSystemException;
	
	public void assignSubGrouptoParentGroup(String subGroupId, String parentGroupId) throws UserManagementSystemException, GroupRetrievalFault;
	
	public void dismissSubGroupFromParentGroup(String subGroupId, String parentGroupId) throws UserManagementSystemException, GroupRetrievalFault;
	
	public String getGroupId(String groupName) throws UserManagementSystemException, GroupRetrievalFault;
	
	public long  getGroupParentId(String groupId) throws UserManagementSystemException, GroupRetrievalFault;
	
	public String getScope(String groupId) throws UserManagementSystemException, GroupRetrievalFault;
	
	public Boolean isRootVO(String groupId) throws UserManagementSystemException, GroupRetrievalFault;
	
	public Boolean isVO(String groupId) throws UserManagementSystemException, GroupRetrievalFault;
	
	public Boolean isVRE(String groupId) throws UserManagementSystemException, GroupRetrievalFault;
	
}
