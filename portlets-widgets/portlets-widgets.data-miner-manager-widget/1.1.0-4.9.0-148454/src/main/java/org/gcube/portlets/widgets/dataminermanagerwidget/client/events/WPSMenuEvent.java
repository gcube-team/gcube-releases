package org.gcube.portlets.widgets.dataminermanagerwidget.client.events;


import org.gcube.portlets.widgets.dataminermanagerwidget.client.type.WPSMenuType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * WPS Menu Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class WPSMenuEvent extends
		GwtEvent<WPSMenuEvent.WPSMenuEventHandler> {

	public static Type<WPSMenuEventHandler> TYPE = new Type<WPSMenuEventHandler>();
	private WPSMenuType menuType;
	
	public interface WPSMenuEventHandler extends EventHandler {
		void onMenu(WPSMenuEvent event);
	}

	public interface HasWPSMenuEventHandler extends HasHandlers {
		public HandlerRegistration addWPSMenuEventHandler(
				WPSMenuEventHandler handler);
	}

	public WPSMenuEvent(WPSMenuType menuType) {
		this.menuType = menuType;
	}

	@Override
	protected void dispatch(WPSMenuEventHandler handler) {
		handler.onMenu(this);
	}

	@Override
	public Type<WPSMenuEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<WPSMenuEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, WPSMenuEvent wpsMenuEvent) {
		source.fireEvent(wpsMenuEvent);
	}

	public WPSMenuType getMenuType() {
		return menuType;
	}

	@Override
	public String toString() {
		return "WPSMenuEvent [menuType=" + menuType + "]";
	}

	
	

}
