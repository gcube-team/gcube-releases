package org.gcube.portlets.user.gcubewidgets.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>ScopeService</code>.
 */
public interface ScopeServiceAsync {

	void setScope(String portalURL, AsyncCallback<Boolean> callback);

	
}
