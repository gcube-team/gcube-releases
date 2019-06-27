package org.gcube.portlets.user.dataminerexecutor.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class RemoveSelectedOperatorEvent
		extends GwtEvent<RemoveSelectedOperatorEvent.RemoveSelectedOperatorEventHandler> {

	public static Type<RemoveSelectedOperatorEventHandler> TYPE = new Type<RemoveSelectedOperatorEventHandler>();

	public interface RemoveSelectedOperatorEventHandler extends EventHandler {
		void onSelect(RemoveSelectedOperatorEvent event);
	}

	public interface HasRemoveSelectedOperatorEventHandler extends HasHandlers {
		public HandlerRegistration removeSelectedOperatorEventHandler(RemoveSelectedOperatorEventHandler handler);
	}

	public RemoveSelectedOperatorEvent() {

	}

	@Override
	protected void dispatch(RemoveSelectedOperatorEventHandler handler) {
		handler.onSelect(this);
	}

	@Override
	public Type<RemoveSelectedOperatorEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<RemoveSelectedOperatorEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, MenuEvent event) {
		source.fireEvent(event);
	}

	@Override
	public String toString() {
		return "RemoveSelectedOperatorEvent []";
	}

}
