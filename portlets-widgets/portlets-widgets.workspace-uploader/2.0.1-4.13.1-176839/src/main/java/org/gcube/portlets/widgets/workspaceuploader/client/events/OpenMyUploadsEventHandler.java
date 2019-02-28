package org.gcube.portlets.widgets.workspaceuploader.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * The Interface OpenMyUploadsEventHandler.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Sep 30, 2015
 */
public interface OpenMyUploadsEventHandler extends EventHandler {

	/**
	 * On open my uploads.
	 *
	 * @param openMyUploadsEvent
	 *            the open my uploads event
	 */
	void onOpenMyUploads(OpenMyUploadsEvent openMyUploadsEvent);
}