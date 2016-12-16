package org.gcube.portlets.widgets.inviteswidget.client;

import org.gcube.portal.databook.shared.InviteOperationResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface InviteServiceAsync {

	void sendInvite(String name, String lastName, String email,
			AsyncCallback<InviteOperationResult> callback);

}
