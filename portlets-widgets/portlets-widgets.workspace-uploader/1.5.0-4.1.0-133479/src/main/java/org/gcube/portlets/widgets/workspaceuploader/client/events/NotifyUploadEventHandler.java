package org.gcube.portlets.widgets.workspaceuploader.client.events;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface NotifyUploadEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Oct 5, 2015
 */
public interface NotifyUploadEventHandler extends EventHandler {


	/**
	 * On notify upload.
	 *
	 * @param notifyUploadEvent the notify upload event
	 */
	void onNotifyUpload(NotifyUploadEvent notifyUploadEvent);
}