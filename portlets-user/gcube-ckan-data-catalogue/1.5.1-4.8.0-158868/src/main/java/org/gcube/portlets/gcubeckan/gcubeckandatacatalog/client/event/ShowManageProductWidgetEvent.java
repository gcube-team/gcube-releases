package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Show a manage product widget
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ShowManageProductWidgetEvent extends GwtEvent<ShowManageProductWidgetEventHandler> {
	
	public static Type<ShowManageProductWidgetEventHandler> TYPE = new Type<ShowManageProductWidgetEventHandler>();
	
	private String productIdentifier;
	
	/**
	 * Instantiates a new show manage product widget event.
	 */
	public ShowManageProductWidgetEvent(String productIdentifier) {
		this.productIdentifier = productIdentifier;
	}

	public String getProductIdentifier() {
		return productIdentifier;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ShowManageProductWidgetEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowManageProductWidgetEventHandler handler) {
		handler.onShowManageProductWidget(this);
	}

}
