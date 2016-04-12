package org.gcube.portlets.user.vremembers.client;

import java.util.ArrayList;

import org.gcube.portlets.user.vremembers.shared.BelongingUser;


import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MembersServiceAsync {

	void getOrganizationUsers(AsyncCallback<ArrayList<BelongingUser>> callback);
}
