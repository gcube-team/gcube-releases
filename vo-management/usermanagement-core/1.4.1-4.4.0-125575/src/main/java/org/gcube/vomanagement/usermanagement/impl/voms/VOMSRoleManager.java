package org.gcube.vomanagement.usermanagement.impl.voms;

import java.util.HashMap;
import java.util.List;

import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.RoleRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementFileNotFoundException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementIOException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.model.RoleModel;



/**
 * VOMS plugin for the RoleManager interface.
 * 
 * @author Giulio Galiero
 *
 */
public class VOMSRoleManager implements
		org.gcube.vomanagement.usermanagement.RoleManager {

	public void assignRoleToUser(String groupId, String roleId, String userId)
			throws UserManagementSystemException, UserRetrievalFault,
			GroupRetrievalFault, RoleRetrievalFault {
		// TODO Auto-generated method stub
		
	}

	public void createRole(String groupName)
			throws UserManagementSystemException, GroupRetrievalFault,
			RoleRetrievalFault, UserManagementPortalException,
			UserManagementFileNotFoundException, UserManagementIOException {
		// TODO Auto-generated method stub
		
	}

	public boolean createRole(String roleName, String roleDescription,
			String groupName) throws UserManagementSystemException,
			RoleRetrievalFault, GroupRetrievalFault,
			UserManagementPortalException {
		// TODO Auto-generated method stub
		return false;
	}

	public void deleteRole(String roleName, String groupName)
			throws UserManagementSystemException, RoleRetrievalFault {
		// TODO Auto-generated method stub
		
	}

	public void dismissRoleFromUser(String groupId, String roleId, String userId)
			throws UserManagementSystemException, UserRetrievalFault,
			GroupRetrievalFault, RoleRetrievalFault {
		// TODO Auto-generated method stub
		
	}

	public RoleModel getRole(String roleId)
			throws UserManagementSystemException, RoleRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRoleId(String roleName, String groupName)
			throws UserManagementSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<String, String> listAllowedRoles(String groupName)
			throws UserManagementSystemException, GroupRetrievalFault,
			UserManagementFileNotFoundException, UserManagementIOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Deprecated
	public List<String> listRoles() throws UserManagementSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<RoleModel> listRolesByGroup(String groupId)
			throws UserManagementSystemException, GroupRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	public List<RoleModel> listRolesByUser(String userId)
			throws UserManagementSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<RoleModel> listRolesByUserAndGroup(String groupId, String userId)
			throws UserManagementSystemException, GroupRetrievalFault,
			UserRetrievalFault {
		// TODO Auto-generated method stub
		return null;
	}

	public void updatePredefinedRoles(HashMap<String, String> rolesMap,
			String groupType) throws UserManagementIOException,
			UserManagementFileNotFoundException {
		// TODO Auto-generated method stub
		
	}

	public void updateRole(String initialRoleName, String newRoleName,
			String roleDescription, String groupName)
			throws UserManagementSystemException, RoleRetrievalFault,
			NumberFormatException, UserManagementFileNotFoundException,
			UserManagementIOException, GroupRetrievalFault,
			UserManagementPortalException {
		// TODO Auto-generated method stub
		
	}

	public List<RoleModel> listAllRoles() throws UserManagementSystemException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAdmin(String userId) {
		// TODO Auto-generated method stub
		return false;
	}


	
}
