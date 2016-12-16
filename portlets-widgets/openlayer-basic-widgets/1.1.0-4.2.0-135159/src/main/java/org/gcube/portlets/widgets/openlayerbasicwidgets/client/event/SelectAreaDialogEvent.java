package org.gcube.portlets.widgets.openlayerbasicwidgets.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Select Area Dialog Event
 * 
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class SelectAreaDialogEvent extends
		GwtEvent<SelectAreaDialogEvent.SelectAreaDialogEventHandler> {

	public static Type<SelectAreaDialogEventHandler> TYPE = new Type<SelectAreaDialogEventHandler>();
	private SelectAreaDialogEventType selectAreaDialogEventType;
	private String area;
	private String errorMessage;
	private Exception exception;

	public interface SelectAreaDialogEventHandler extends EventHandler {
		void onResponse(SelectAreaDialogEvent event);
	}

	public interface HasSelectAreaDialogEventHandler extends HasHandlers {
		public HandlerRegistration addSelectAreaDialogEventHandler(
				SelectAreaDialogEventHandler handler);
	}

	public SelectAreaDialogEvent(SelectAreaDialogEventType wizardEventType) {
		this.selectAreaDialogEventType = wizardEventType;
	}

	@Override
	protected void dispatch(SelectAreaDialogEventHandler handler) {
		handler.onResponse(this);
	}

	@Override
	public Type<SelectAreaDialogEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<SelectAreaDialogEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			SelectAreaDialogEvent deleteItemEvent) {
		source.fireEvent(deleteItemEvent);
	}

	public SelectAreaDialogEventType getWizardEventType() {
		return selectAreaDialogEventType;
	}

	public void setWizardEventType(SelectAreaDialogEventType wizardEventType) {
		this.selectAreaDialogEventType = wizardEventType;
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

	public SelectAreaDialogEventType getSelectAreaDialogEventType() {
		return selectAreaDialogEventType;
	}

	public void setSelectAreaDialogEventType(
			SelectAreaDialogEventType selectAreaDialogEventType) {
		this.selectAreaDialogEventType = selectAreaDialogEventType;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	@Override
	public String toString() {
		return "SelectAreaDialogEvent [selectAreaDialogEventType="
				+ selectAreaDialogEventType + ", area=" + area
				+ ", errorMessage=" + errorMessage + ", exception=" + exception
				+ "]";
	}

}
