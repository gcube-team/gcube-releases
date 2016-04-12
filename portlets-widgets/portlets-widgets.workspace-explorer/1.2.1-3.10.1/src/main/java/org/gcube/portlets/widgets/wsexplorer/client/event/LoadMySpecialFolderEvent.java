package org.gcube.portlets.widgets.wsexplorer.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 */
public class LoadMySpecialFolderEvent extends GwtEvent<LoadMySpecialFolderEventHandler> {
	
	public static Type<LoadMySpecialFolderEventHandler> TYPE = new Type<LoadMySpecialFolderEventHandler>();

	/**
	 * Instantiates a new double click event.
	 *
	 * @param target the target
	 */
	public LoadMySpecialFolderEvent() {
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<LoadMySpecialFolderEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(LoadMySpecialFolderEventHandler handler) {
		handler.onLoadMySpecialFolder(this);
	}
}
