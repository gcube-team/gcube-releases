package org.gcube.portlets.admin.resourcesweeper.client.async;

import java.util.List;

import org.gcube.resourcemanagement.support.shared.util.SweeperActions;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>SweeperService</code>.
 */
public interface SweeperServiceAsync {

	void getSweepElems(String scope, SweeperActions action,
			AsyncCallback<List<String>> callback);

	void applySweep(String scope, List<ModelData> elems,
			AsyncCallback<Boolean> callback);
	
}
