package org.gcube.portlets.user.workspaceexplorerapp.client.event;

import org.gcube.portlets.user.workspaceexplorerapp.shared.Item;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class BreadcrumbInitEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 2, 2016
 */
public class BreadcrumbInitEvent extends GwtEvent<BreadcrumbInitEventHandler> {
	public static Type<BreadcrumbInitEventHandler> TYPE = new Type<BreadcrumbInitEventHandler>();
	private Item targetItem;


	/**
	 * Instantiates a new breadcrumb click event.
	 *
	 * @param target the target
	 */
	public BreadcrumbInitEvent(Item target) {
		this.targetItem = target;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<BreadcrumbInitEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(BreadcrumbInitEventHandler handler) {
		handler.onBreadcrumbInit(this);
	}

	/**
	 * Gets the target item.
	 *
	 * @return the targetItem
	 */
	public Item getTargetItem() {
		return targetItem;
	}

	/**
	 * Sets the target item.
	 *
	 * @param targetItem the targetItem to set
	 */
	public void setTargetItem(Item targetItem) {
		this.targetItem = targetItem;
	}
}
