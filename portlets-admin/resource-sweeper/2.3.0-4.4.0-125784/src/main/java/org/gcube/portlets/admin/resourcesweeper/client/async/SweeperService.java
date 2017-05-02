package org.gcube.portlets.admin.resourcesweeper.client.async;

import java.util.List;

import org.gcube.resourcemanagement.support.shared.util.SweeperActions;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("sweeper")
public interface SweeperService extends RemoteService {
	List<String> getSweepElems(final String scope, final SweeperActions action);
	Boolean applySweep(final String scope, final List<ModelData> elems);
}
