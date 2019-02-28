package org.gcube.portlets.widgets.fileupload.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event thrown when the user tries to upload a too large file.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public class FileTooLargeEvent  extends GwtEvent<FileTooLargeEventHandler> {
	public static Type<FileTooLargeEventHandler> TYPE = new Type<FileTooLargeEventHandler>();

	@Override
	public Type<FileTooLargeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(FileTooLargeEventHandler handler) {
		handler.onFileTooLargeEvent(this);
	}
}
