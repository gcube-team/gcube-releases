package org.gcube.portlets.admin.usersmanagementportlet.gwt.client.interfaces;

import java.util.ArrayList;

import org.gcube.portlets.admin.usersmanagementportlet.gwt.shared.UserInfo;


import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public interface UsersManagementServiceAsync {
	
	public void getAvailableRolesByCurrentGroup(AsyncCallback<String[]> callback);
	
	public void getAllRegisteredUsers(AsyncCallback<ArrayList<UserInfo>> callback);
	
	public void getRegisteredUsersForaVO(AsyncCallback<ArrayList<UserInfo>> callback);
	
	public void removeUsersFromVO(String users[], AsyncCallback<Boolean> callback);
	
	public void updateUserRoles(String username, String rolesAndValues[][], AsyncCallback<String> callback);
	
	public void getUnregisteredUsersForVO(AsyncCallback<ArrayList<UserInfo>> callback);
	
	public void addUsersToVO(ArrayList<String> users, AsyncCallback<Boolean> callback);
	
	public void getUsersRequests(AsyncCallback<ArrayList<UserInfo>> callback);
	
	public void addNewUsersToVO(ArrayList<String> users, AsyncCallback<Boolean> callback);
	
	public void sendEmailToRegisteredUsers(String subject, String body, AsyncCallback<Void> callback);
	
	public void getNumberOfUserThatReceiveNots(AsyncCallback<Integer> callback);

	public void sendEmailWithErrorToSupport(Throwable caught, AsyncCallback<Void> callback);

	public void denyRequests(String users[], boolean sendCustomMailToRejectedUsers, AsyncCallback<Boolean> callback);

	void sendEmail(ArrayList<String> emails, String subject, String body,
			AsyncCallback<Void> callback);
	
}
