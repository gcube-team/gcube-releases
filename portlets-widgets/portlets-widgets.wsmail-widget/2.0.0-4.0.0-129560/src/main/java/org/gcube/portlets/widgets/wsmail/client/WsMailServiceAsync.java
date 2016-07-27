package org.gcube.portlets.widgets.wsmail.client;

import java.util.ArrayList;

import org.gcube.portlets.widgets.wsmail.shared.CurrUserAndPortalUsersWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>WsMailService</code>.
 */
public interface WsMailServiceAsync {

	void getWorkspaceUsers(AsyncCallback<CurrUserAndPortalUsersWrapper> callback);

	void sendToById(ArrayList<String> listContactsId,
			ArrayList<String> listAttachmentsId, String subject, String body,
			AsyncCallback<Boolean> callback);
}
