/**
 *
 */
package org.gcube.portlets.widgets.workspaceuploader.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.workspaceuploader.client.WorkspaceUploadNotification.WorskpaceUploadNotificationListener;
import org.gcube.portlets.widgets.workspaceuploader.client.events.NotifyUploadEvent;
import org.gcube.portlets.widgets.workspaceuploader.client.events.NotifyUploadEventHandler;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerManager;


/**
 * The Class WorkspaceExplorerListenerController.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 5, 2015
 */
public class WorkspaceUploaderListenerController {

	private static HandlerManager controllerEventBus = null;

	private List<WorskpaceUploadNotificationListener> listenersUpload = new ArrayList<WorskpaceUploadNotificationListener>();

	/**
	 * Instantiates a new workspace explorer listener controller.
	 */
	public WorkspaceUploaderListenerController(){
		controllerEventBus = new HandlerManager(null);
		bindEvents();

	}

	/**
	 * Bind events.
	 */
	private void bindEvents() {
		controllerEventBus.addHandler(NotifyUploadEvent.TYPE, new NotifyUploadEventHandler() {

			@Override
			public void onNotifyUpload(final NotifyUploadEvent notifyUploadEvent) {

				if(notifyUploadEvent.getEvent()==null)
					return;

				switch(notifyUploadEvent.getEvent()){

				case UPLOAD_COMPLETED:
					GWT.log("NotifyUploadEvent Completed");
					notifyUploadCompleted(notifyUploadEvent.getParentId(), notifyUploadEvent.getItemId());
					break;
				case ABORTED:
					GWT.log("NotifyUploadEvent Aborted");
					notifyUploadAborted(notifyUploadEvent.getParentId(), notifyUploadEvent.getItemId());
					break;
				case FAILED:
					GWT.log("NotifyUploadEvent FAILED");
					notifyUploadError(notifyUploadEvent.getParentId(), notifyUploadEvent.getItemId(), new Exception(notifyUploadEvent.getUploadResultMsg()));
					break;
				case OVERWRITE_COMPLETED:
					GWT.log("NotifyUploadEvent OVERWRITE_COMPLETED");
					notifyOverwriteCompleted(notifyUploadEvent.getParentId(), notifyUploadEvent.getItemId());
					break;
				default:
					break;

				}
			}
		});
	}

	/**
	 * Gets the event bus.
	 *
	 * @return the event bus
	 */
	public static HandlerManager getEventBus(){
		return controllerEventBus;
	}

	/**
	 * Adds the workspace upload listener.
	 *
	 * @param handler the handler
	 */
	public void addWorkspaceUploadListener(WorskpaceUploadNotificationListener handler) {
		this.listenersUpload.add(handler);
	}

	/**
	 * Removes the workspace upload listener.
	 *
	 * @param handler the handler
	 */
	public void removeWorkspaceUploadListener(WorskpaceUploadNotificationListener handler) {
		try {
			this.listenersUpload.remove(handler);
		} catch (Exception e) {
			// SILENT
		}
	}

	/**
	 * Reset listeners.
	 */
	public void resetListeners(){
		this.listenersUpload.clear();
	}

	/**
	 * Notify upload completed.
	 *
	 * @param parentId the parent id
	 * @param itemId the item id
	 */
	private void notifyUploadCompleted(String parentId, String itemId){
//		listenersSize();
		for (WorskpaceUploadNotificationListener listener : listenersUpload) {
			listener.onUploadCompleted(parentId, itemId);
		}
	}

	/**
	 * Notify overwrite completed.
	 *
	 * @param parentId the parent id
	 * @param itemId the item id
	 */
	private void notifyOverwriteCompleted(String parentId, String itemId) {
//		listenersSize();
		for (WorskpaceUploadNotificationListener listener : listenersUpload) {
			listener.onOverwriteCompleted(parentId, itemId);
		}
	}

	/**
	 * Notify upload aborted.
	 *
	 * @param parentId the parent id
	 * @param itemId the item id
	 */
	private void notifyUploadAborted(String parentId, String itemId){
		for (WorskpaceUploadNotificationListener listener : listenersUpload) {
			listener.onUploadAborted(parentId, itemId);
		}
	}

	/**
	 * Notify upload error.
	 *
	 * @param parentId the parent id
	 * @param itemId the item id
	 * @param t the t
	 */
	private void notifyUploadError(String parentId, String itemId, Throwable t){
		for (WorskpaceUploadNotificationListener listener : listenersUpload) {
			listener.onError(parentId, itemId, t);
		}
	}

	/**
	 * Listeners size.
	 *
	 * @return the int
	 */
	private int listenersSize(){
		GWT.log("listenersUpload.size() "+listenersUpload.size());
		return listenersUpload.size();
	}
}
