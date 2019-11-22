package org.gcube.portlets.user.dataminerexecutor.client.events;

import org.gcube.portlets.user.dataminerexecutor.client.type.UIStateEventType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * User Interface State Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class UIStateEvent extends GwtEvent<UIStateEvent.UIStateEventHandler> {

	public static Type<UIStateEventHandler> TYPE = new Type<UIStateEventHandler>();
	private UIStateEventType uiStateType;

	public interface UIStateEventHandler extends EventHandler {
		void onChange(UIStateEvent event);
	}

	public interface HasUIStateEventHandler extends HasHandlers {
		public HandlerRegistration addUIStateEventHandler(
				UIStateEventHandler handler);
	}

	public UIStateEvent() {
		this.uiStateType = UIStateEventType.START;
	}

	public UIStateEvent(UIStateEventType uiStateType) {
		this.uiStateType = uiStateType;
	}

	@Override
	protected void dispatch(UIStateEventHandler handler) {
		handler.onChange(this);
	}

	@Override
	public Type<UIStateEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<UIStateEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, UIStateEvent uiStateEvent) {
		source.fireEvent(uiStateEvent);
	}

	public UIStateEventType getUiStateType() {
		return uiStateType;
	}

	@Override
	public String toString() {
		return "UIStatusEvent [uiStateType=" + uiStateType + "]";
	}

}
