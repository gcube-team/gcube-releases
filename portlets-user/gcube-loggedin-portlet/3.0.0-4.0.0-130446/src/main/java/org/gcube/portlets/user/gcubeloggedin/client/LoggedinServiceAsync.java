package org.gcube.portlets.user.gcubeloggedin.client;

import org.gcube.portlets.user.gcubeloggedin.shared.VObject;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface LoggedinServiceAsync {
	void getSelectedRE(String portalURL, AsyncCallback<VObject> callback);

	void saveVREDescription(String toSave, AsyncCallback<String> callback);
}
