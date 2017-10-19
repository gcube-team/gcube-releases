package org.gcube.portlets.admin.ishealthmonitor.client.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>ISMonitorService</code>.
 */
public interface ISMonitorServiceAsync {

	void getResourceTypeTree(String scope,
			AsyncCallback<HashMap<String, ArrayList<String>>> callback);

}
