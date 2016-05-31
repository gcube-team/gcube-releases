package org.gcube.portlets.user.socialprofile.client;

import org.gcube.portlets.user.socialprofile.shared.UserContext;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SocialServiceAsync {

	void getUserContext(String userid, AsyncCallback<UserContext> callback);

	void saveHeadline(String newHeadline, AsyncCallback<Boolean> callback);

	void saveIsti(String institution, AsyncCallback<Boolean> callback);

	void fetchUserProfile(String authCode, String redirectURI,
			AsyncCallback<String> callback);
	
}
