/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ShowOccurrencesEvent extends GwtEvent<ShowOccurrencesEventHandler> {
	
	public static final GwtEvent.Type<ShowOccurrencesEventHandler> TYPE = new Type<ShowOccurrencesEventHandler>();

	@Override
	public Type<ShowOccurrencesEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowOccurrencesEventHandler handler) {
		handler.onShowOccurrences(this);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ShowOccurrencesEvent []");
		return builder.toString();
	}
	
}
