package org.gcube.portlets.admin.accountingmanager.client.event;


import org.gcube.portlets.admin.accountingmanager.client.type.UIStateType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class UIStateEvent extends GwtEvent<UIStateEvent.UIStateHandler> {

	public static Type<UIStateHandler> TYPE = new Type<UIStateHandler>();
	private UIStateType uiStateType;

	public interface UIStateHandler extends EventHandler {
		void onUIState(UIStateEvent event);
	}

	public interface HasUIStateHandler extends HasHandlers {
		public HandlerRegistration addUIStateHandler(UIStateHandler handler);
	}

	public UIStateEvent(UIStateType uiStateType) {
		this.uiStateType = uiStateType;
	}
	

	@Override
	protected void dispatch(UIStateHandler handler) {
		handler.onUIState(this);
	}

	@Override
	public Type<UIStateHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<UIStateHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, UIStateEvent uiStateEvent) {
		source.fireEvent(uiStateEvent);
	}


	public UIStateType getUiStateType() {
		return uiStateType;
	}


	@Override
	public String toString() {
		return "UIStateEvent [uiStateType=" + uiStateType + "]";
	}

	
	

}
