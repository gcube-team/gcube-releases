package org.gcube.portlets.user.workspaceexplorerapp.client.event;

import com.google.gwt.event.shared.GwtEvent;



/**
 * The Class LoadFolderEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 25, 2015
 */
public class OrderDataByEvent extends GwtEvent<OrderDataByEventHandler> {
	public static Type<OrderDataByEventHandler> TYPE = new Type<OrderDataByEventHandler>();
	private String label;

	/**
	 * Instantiates a new order data by event.
	 *
	 * @param label the label
	 */
	public OrderDataByEvent(String label) {
		this.label = label;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<OrderDataByEventHandler> getAssociatedType() {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(OrderDataByEventHandler handler) {
		handler.onOrderDataBy(this);
	}


	/**
	 * @return the label
	 */
	public String getLabel() {

		return label;
	}

}
