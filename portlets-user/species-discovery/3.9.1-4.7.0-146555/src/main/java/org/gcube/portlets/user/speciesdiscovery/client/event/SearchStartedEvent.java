/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class SearchStartedEvent extends GwtEvent<SearchStartedEventHandler> {
	
	public static GwtEvent.Type<SearchStartedEventHandler> TYPE = new Type<SearchStartedEventHandler>();

	@Override
	public Type<SearchStartedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SearchStartedEventHandler handler) {
		handler.onSearchStarted(this);	
	}

}
