package org.gcube.portlets.user.socialprofile.client;

import org.gcube.portlets.user.socialprofile.shared.UserContext;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("socialService")
public interface SocialService extends RemoteService {
	UserContext getUserContext(String userid);
	
	Boolean saveHeadline(String newHeadline);
	
	Boolean saveIsti(String institution);
	
	String fetchUserProfile(String authCode, String redirectURI);
}
