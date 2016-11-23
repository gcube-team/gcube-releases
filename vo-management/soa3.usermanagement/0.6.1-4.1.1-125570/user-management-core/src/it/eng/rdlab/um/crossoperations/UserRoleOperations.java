package it.eng.rdlab.um.crossoperations;

import it.eng.rdlab.um.exceptions.RoleRetrievalException;
import it.eng.rdlab.um.exceptions.UserManagementSystemException;
import it.eng.rdlab.um.exceptions.UserRetrievalException;
import it.eng.rdlab.um.role.beans.RoleModel;
import it.eng.rdlab.um.user.beans.UserModel;

import java.util.List;



public interface UserRoleOperations 
{
	public boolean assignRoleToUser(String roleId, String userId) throws UserManagementSystemException, UserRetrievalException,RoleRetrievalException;
	public boolean dismissRoleFromUser(String roleId, String userId)throws UserManagementSystemException, UserRetrievalException, RoleRetrievalException;
	public List<RoleModel> listRolesByUser(String userId) throws UserManagementSystemException, UserRetrievalException,RoleRetrievalException;
	public List<UserModel> listUserByRole(String roleId) throws UserManagementSystemException, UserRetrievalException,RoleRetrievalException;
	public void close ();
}
