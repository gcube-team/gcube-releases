package org.gcube.portlets.widgets.wsexplorer.client.event;

import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.google.gwt.event.shared.GwtEvent;



/**
 * The Class LoadFolderEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 25, 2015
 */
public class LoadFolderEvent extends GwtEvent<LoadFolderEventHandler> {
	public static Type<LoadFolderEventHandler> TYPE = new Type<LoadFolderEventHandler>();
	private Item targetItem;

	

	/**
	 * Instantiates a new load folder event.
	 *
	 * @param target the target
	 */
	public LoadFolderEvent(Item target) {
		this.targetItem = target;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<LoadFolderEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(LoadFolderEventHandler handler) {
		handler.onLoadFolder(this);
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
