package org.gcube.vomanagement.usermanagement.ws;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementFileNotFoundException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementIOException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.model.RoleModel;
import org.gcube.vomanagement.usermanagement.ws.utils.StringCoupleWrapper;

@WebService(name = "LiferaySOAPRoleManager", serviceName = "LiferaySOAPRoleManager")
public class LiferaySOAPRoleManager {

	LiferayRoleManager liferayRoleManager;

	public LiferaySOAPRoleManager() {
		this.liferayRoleManager = new LiferayRoleManager();
	}

	@WebMethod
	public RoleModel getRole(String roleId) throws UserManagementSystemException, RoleRetrievalFault {
		return this.liferayRoleManager.getRole(roleId);
	}
	
	@WebMethod
	public String getRoleId(String roleName, String groupName) throws UserManagementSystemException {
		return this.liferayRoleManager.getRoleId(roleName, groupName);
	}
	
	
	@WebMethod
	public List<StringCoupleWrapper> listAllowedRoles(String groupName)  throws UserManagementSystemException, GroupRetrievalFault, UserManagementIOException, UserManagementFileNotFoundException {
		List<StringCoupleWrapper> wrapper = new ArrayList<StringCoupleWrapper>();
		
		Map<String, String> resMap = liferayRoleManager.listAllowedRoles(groupName);
		
		if (resMap != null)
		{
		
			Iterator<String> keys = resMap.keySet().iterator();
			
			while (keys.hasNext())
			{
				String key = keys.next();
				String value = resMap.get(key);
				
				wrapper.add(new StringCoupleWrapper(key, value));
			}
		
		}
		
		return wrapper;
	}
	
	@WebMethod
	public List<String> listRoles() throws UserManagementSystemException {
		return this.liferayRoleManager.listRoles();
	}
	
	@WebMethod
	public List<RoleModel> listRolesByGroup(String groupId) throws UserManagementSystemException, GroupRetrievalFault, UserManagementFileNotFoundException, UserManagementIOException {
		return this.liferayRoleManager.listRolesByGroup(groupId);
	}
	
	@WebMethod
	public List<RoleModel> listRolesByUser(String userId) throws UserManagementSystemException {
		return this.liferayRoleManager.listRolesByUser(userId);
	}

	@WebMethod
	public List<RoleModel> listRolesByUserAndGroup(String groupId, String userId) throws UserManagementSystemException, GroupRetrievalFault {
		return this.liferayRoleManager.listRolesByUserAndGroup(groupId, userId);
	}
	
}
