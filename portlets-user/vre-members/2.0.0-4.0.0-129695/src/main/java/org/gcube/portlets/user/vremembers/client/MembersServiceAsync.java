package org.gcube.portlets.user.vremembers.client;

import java.util.ArrayList;

import org.gcube.portlets.user.vremembers.shared.BelongingUser;
import org.gcube.portlets.user.vremembers.shared.VREGroup;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MembersServiceAsync {

	void getSiteUsers(AsyncCallback<ArrayList<BelongingUser>> callback);

	void getVREGroupUsers(String teamId, AsyncCallback<VREGroup> callback);
}
