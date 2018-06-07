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
public class MenuEvent extends
		GwtEvent<MenuEvent.MenuEventHandler> {

	public static Type<MenuEventHandler> TYPE = new Type<MenuEventHandler>();
	private MenuType menuType;

	public interface MenuEventHandler extends EventHandler {
		void onSelect(MenuEvent event);
	}

	public interface HasMenuEventHandler extends HasHandlers {
		public HandlerRegistration addMenuEventHandler(
				MenuEventHandler handler);
	}

	public MenuEvent(
			MenuType menuType) {
		this.menuType = menuType;
	}

	@Override
	protected void dispatch(MenuEventHandler handler) {
		handler.onSelect(this);
	}

	@Override
	public Type<MenuEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<MenuEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, MenuEvent event) {
		source.fireEvent(event);
	}

	public MenuType getMenuType() {
		return menuType;
	}

	@Override
	public String toString() {
		return "MenuEvent [menuType=" + menuType + "]";
	}

	
	

}
