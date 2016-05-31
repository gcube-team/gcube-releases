package org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.interfaces;

import java.util.ArrayList;

import org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.exceptions.CurrentGroupRetrievalException;
import org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.exceptions.RetrieveAllowedRolesException;
import org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.exceptions.RoleCreationException;
import org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.exceptions.RoleUpdateException;
import org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.exceptions.RolesRetrievalException;
import org.gcube.portlets.admin.rolesmanagementportlet.gwt.shared.RoleInfo;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * A RemoteService interface for the RolesManagement service following GWT RPC design
 * 
 * @author Panagiota Koltsida, NKUA
 */
public interface RolesManagementService  extends RemoteService {

	public ArrayList<RoleInfo> getAvailableRoles() throws RolesRetrievalException, CurrentGroupRetrievalException;
	
	public ArrayList<RoleInfo> listAllowedRoles() throws CurrentGroupRetrievalException, RetrieveAllowedRolesException;
	
	public Boolean deleteRoles(ArrayList<String> roleNames);
	
	public Boolean createNewRole(String roleName, String roleDesc) throws RoleCreationException;
	
	public Boolean updateRole(String roleName, String newRoleName, String newRoleDescription) throws RoleUpdateException, RolesRetrievalException, CurrentGroupRetrievalException;
	
	public void sendEmailWithErrorToSupport(Throwable caught);
	
}
