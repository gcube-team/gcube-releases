/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.GwtEvent;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ViewDetailsOfSelectedEvent extends GwtEvent<ViewDetailsOfSelectedEventHandler> {
	
	public static final GwtEvent.Type<ViewDetailsOfSelectedEventHandler> TYPE = new Type<ViewDetailsOfSelectedEventHandler>();

	@Override
	public Type<ViewDetailsOfSelectedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ViewDetailsOfSelectedEventHandler handler) {
		handler.onViewDetails(this);	
	}
	
	public ViewDetailsOfSelectedEvent() {
	}

}
