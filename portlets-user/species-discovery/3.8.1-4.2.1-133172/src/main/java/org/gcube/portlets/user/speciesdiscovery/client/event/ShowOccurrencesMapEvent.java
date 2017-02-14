/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ShowOccurrencesMapEvent extends GwtEvent<ShowOccurrencesMapEventHandler> {
	
	public static final GwtEvent.Type<ShowOccurrencesMapEventHandler> TYPE = new Type<ShowOccurrencesMapEventHandler>();
	private int expectedPoints;

	public ShowOccurrencesMapEvent(int expectedPoints) {
		this.expectedPoints = expectedPoints;
	}

	@Override
	public Type<ShowOccurrencesMapEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowOccurrencesMapEventHandler handler) {
		handler.onShowOccurrencesMap(this);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ShowOccurrencesMapEvent []");
		return builder.toString();
	}

	public int getExpectedPoints() {
		return expectedPoints;
	}
	
}
