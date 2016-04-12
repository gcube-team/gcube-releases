package org.gcube.portlets.user.gcubeloggedin.client;

import org.gcube.portlets.user.gcubeloggedin.shared.VObject;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("LoggedinServiceImpl")
public interface LoggedinService extends RemoteService {

	VObject getSelectedRE(String portalURL);

	String getDefaultCommunityURL();
	
	String removeUserFromVRE();

}
