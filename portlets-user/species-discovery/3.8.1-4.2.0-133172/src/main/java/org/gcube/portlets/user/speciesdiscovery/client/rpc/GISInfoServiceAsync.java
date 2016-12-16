package org.gcube.portlets.user.speciesdiscovery.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GISInfoServiceAsync {

	
	public void getGisLinkByLayerName(String layername, AsyncCallback<String> callback);
}
