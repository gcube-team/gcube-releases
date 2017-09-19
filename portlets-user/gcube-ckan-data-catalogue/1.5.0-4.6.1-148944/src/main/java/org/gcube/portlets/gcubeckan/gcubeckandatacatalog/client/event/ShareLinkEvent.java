package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * On share link button press event
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ShareLinkEvent extends GwtEvent<ShareLinkEventHandler>{
	
	public static Type<ShareLinkEventHandler> TYPE = new Type<ShareLinkEventHandler>();
	private String uuidItem;

	/**
	 * Instantiates a new insert metadata event.
	 */
	public ShareLinkEvent(String uuidItem) {
		this.uuidItem = uuidItem;
	}

	public String getUuidItem() {
		return uuidItem;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<ShareLinkEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(ShareLinkEventHandler handler) {
		handler.onShareLink(this);
	}


}
