package org.gcube.portlets.user.workspace.client.event;

import java.util.List;

import org.gcube.portlets.user.workspace.client.interfaces.EventsTypeEnum;
import org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class CopyItemsEvent.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Oct 10, 2018
 */
public class CopyItemsEvent extends GwtEvent<CopyItemsEventHandler> implements GuiEventInterface{
	public static Type<CopyItemsEventHandler> TYPE = new Type<CopyItemsEventHandler>();

	private List<String> ids;

	private String destinationFolderId;


	/**
	 * Instantiates a new copy items event.
	 *
	 * @param ids the ids
	 * @param destinationFolderId the destination folder id
	 */
	public CopyItemsEvent(List<String> ids, String destinationFolderId) {
		this.ids = ids;
		this.destinationFolderId = destinationFolderId;
	}


	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<CopyItemsEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(CopyItemsEventHandler handler) {
		handler.onCopyItems(this);

	}
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.workspace.client.interfaces.GuiEventInterface#getKey()
	 */
	@Override
	public EventsTypeEnum getKey() {
		return EventsTypeEnum.COPY_EVENT;
	}

	/**
	 * Gets the ids.
	 *
	 * @return the ids
	 */
	public List<String> getIds() {
		return ids;
	}

	/**
	 * Sets the ids.
	 *
	 * @param ids the new ids
	 */
	public void setIds(List<String> ids) {
		this.ids = ids;
	}


	/**
	 * Gets the destination folder id.
	 *
	 * @return the destinationId
	 */
	public String getDestinationFolderId() {

		return destinationFolderId;
	}

}
