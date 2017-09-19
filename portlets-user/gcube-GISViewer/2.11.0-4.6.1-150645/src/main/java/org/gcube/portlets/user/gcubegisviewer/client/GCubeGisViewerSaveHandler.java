/**
 * 
 */
package org.gcube.portlets.user.gcubegisviewer.client;

import java.util.Map;

import org.gcube.portlets.user.gcubegisviewer.client.event.HasSaveHandlers;
import org.gcube.portlets.user.gcubegisviewer.client.event.SaveEvent;
import org.gcube.portlets.user.gisviewer.client.GisViewerSaveHandler;
import org.gcube.portlets.widgets.wsexplorer.client.notification.WorkspaceExplorerSaveNotification.WorskpaceExplorerSaveNotificationListener;
import org.gcube.portlets.widgets.wsexplorer.client.save.WorkspaceExplorerSaveDialog;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Class GCubeGisViewerSaveHandler.
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * @author updated by Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 27, 2015
 */
public class GCubeGisViewerSaveHandler implements GisViewerSaveHandler {
	
	protected static GCubeGisViewerServiceAsync service = (GCubeGisViewerServiceAsync)GWT.create(GCubeGisViewerService.class);		
	protected HasSaveHandlers hasSaveHandlers;
	
	/**
	 * Sets the checks for save handlers.
	 *
	 * @param hasSaveHandlers the hasSaveHandlers to set
	 */
	public void setHasSaveHandlers(HasSaveHandlers hasSaveHandlers) {
		this.hasSaveHandlers = hasSaveHandlers;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.GisViewerSaveHandler#saveLayerImage(java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public void saveLayerImage(String name, String contentType, String url, int zIndex) {
		GWT.log("saveLayerImage name: "+name+" contentType: "+contentType+" url: "+url);
		String message = "Select where to save the Layer item";		
		openWorkspaceExplorer(message, name, contentType, url, zIndex);		
	}

	/**
	 * Open workspace explorer.
	 *
	 * @param message the message
	 * @param name the name
	 * @param contentType the content type
	 * @param url the url
	 * @param zIndex the z index
	 */
	private void openWorkspaceExplorer(String message, String name, final String contentType, final String url, int zIndex) {
		
		final WorkspaceExplorerSaveDialog navigator = new WorkspaceExplorerSaveDialog(message, name, true);
		 
		WorskpaceExplorerSaveNotificationListener listener = new WorskpaceExplorerSaveNotificationListener(){
	 
			@Override
			public void onSaving(Item parent, String fileName) {
				final String name = fileName;
				SaveEvent.fireSave(hasSaveHandlers, name, contentType);
				service.saveLayerItem(name, contentType, url, parent.getId(), new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						SaveEvent.fireSaveSuccess(hasSaveHandlers, name, contentType);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						SaveEvent.fireSaveFailure(hasSaveHandlers, name, contentType, caught);
					}
				});	
				navigator.hide();
			}
	 
			@Override
			public void onAborted() {
				GWT.log("onAborted");
			}
	 
			@Override
			public void onFailed(Throwable throwable) {
				GWT.log("Workspace Explorer loading failure", throwable);
			}
		};
	 
		navigator.addWorkspaceExplorerSaveNotificationListener(listener);
		navigator.setZIndex(zIndex);
		navigator.show();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.gisviewer.client.GisViewerSaveHandler#saveMapImage(java.lang.String, java.lang.String, java.util.Map)
	 */
	@Override
	public void saveMapImage(String fileName, final String outputFormat,
			final Map<String, String> parameters, int zIndex) {
		
		GWT.log("saveMapImage name: "+fileName+" contentType: "+outputFormat);

		final WorkspaceExplorerSaveDialog navigator = new WorkspaceExplorerSaveDialog("Select where to save the Map Image", fileName, true);
		 
		WorskpaceExplorerSaveNotificationListener listener = new WorskpaceExplorerSaveNotificationListener(){
	 
			@Override
			public void onSaving(Item parent, String fileName) {
				final String name = fileName;
				SaveEvent.fireSave(hasSaveHandlers, name, outputFormat);
				
				service.saveMapImageItem(name, outputFormat, parameters, parent.getId(), new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						SaveEvent.fireSaveSuccess(hasSaveHandlers, name, outputFormat);
					}

					@Override
					public void onFailure(Throwable caught) {
						SaveEvent.fireSaveFailure(hasSaveHandlers, name, outputFormat, caught);
					}

				});				
				navigator.hide();
			}
	 
			@Override
			public void onAborted() {
				GWT.log("onAborted");
			}
	 
			@Override
			public void onFailed(Throwable throwable) {
				GWT.log("Workspace Explorer loading failure", throwable);
			}
		};
	 
		navigator.addWorkspaceExplorerSaveNotificationListener(listener);
		navigator.setZIndex(zIndex);
		navigator.show();
	}

}
