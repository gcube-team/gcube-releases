package org.gcube.portal.clientcontext.client;

import com.google.gwt.core.client.EntryPoint;

/**
 * 
 * @author Massimiliano Assante CNR-ISTI
 *
 */
public class GCubeClientContext implements EntryPoint {
	public static final String VRE_ID_ATTR_NAME = "gcube-vreid";
	public static final String USER_ID_ATTR_NAME = "gcube-userId";

	public void onModuleLoad() {}

	public static native String getCurrentContextId() /*-{
		var groupId;
		if ($wnd.Liferay != null) {
			groupId = $wnd.Liferay.ThemeDisplay.getScopeGroupId();
				console.log("context id is = " + groupId);
		}
		return groupId;
	}-*/;
	
	public static native String getCurrentUserId() /*-{
	var userId;
	if ($wnd.Liferay != null) {
		userId = $wnd.Liferay.ThemeDisplay.getUserId();
			console.log("current userid is = " + userId);
	}
	return userId;
	}-*/;
	
}
