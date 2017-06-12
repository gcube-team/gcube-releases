package org.gcube.portlets.admin.createusers.client;

import java.util.List;

import org.gcube.portlets.admin.createusers.shared.VreUserBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async version of the service for the client.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public interface HandleUsersServiceAsync {

	void getAlreadyRegisterdUsers(AsyncCallback<List<VreUserBean>> callback);

	void register(String name, String surname, String institution, String email,
			boolean sendEmail, boolean isMale, AsyncCallback<VreUserBean> callback);

	void deleteInvitedUser(String email, AsyncCallback<Boolean> callback);

	void sendEmailToUser(String email, AsyncCallback<Void> callback);
}
