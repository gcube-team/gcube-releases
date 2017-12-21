package org.gcube.portlets_widgets.catalogue_sharing_widget.client;

import org.gcube.portlets_widgets.catalogue_sharing_widget.shared.ItemUrls;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ShareServicesAsync {

	void getPackageUrl(String uuid, AsyncCallback<ItemUrls> callback);

}
