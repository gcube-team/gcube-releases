package org.gcube.portlets.user.gcubewidgets.client;


import org.gcube.portlets.user.gcubewidgets.client.rpc.ScopeService;
import org.gcube.portlets.user.gcubewidgets.client.rpc.ScopeServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window.Location;

public class ClientScopeHelper {
	public static ScopeServiceAsync getService() {
		return GWT.create(ScopeService.class);
		//Window.Location.getHref();
	}
	
	public static String extractOrgFriendlyURL(String portalURL) {
		String groupRegEx = "/group/";
		if (portalURL.contains(groupRegEx)) {
			String[] splits = portalURL.split(groupRegEx);
			String friendlyURL = splits[1];
			if (friendlyURL.contains("/")) {
				friendlyURL = friendlyURL.split("/")[0];
			} else {
				friendlyURL = friendlyURL.split("\\?")[0].split("\\#")[0];
			}
			return "/"+friendlyURL;
		}
		return null;
	}
	
}
