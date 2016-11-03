package it.eng.rdlab.um.role.service;


import it.eng.rdlab.um.exceptions.RoleRetrievalException;
import it.eng.rdlab.um.exceptions.UserManagementSystemException;
import it.eng.rdlab.um.role.beans.RoleModel;

import java.util.List;


/**
 * This interface defines the manager class that manages the roles.
 * 
 * @author Ciro Formisano
 *
 */
public interface RoleManager 
{
	public boolean createRole(RoleModel role) throws UserManagementSystemException;
	public boolean deleteRole(String roleId) throws UserManagementSystemException, RoleRetrievalException ;
	public boolean updateRole(RoleModel roleModel) throws UserManagementSystemException, RoleRetrievalException;
	public RoleModel getRole(String roleId) throws UserManagementSystemException, RoleRetrievalException;
	public List<RoleModel> listRoles() throws UserManagementSystemException,RoleRetrievalException;
	public List<RoleModel> listRoles (RoleModel filter)  throws UserManagementSystemException, RoleRetrievalException;
	public void close ();
	
}
