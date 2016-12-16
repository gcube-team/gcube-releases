package it.eng.rdlab.um.crossoperations;

import it.eng.rdlab.um.exceptions.GroupRetrievalException;
import it.eng.rdlab.um.exceptions.RoleRetrievalException;
import it.eng.rdlab.um.exceptions.UserManagementSystemException;
import it.eng.rdlab.um.group.beans.GroupModel;
import it.eng.rdlab.um.role.beans.RoleModel;

import java.util.List;



public interface GroupRoleOperations 
{
	public void assignRoleToGroup(String roleId, String groupId) throws UserManagementSystemException, GroupRetrievalException,RoleRetrievalException;
	public void dismissRoleFromGroup(String roleId, String groupId)throws UserManagementSystemException, GroupRetrievalException, RoleRetrievalException;
	public List<RoleModel> listRolesByGroup(String groupId) throws UserManagementSystemException, GroupRetrievalException,RoleRetrievalException;
	public List<GroupModel> listGroupsByRole(String roleId) throws UserManagementSystemException, GroupRetrievalException,RoleRetrievalException;
	public void close ();
}
