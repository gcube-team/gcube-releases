package org.gcube.portlets.user.statisticalalgorithmsimporter.client.workspace;

import org.gcube.portal.clientcontext.client.GCubeClientContext;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.rpc.StatAlgoImporterServiceAsync;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.type.SessionExpiredType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.client.utils.UtilsGXT3;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.Constants;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.exception.StatAlgoImporterSessionExpiredException;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
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
	private EventBus eventBus;
	
	public DownloadWidget(EventBus eventBus) {
		this.eventBus=eventBus;
	}

	public void download(String itemId) {

		StatAlgoImporterServiceAsync.INSTANCE.getItemDescription(itemId, new AsyncCallback<ItemDescription>() {

			@Override
			public void onFailure(Throwable caught) {
				if (caught instanceof StatAlgoImporterSessionExpiredException) {
					eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
				} else {
					Log.error("Error open file: " + caught.getLocalizedMessage());
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
