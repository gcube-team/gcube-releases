package org.gcube.portlets.widgets.wsexplorer.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class CreateFolderClickEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 1, 2016
 */
public class CreateFolderClickEvent extends GwtEvent<CreateFolderClickEventHandler> {
	public static Type<CreateFolderClickEventHandler> TYPE = new Type<CreateFolderClickEventHandler>();

	/**
	 * Instantiates a new click item event.
	 */
	public CreateFolderClickEvent() {
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<CreateFolderClickEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(CreateFolderClickEventHandler handler) {
		handler.onClick(this);
	}
}
