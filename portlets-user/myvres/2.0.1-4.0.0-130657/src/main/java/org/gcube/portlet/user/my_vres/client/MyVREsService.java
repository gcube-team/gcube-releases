package org.gcube.portlet.user.my_vres.client;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.gcube.portlet.user.my_vres.shared.VRE;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface MyVREsService extends RemoteService {
	LinkedHashMap<String, ArrayList<VRE>> getUserVREs();
	
	void loadLayout(String scope, String URL);
	
	String showMoreVREs();
	
	String getSiteLandingPagePath();
}
