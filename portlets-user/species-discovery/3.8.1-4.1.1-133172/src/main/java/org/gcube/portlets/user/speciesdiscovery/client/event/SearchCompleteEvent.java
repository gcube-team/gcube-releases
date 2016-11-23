/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class SearchCompleteEvent extends GwtEvent<SearchCompleteEventHandler> {
	
	public static GwtEvent.Type<SearchCompleteEventHandler> TYPE = new Type<SearchCompleteEventHandler>();

	@Override
	public Type<SearchCompleteEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SearchCompleteEventHandler handler) {
		handler.onSearchComplete(this);	
	}

}
