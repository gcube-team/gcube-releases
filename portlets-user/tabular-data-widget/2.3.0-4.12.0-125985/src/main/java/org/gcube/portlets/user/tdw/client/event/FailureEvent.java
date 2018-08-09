/**
 * 
 */
package org.gcube.portlets.user.tdw.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class FailureEvent extends GwtEvent<FailureEventHandler> {
	
	public static GwtEvent.Type<FailureEventHandler> TYPE = new Type<FailureEventHandler>();

	@Override
	public Type<FailureEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(FailureEventHandler handler) {
		handler.onFailure(this);	
	}
	
	protected Throwable caught;
	protected String message;

	/**
	 * @param caught
	 * @param message
	 */
	public FailureEvent(Throwable caught, String message) {
		this.caught = caught;
		this.message = message;
	}

	/**
	 * @return the caught
	 */
	public Throwable getCaught() {
		return caught;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FailureEvent [caught=");
		builder.append(caught);
		builder.append(", message=");
		builder.append(message);
		builder.append("]");
		return builder.toString();
	}
}
