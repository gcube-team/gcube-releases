package org.gcube.portlets.widgets.dataminermanagerwidget.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Tabular Resource Info Request Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class TabularResourceInfoRequestEvent
		extends
		GwtEvent<TabularResourceInfoRequestEvent.TabularResourceInfoRequestEventHandler> {

	public static Type<TabularResourceInfoRequestEventHandler> TYPE = new Type<TabularResourceInfoRequestEventHandler>();

	public interface TabularResourceInfoRequestEventHandler extends
			EventHandler {
		void onRequest(TabularResourceInfoRequestEvent event);
	}

	public interface HasTabularResourceInfoRequestEventHandler extends
			HasHandlers {
		public HandlerRegistration addTabularResourceInfoRequestEventHandler(
				TabularResourceInfoRequestEventHandler handler);
	}

	public TabularResourceInfoRequestEvent() {
	}

	@Override
	protected void dispatch(TabularResourceInfoRequestEventHandler handler) {
		handler.onRequest(this);
	}

	@Override
	public Type<TabularResourceInfoRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<TabularResourceInfoRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			TabularResourceInfoRequestEvent event) {
		source.fireEvent(event);
	}

	@Override
	public String toString() {
		return "TabularResourceInfoRequestEvent []";
	}

}
