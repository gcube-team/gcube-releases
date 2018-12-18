package org.gcube.portlets_widgets.catalogue_sharing_widget.client;

import org.gcube.portlets_widgets.catalogue_sharing_widget.shared.ItemUrls;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("shareservices")
public interface ShareServices extends RemoteService {
	
	ItemUrls getPackageUrl(String uuid) throws Exception;
	
}
