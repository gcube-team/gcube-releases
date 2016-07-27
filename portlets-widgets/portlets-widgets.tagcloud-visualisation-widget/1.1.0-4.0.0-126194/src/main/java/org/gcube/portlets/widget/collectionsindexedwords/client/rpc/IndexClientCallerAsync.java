/**
 * 
 */
package org.gcube.portlets.widget.collectionsindexedwords.client.rpc;


import org.gcube.portlets.widget.collectionsindexedwords.shared.IndexData;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IndexClientCallerAsync {
	public void getValues(Integer queryID, Integer maxStats, AsyncCallback<IndexData> callback);
	public void getClusterValues(Integer queryID, AsyncCallback<String> callback);
}
