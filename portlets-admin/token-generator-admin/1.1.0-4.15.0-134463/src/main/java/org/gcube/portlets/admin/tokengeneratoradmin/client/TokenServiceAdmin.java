package org.gcube.portlets.admin.tokengeneratoradmin.client;

import java.util.List;

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
	NodeToken createNodeToken(String ipaddress, short port, String context);
	
	// get port range
	PortRange getRange();
	
	// retrieve contexts starting from the current one and inspecting below
	List<String> retrieveListContexts();
}
