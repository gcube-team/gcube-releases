package org.gcube.portlets.user.gcubewidgets.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("scopeService")
public interface ScopeService extends RemoteService {
	boolean setScope(String portalURL);
}
