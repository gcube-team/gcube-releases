package org.gcube.portlets.admin.tokengeneratoradmin.client;

import org.gcube.portlets.admin.tokengeneratoradmin.shared.NodeToken;
import org.gcube.portlets.admin.tokengeneratoradmin.shared.PortRange;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("tokenserviceadmin")
public interface TokenServiceAdmin extends RemoteService {

	// generate method
	NodeToken createNodeToken(String ipaddress, short port);

	// get current user info
	//	String getCurrentUser();

	// get port range
	PortRange getRange();
}
