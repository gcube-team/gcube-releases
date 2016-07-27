package org.gcube.portlets.user.questions.client;

import java.util.ArrayList;

import org.gcube.portal.databook.shared.UserInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface QuestionsServiceAsync {
	void getManagers(AsyncCallback<ArrayList<UserInfo>> callback);
	void removeUserFromVRE(AsyncCallback<String> callback);
	void isLeaveButtonAvailable(String currentUrl,
			AsyncCallback<Boolean> callback);
}
