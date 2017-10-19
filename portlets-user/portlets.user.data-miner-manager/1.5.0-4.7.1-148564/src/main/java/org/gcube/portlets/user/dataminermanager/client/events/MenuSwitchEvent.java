package org.gcube.portlets.user.dataminermanager.client.events;

import org.gcube.portlets.user.dataminermanager.client.type.MenuType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Menu Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class MenuSwitchEvent extends
		GwtEvent<MenuSwitchEvent.MenuSwitchEventHandler> {

	public static Type<MenuSwitchEventHandler> TYPE = new Type<MenuSwitchEventHandler>();
	private MenuType menuType;

	public interface MenuSwitchEventHandler extends EventHandler {
		void onSelect(MenuSwitchEvent event);
	}

	public interface HasMenuSwitchEventHandler extends HasHandlers {
		public HandlerRegistration addMenuSwitchEventHandler(
				MenuSwitchEventHandler handler);
	}

	public MenuSwitchEvent(MenuType menuType) {
		this.menuType = menuType;
	}

	@Override
	protected void dispatch(MenuSwitchEventHandler handler) {
		handler.onSelect(this);
	}

	@Override
	public Type<MenuSwitchEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<MenuSwitchEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, MenuSwitchEvent event) {
		source.fireEvent(event);
	}

	public MenuType getMenuType() {
		return menuType;
	}

	@Override
	public String toString() {
		return "MenuSwitchEvent [menuType=" + menuType + "]";
	}

}
