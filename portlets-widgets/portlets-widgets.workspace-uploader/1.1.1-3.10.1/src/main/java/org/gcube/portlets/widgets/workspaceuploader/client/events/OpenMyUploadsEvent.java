package org.gcube.portlets.widgets.workspaceuploader.client.events;

import com.google.gwt.event.shared.GwtEvent;



/**
 * The Class OpenMyUploadsEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 30, 2015
 */
public class OpenMyUploadsEvent extends GwtEvent<OpenMyUploadsEventHandler> {
	public static Type<OpenMyUploadsEventHandler> TYPE = new Type<OpenMyUploadsEventHandler>();
	
	/**
	 * Instantiates a new open my uploads event.
	 */
	public OpenMyUploadsEvent() {
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<OpenMyUploadsEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(OpenMyUploadsEventHandler handler) {
		handler.onOpenMyUploads(this);
	}
}
