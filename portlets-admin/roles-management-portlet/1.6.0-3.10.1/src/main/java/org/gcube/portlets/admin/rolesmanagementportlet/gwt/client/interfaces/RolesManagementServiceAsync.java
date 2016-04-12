package org.gcube.portlets.admin.rolesmanagementportlet.gwt.client.interfaces;


import java.util.ArrayList;

import org.gcube.portlets.admin.rolesmanagementportlet.gwt.shared.RoleInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * An asynchronous interface for the RolesManagement service following GWT RPC design
 * 
 * @author Panagiota Koltsida, NKUA
 */
public interface RolesManagementServiceAsync {

	public void getAvailableRoles(AsyncCallback<ArrayList<RoleInfo>> callback);
	
	public void deleteRoles(ArrayList<String> roleNames, AsyncCallback<Boolean> callback);
	
	public void createNewRole(String roleName, String roleDesc, AsyncCallback<Boolean> callback);

	public void listAllowedRoles(AsyncCallback<ArrayList<RoleInfo>> callback);

	public void sendEmailWithErrorToSupport(Throwable caught, AsyncCallback<Void> callback);

	public void updateRole(String roleName, String newRoleName,	String newRoleDescription, AsyncCallback<Boolean> callback);
	
}
