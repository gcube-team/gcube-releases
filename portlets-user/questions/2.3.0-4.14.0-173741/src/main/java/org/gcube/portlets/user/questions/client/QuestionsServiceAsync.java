package org.gcube.portlets.user.questions.client;

import java.util.ArrayList;

import org.gcube.portlets.user.questions.shared.GroupDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface QuestionsServiceAsync {
	void getGroups(AsyncCallback<ArrayList<GroupDTO>> callback);


}
