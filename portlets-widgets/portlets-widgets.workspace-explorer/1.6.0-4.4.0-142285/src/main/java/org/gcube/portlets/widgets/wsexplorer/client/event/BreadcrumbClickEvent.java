package org.gcube.portlets.widgets.wsexplorer.client.event;

import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class BreadcrumbClickEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Aug 3, 2015
 */
public class BreadcrumbClickEvent extends GwtEvent<BreadcrumbClickEventHandler> {
	public static Type<BreadcrumbClickEventHandler> TYPE = new Type<BreadcrumbClickEventHandler>();
	private Item targetItem;

	
	/**
	 * Instantiates a new breadcrumb click event.
	 *
	 * @param target the target
	 */
	public BreadcrumbClickEvent(Item target) {
		this.targetItem = target;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<BreadcrumbClickEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(BreadcrumbClickEventHandler handler) {
		handler.onBreadcrumbClick(this);
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
