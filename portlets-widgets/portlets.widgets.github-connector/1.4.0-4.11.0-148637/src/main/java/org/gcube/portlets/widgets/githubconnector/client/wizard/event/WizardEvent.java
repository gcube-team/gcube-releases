package org.gcube.portlets.widgets.githubconnector.client.wizard.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Wizard Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class WizardEvent extends GwtEvent<WizardEvent.WizardEventHandler> {

	public static Type<WizardEventHandler> TYPE = new Type<WizardEventHandler>();
	private WizardEventType wizardEventType;
	private String errorMessage;
	private Exception exception;

	public interface WizardEventHandler extends EventHandler {
		void onResponse(WizardEvent event);
	}

	public interface HasWizardEventHandler extends HasHandlers {
		public HandlerRegistration addWizardEventHandler(
				WizardEventHandler handler);
	}

	public WizardEvent(WizardEventType wizardEventType) {
		this.wizardEventType = wizardEventType;
	}

	@Override
	protected void dispatch(WizardEventHandler handler) {
		handler.onResponse(this);
	}

	@Override
	public Type<WizardEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<WizardEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, WizardEvent deleteItemEvent) {
		source.fireEvent(deleteItemEvent);
	}

	public WizardEventType getWizardEventType() {
		return wizardEventType;
	}

	public void setWizardEventType(WizardEventType wizardEventType) {
		this.wizardEventType = wizardEventType;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	@Override
	public String toString() {
		return "WizardEvent [wizardEventType=" + wizardEventType
				+ ", errorMessage=" + errorMessage + ", exception=" + exception
				+ "]";
	}

}
