package org.gcube.portlets.user.trainingcourse.client.event;

import com.google.gwt.event.shared.GwtEvent;


// TODO: Auto-generated Javadoc
/**
 * The Class SelectedWorkspaceItemEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 15, 2018
 */
public class SelectedWorkspaceItemEvent extends GwtEvent<SelectedWorkspaceItemEventHandler> {
	
	/** The type. */
	public static Type<SelectedWorkspaceItemEventHandler> TYPE = new Type<SelectedWorkspaceItemEventHandler>();
	private String itemId;

	
	/**
	 * Instantiates a new selected workspace item event.
	 *
	 * @param itemId the item id
	 */
	public SelectedWorkspaceItemEvent(String itemId) {
		this.itemId = itemId;

	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<SelectedWorkspaceItemEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(SelectedWorkspaceItemEventHandler handler) {
		handler.onSelectedItem(this);
	}
	
	public String getItemId() {
		return itemId;
	}

}
