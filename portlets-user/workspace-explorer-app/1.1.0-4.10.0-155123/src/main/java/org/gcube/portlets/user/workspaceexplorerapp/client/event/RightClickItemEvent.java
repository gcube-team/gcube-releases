package org.gcube.portlets.user.workspaceexplorerapp.client.event;

import org.gcube.portlets.user.workspaceexplorerapp.shared.Item;

import com.google.gwt.event.shared.GwtEvent;

/**
 * The Class RightClickItemEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 23, 2016
 */
public class RightClickItemEvent extends GwtEvent<RightClickItemEventHandler> {
	public static Type<RightClickItemEventHandler> TYPE = new Type<RightClickItemEventHandler>();
	private Item item;
	private int xPos;
	private int yPos;


	/**
	 * Instantiates a new click item event.
	 * @param yPos
	 * @param xPos
	 *
	 * @param item the item
	 */
	public RightClickItemEvent(int xPos, int yPos, Item item) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.item = item;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<RightClickItemEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	/**
	 * Dispatch.
	 *
	 * @param handler the handler
	 */
	@Override
	protected void dispatch(RightClickItemEventHandler handler) {
		handler.onClick(this);
	}

	/**
	 * Gets the item.
	 *
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * @return the xPos
	 */
	public int getXPos() {

		return xPos;
	}


	/**
	 * Gets the y pos.
	 *
	 * @return the yPos
	 */
	public int getYPos() {

		return yPos;
	}
}
