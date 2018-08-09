package org.gcube.portlets.user.workspaceexplorerapp.client.event;

import org.gcube.portlets.user.workspaceexplorerapp.client.download.DownloadType;
import org.gcube.portlets.user.workspaceexplorerapp.shared.ItemInterface;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class ClickItemEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 6, 2015
 */
public class DownloadItemEvent extends GwtEvent<DownloadItemEventHandler> {
	public static Type<DownloadItemEventHandler> TYPE = new Type<DownloadItemEventHandler>();
	private ItemInterface item;
	private DownloadType type;

	/**
	 * Instantiates a new click item event.
	 *
	 * @param itemDownload the item download
	 * @param type the type
	 */
	public DownloadItemEvent(ItemInterface itemDownload, DownloadType type) {
		this.item = itemDownload;
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public DownloadType getType() {

		return type;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<DownloadItemEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(DownloadItemEventHandler handler) {
		handler.onDownloadItem(this);
	}

	/**
	 * Gets the item.
	 *
	 * @return the item
	 */
	public ItemInterface getItem() {
		return item;
	}
}
