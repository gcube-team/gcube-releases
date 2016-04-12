package org.gcube.portlets.admin.usersmanagementportlet.gwt.client.interfaces;

import java.util.ArrayList;

import org.gcube.portlets.admin.usersmanagementportlet.gwt.client.exceptions.CurrentGroupRetrievalException;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.client.exceptions.GroupAssignmentException;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.client.exceptions.PendingUsersRetrievalException;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.client.exceptions.RegisteredUsersRetrievalException;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.client.exceptions.RolesRetrievalException;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.client.exceptions.UserInfoRetrievalException;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.client.exceptions.UserRemovalFailureException;
import org.gcube.portlets.admin.usersmanagementportlet.gwt.shared.UserInfo;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * A RemoteService interface for the UsersManagement service following GWT RPC design
 * 
 * @author Panagiota Koltsida, NKUA
 */
public interface UsersManagementService  extends RemoteService {
		
	public String[] getAvailableRolesByCurrentGroup();
	
	public ArrayList<UserInfo> getAllRegisteredUsers();
	
	public ArrayList<UserInfo> getRegisteredUsersForaVO();
	
	public Boolean removeUsersFromVO(String users[]) throws CurrentGroupRetrievalException, RolesRetrievalException, UserRemovalFailureException, UserInfoRetrievalException;
	
	public Boolean denyRequests(String users[], boolean sendCustomMailToRejectedUsers) throws CurrentGroupRetrievalException, RolesRetrievalException, UserRemovalFailureException, UserInfoRetrievalException;
	
	public String updateUserRoles(String username, String rolesAndValues[][]);
	
	public ArrayList<UserInfo> getUnregisteredUsersForVO();
	
	public Boolean addUsersToVO(ArrayList<String> users) throws RolesRetrievalException, UserInfoRetrievalException, CurrentGroupRetrievalException, GroupAssignmentException;
	
	public ArrayList<UserInfo> getUsersRequests() throws PendingUsersRetrievalException;
	
	public Boolean addNewUsersToVO(ArrayList<String> users) throws CurrentGroupRetrievalException;

	public void sendEmailToRegisteredUsers(String subject, String body) throws CurrentGroupRetrievalException, RegisteredUsersRetrievalException, UserInfoRetrievalException;
	
	public Integer getNumberOfUserThatReceiveNots() throws RegisteredUsersRetrievalException;
	
	public void sendEmailWithErrorToSupport(Throwable caught);
	
	public void sendEmail(ArrayList<String> emails, String subject, String body);
	
}
