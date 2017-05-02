package org.gcube.vomanagement.usermanagement;


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
 * This interface defines the manager class that manages the roles.
 * 
 * @author Giulio Galiero
 *
 */
public interface RoleManager {
	
	boolean isAdmin(String userId);

	public void assignRoleToUser(String groupId, String roleId, String userId) throws UserManagementSystemException, UserRetrievalFault, GroupRetrievalFault,RoleRetrievalFault;
	
	public void createRole(String groupName) throws UserManagementSystemException, GroupRetrievalFault, RoleRetrievalFault, UserManagementPortalException, UserManagementFileNotFoundException, UserManagementIOException;

	public boolean createRole(String roleName,String roleDescription, String groupName) throws UserManagementSystemException, RoleRetrievalFault, GroupRetrievalFault, UserManagementPortalException;
	
	public void deleteRole(String roleName, String groupName) throws UserManagementSystemException, RoleRetrievalFault ;
	
	public void dismissRoleFromUser(String groupId, String roleId, String userId)throws UserManagementSystemException, UserRetrievalFault, GroupRetrievalFault,RoleRetrievalFault;
	
	public RoleModel getRole(String roleId) throws UserManagementSystemException, RoleRetrievalFault;

	public String getRoleId(String roleName, String groupName) throws UserManagementSystemException;

	public HashMap<String, String>  listAllowedRoles(String groupName) throws UserManagementSystemException, GroupRetrievalFault,  UserManagementFileNotFoundException, UserManagementIOException;
	
	@Deprecated
	public List<String> listRoles() throws UserManagementSystemException;
	
	public List<RoleModel> listAllRoles() throws UserManagementSystemException;
	
	public List<RoleModel> listRolesByGroup(String groupId) throws UserManagementSystemException, GroupRetrievalFault, UserManagementFileNotFoundException, UserManagementIOException;
	
	public List<RoleModel> listRolesByUser(String userId) throws UserManagementSystemException;

	public List<RoleModel> listRolesByUserAndGroup(String groupId, String userId) throws UserManagementSystemException, GroupRetrievalFault,UserRetrievalFault ;
	
	public void updateRole(String initialRoleName, String newRoleName, String roleDescription, String groupName) throws UserManagementSystemException, RoleRetrievalFault, NumberFormatException,  UserManagementFileNotFoundException, UserManagementIOException, GroupRetrievalFault,UserManagementPortalException;
	
	public void updatePredefinedRoles(HashMap<String,String> rolesMap, String groupType) throws UserManagementIOException, UserManagementFileNotFoundException ;

}
