package org.gcube.vomanagement.usermanagement.ws;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.GroupModel;
import org.gcube.vomanagement.usermanagement.model.RoleModel;
import org.gcube.vomanagement.usermanagement.ws.utils.StringRoleListCoupleWrapper;

@WebService(name = "LiferaySOAPGroupManager", serviceName = "LiferaySOAPGroupManager")
public class LiferaySOAPGroupManager {
	
	LiferayGroupManager liferayGroupManager;

	public LiferaySOAPGroupManager() {
		this.liferayGroupManager = new LiferayGroupManager();
	}

	@WebMethod
	public GroupModel getGroup(String groupId) throws UserManagementSystemException, GroupRetrievalFault {
		return this.liferayGroupManager.getGroup(groupId);
	}
	
	@WebMethod
	public String getGroupId(String groupName) throws UserManagementSystemException, GroupRetrievalFault {
		return this.liferayGroupManager.getGroupId(groupName);
	}
	
	@WebMethod
	public long getGroupParentId(String groupId) throws UserManagementSystemException, GroupRetrievalFault {
		return this.liferayGroupManager.getGroupParentId(groupId);
	}
	
	@WebMethod
	public GroupModel getRootVO() throws UserManagementSystemException, GroupRetrievalFault {
		return this.liferayGroupManager.getRootVO();
	}
	
	@WebMethod
	public String getRootVOName() throws UserManagementSystemException, GroupRetrievalFault {
		return this.liferayGroupManager.getRootVOName();
	}
	
	@WebMethod
	public String getScope(String groupId) throws UserManagementSystemException, GroupRetrievalFault {
		return this.liferayGroupManager.getScope(groupId);
	}

	@WebMethod
	public Boolean isRootVO(String groupId) throws UserManagementSystemException, GroupRetrievalFault {
		return this.liferayGroupManager.isRootVO(groupId);
	}
	
	@WebMethod
	public Boolean isVO(String groupId) throws UserManagementSystemException, GroupRetrievalFault {
		return this.liferayGroupManager.isVO(groupId);
	}
	
	@WebMethod
	public Boolean isVRE(String groupId) throws UserManagementSystemException, GroupRetrievalFault {
		return this.liferayGroupManager.isVRE(groupId);
	}
	
	@WebMethod
	public List<GroupModel> listGroups() throws UserManagementSystemException {	
		return this.liferayGroupManager.listGroups();
	}



	
	@WebMethod
	public List<StringRoleListCoupleWrapper> listGroupsAndRolesByUser(String userId) throws UserManagementPortalException, UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault {
		
		List<StringRoleListCoupleWrapper> wrapper = new ArrayList<StringRoleListCoupleWrapper>();
		Map<String, List<RoleModel>> resMap = this.liferayGroupManager.listGroupsAndRolesByUser(userId);
		
		if (resMap != null)
		{
			Iterator<String> keys = resMap.keySet().iterator();
			
			while (keys.hasNext())
			{
				String key = keys.next();
				List<RoleModel> value = resMap.get(key);
				
				wrapper.add(new StringRoleListCoupleWrapper(key,value));
			}
		}
		
		return wrapper;
	}
	
	@WebMethod
	public List<GroupModel> listGroupsByUser(String userId) throws UserManagementSystemException {
		return this.liferayGroupManager.listGroupsByUser(userId);
	}
	
	@WebMethod
	public List<GroupModel> listPendingGroupsByUser(String userId) throws UserManagementSystemException {
		return this.liferayGroupManager.listPendingGroupsByUser(userId);
	}
	
	@WebMethod
	public List<GroupModel> listSubGroupsByGroup(String groupIdn) throws UserManagementSystemException, GroupRetrievalFault {
		return this.liferayGroupManager.listSubGroupsByGroup(groupIdn);
	}
	
}

