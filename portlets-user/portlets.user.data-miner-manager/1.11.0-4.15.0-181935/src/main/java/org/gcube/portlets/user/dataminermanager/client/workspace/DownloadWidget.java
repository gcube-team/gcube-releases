package org.gcube.portlets.user.dataminermanager.client.workspace;

import org.gcube.data.analysis.dataminermanagercl.shared.workspace.ItemDescription;
import org.gcube.portal.clientcontext.client.GCubeClientContext;
import org.gcube.portlets.user.dataminermanager.client.common.EventBusProvider;
import org.gcube.portlets.user.dataminermanager.client.events.SessionExpiredEvent;
import org.gcube.portlets.user.dataminermanager.client.rpc.DataMinerPortletServiceAsync;
import org.gcube.portlets.user.dataminermanager.client.util.UtilsGXT3;
import org.gcube.portlets.user.dataminermanager.shared.Constants;
import org.gcube.portlets.user.dataminermanager.shared.exception.SessionExpiredServiceException;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class DownloadWidget {

	public DownloadWidget() {

	}

	public void download(String itemId) {
		DataMinerPortletServiceAsync.INSTANCE.getItemDescription(itemId, new AsyncCallback<ItemDescription>() {

			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof SessionExpiredServiceException) {
					EventBusProvider.INSTANCE.fireEvent(new SessionExpiredEvent());
				} else {
					Log.error("Error open item: " + caught.getLocalizedMessage(), caught);
					UtilsGXT3.alert("Error", caught.getLocalizedMessage());
				}
				caught.printStackTrace();

			}

			@Override
			public void onSuccess(ItemDescription itemDownloadInfo) {
				Log.debug("Retrieved item download info: " + itemDownloadInfo);
				requestDownload(itemDownloadInfo);
			}

		});
	}

	private void requestDownload(ItemDescription itemDescription) {
		switch (itemDescription.getType()) {
		case "AbstractFileItem":
		case "FolderItem":
			executeDownload(itemDescription);
			break;
		default:
			UtilsGXT3.info("Attention", "This item does not support download operation!");
			break;
		}

	}

	private void executeDownload(ItemDescription itemDescription) {
		StringBuilder url = new StringBuilder();
		url.append(GWT.getModuleBaseURL());
		url.append(Constants.DOWNLOAD_SERVLET + "/" + itemDescription.getName() + "?itemId=" + itemDescription.getId()
				+ "&" + Constants.CURR_GROUP_ID + "=" + GCubeClientContext.getCurrentContextId());

		Window.open(URL.encode(url.toString()), "_blank", "");

	}

}
