package org.gcube.portlet.user.my_vres.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.gcube.portlet.user.my_vres.shared.AuthorizationBean;
import org.gcube.portlet.user.my_vres.shared.VRE;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface MyVREsService extends RemoteService {
	LinkedHashMap<String, ArrayList<VRE>> getUserVREs();
	
	String getSiteLandingPagePath();
	
	AuthorizationBean getOAuthTempCode(String context, String state, String clientId, String authorisedRedirectURL);
}
