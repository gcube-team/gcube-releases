package org.gcube.portlets.admin.invitessent.client;

import java.util.ArrayList;

import org.gcube.portal.databook.shared.Invite;
import org.gcube.portal.databook.shared.InviteStatus;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface InvitesServiceAsync {

	void getInvites(InviteStatus[] statuses,
			AsyncCallback<ArrayList<Invite>> callback);

}
