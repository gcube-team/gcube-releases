/**
 * 
 */
package org.gcube.portlets.user.tdwx.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class FailureEvent extends GwtEvent<FailureEvent.FailureEventHandler> {

	
	public interface FailureEventHandler extends EventHandler {

		public void onFailure(FailureEvent event);

	}

	public interface HasFailureEventHandler extends HasHandlers {
		public HandlerRegistration addFailureEventHandler(
				FailureEventHandler handler);
	}

	public static GwtEvent.Type<FailureEventHandler> TYPE = new Type<FailureEventHandler>();

	@Override
	public Type<FailureEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(FailureEventHandler handler) {
		handler.onFailure(this);
	}

	public static void fire(HasHandlers source, Throwable caught, String message) {
		source.fireEvent(new FailureEvent(caught, message));
	}

	protected String message;
	protected Throwable caught;

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

	@Override
	public String toString() {
		return "FailureEvent [message=" + message + ", caught=" + caught + "]";
	}

}
