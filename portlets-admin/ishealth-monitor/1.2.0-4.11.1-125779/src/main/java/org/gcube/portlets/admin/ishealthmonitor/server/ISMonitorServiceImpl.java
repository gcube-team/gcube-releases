package org.gcube.portlets.admin.ishealthmonitor.server;

import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.admin.ishealthmonitor.client.async.ISMonitorService;
import org.gcube.resourcemanagement.support.server.gcube.CacheManager;
import org.gcube.resourcemanagement.support.server.gcube.ISClientRequester;
import org.gcube.resourcemanagement.support.server.managers.scope.ScopeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ISMonitorServiceImpl extends RemoteServiceServlet implements ISMonitorService {
	
	private static final Logger _log = LoggerFactory.getLogger(ISMonitorServiceImpl.class);
	
	
	@Override
	public HashMap<String, ArrayList<String>> getResourceTypeTree(String scope) {
		CacheManager cm = new CacheManager();
		cm.setUseCache(false);
		try {
			ScopeBean gscope = ScopeManager.getScope(scope);
			HashMap<String, ArrayList<String>> results = ISClientRequester.getResourcesTree(cm, gscope);
			return results;
		} catch (Exception e) {
			_log.error("while applying resource get", e);
			return null;
		}
	}
	
	
	
}
