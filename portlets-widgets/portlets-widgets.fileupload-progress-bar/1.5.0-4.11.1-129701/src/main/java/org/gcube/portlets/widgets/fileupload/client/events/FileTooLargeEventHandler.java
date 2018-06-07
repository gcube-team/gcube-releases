package org.gcube.portlets.widgets.fileupload.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler linked to @{FileTooLargeEvent} event class.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public interface FileTooLargeEventHandler extends EventHandler{
	void onFileTooLargeEvent(FileTooLargeEvent fileTooLargeEvent);
}
