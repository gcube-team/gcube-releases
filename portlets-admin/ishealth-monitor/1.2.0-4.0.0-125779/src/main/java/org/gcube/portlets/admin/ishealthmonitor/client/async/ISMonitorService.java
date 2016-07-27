package org.gcube.portlets.admin.ishealthmonitor.client.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("ishealth")
public interface ISMonitorService extends RemoteService {
	HashMap<String, ArrayList<String>> getResourceTypeTree(String scope);
}
