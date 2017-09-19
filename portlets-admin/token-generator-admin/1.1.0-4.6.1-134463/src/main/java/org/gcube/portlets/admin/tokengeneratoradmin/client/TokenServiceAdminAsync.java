package org.gcube.portlets.admin.tokengeneratoradmin.client;

import java.util.List;

import org.gcube.portlets.admin.tokengeneratoradmin.shared.NodeToken;
import org.gcube.portlets.admin.tokengeneratoradmin.shared.PortRange;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async version for rpc.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public interface TokenServiceAdminAsync {

	void createNodeToken(String ipaddress, short port, String context,
			AsyncCallback<NodeToken> callback);

	void getRange(AsyncCallback<PortRange> callback);

	void retrieveListContexts(AsyncCallback<List<String>> callback);
}
