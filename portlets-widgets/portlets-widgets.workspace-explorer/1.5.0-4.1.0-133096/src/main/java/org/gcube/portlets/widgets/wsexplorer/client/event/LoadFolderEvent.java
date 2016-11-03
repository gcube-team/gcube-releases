package org.gcube.portlets.widgets.wsexplorer.client.event;

import com.google.gwt.event.shared.GwtEvent;



/**
 * The Class LoadFolderEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 25, 2015
 * @param <T>
 */
public class LoadFolderEvent<T> extends GwtEvent<LoadFolderEventHandler> {
	public static Type<LoadFolderEventHandler> TYPE = new Type<LoadFolderEventHandler>();
	private T targetItem;



	/**
	 * Instantiates a new load folder event.
	 *
	 * @param selected the target
	 */
	public LoadFolderEvent(T selected) {
		this.targetItem = selected;
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
	 * @param <T>
	 *
	 * @return the targetItem
	 */
	public T getTargetItem() {
		return targetItem;
	}

	/**
	 * Sets the target item.
	 * @param <T>
	 *
	 * @param targetItem the targetItem to set
	 */
	public void setTargetItem(T targetItem) {
		this.targetItem = targetItem;
	}
}
