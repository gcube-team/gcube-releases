package org.gcube.portlets.admin.createusers.client;

import java.util.List;

import org.gcube.portlets.admin.createusers.shared.VreUserBean;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("usersservice")
public interface HandleUsersService extends RemoteService {

	/**
	 * Register this user to the current vre.
	 * @param name
	 * @param surname
	 * @param institution/organization
	 * @param email
	 * @param sendEmail
	 * @param isMale
	 * @return the registered user information or null if the registration failed
	 */
	VreUserBean register(String name, String surname, String institution, String email, boolean sendEmail, boolean isMale);

	/**
	 * Retrieve the list of already registered users.
	 * @return a list of already registered users or null in case of error
	 */
	List<VreUserBean> getAlreadyRegisterdUsers();
	
	/**
	 * Delete an already invited user that didn't change his password.
	 * @param email
	 */
	public boolean deleteInvitedUser(String email);
	
	/**
	 * Send the registration email to this user
	 * @param email
	 */
	public void sendEmailToUser(String email);

}
