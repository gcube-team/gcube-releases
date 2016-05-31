package org.gcube.portlets.user.gcubewidgets.client;

import org.gcube.portlets.user.gcubewidgets.client.rpc.ScopeService;
import org.gcube.portlets.user.gcubewidgets.client.rpc.ScopeServiceAsync;

import com.google.gwt.core.client.GWT;

public class ClientScopeHelper {
	public static ScopeServiceAsync getService() {
		return GWT.create(ScopeService.class);
		//Window.Location.getHref();
	}
}
