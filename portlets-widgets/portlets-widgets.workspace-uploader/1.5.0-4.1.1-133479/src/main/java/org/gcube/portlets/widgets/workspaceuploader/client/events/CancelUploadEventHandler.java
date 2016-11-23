package org.gcube.portlets.widgets.workspaceuploader.client.events;

import com.google.gwt.event.shared.EventHandler;


/**
 * The Interface CancelUploadEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 7, 2015
 */
public interface CancelUploadEventHandler extends EventHandler {


	/**
	 * On cancel upload.
	 *
	 * @param deleteTimerEvent the delete timer event
	 */
	void onCancelUpload(CancelUploadEvent deleteTimerEvent);
}