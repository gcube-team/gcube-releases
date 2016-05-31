package org.gcube.portlets.user.contactinformation.client;

import org.gcube.portlets.user.contactinformation.shared.UserContext;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ContactInfoServiceAsync {

	

	void getUserContext(String userid, AsyncCallback<UserContext> callback);

	
}
