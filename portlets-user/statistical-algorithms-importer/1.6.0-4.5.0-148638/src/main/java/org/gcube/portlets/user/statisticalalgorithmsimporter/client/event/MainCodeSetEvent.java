package org.gcube.portlets.user.statisticalalgorithmsimporter.client.event;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Main Code Set Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class MainCodeSetEvent extends
		GwtEvent<MainCodeSetEvent.MainCodeSetEventHandler> {

	public static Type<MainCodeSetEventHandler> TYPE = new Type<MainCodeSetEventHandler>();
	private ItemDescription itemDescription;

	public interface MainCodeSetEventHandler extends EventHandler {
		void onMainCodeSet(MainCodeSetEvent event);
	}

	public interface HasMainCodeSetEventHandler extends HasHandlers {
		public HandlerRegistration addMainCodeSetEventHandler(
				MainCodeSetEventHandler handler);
	}

	public MainCodeSetEvent(ItemDescription itemDescription) {
		this.itemDescription = itemDescription;
	}

	@Override
	protected void dispatch(MainCodeSetEventHandler handler) {
		handler.onMainCodeSet(this);
	}

	@Override
	public Type<MainCodeSetEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<MainCodeSetEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, MainCodeSetEvent importCodeEvent) {
		source.fireEvent(importCodeEvent);
	}

	public ItemDescription getItemDescription() {
		return itemDescription;
	}

	@Override
	public String toString() {
		return "MainCodeSetEvent [itemDescription=" + itemDescription + "]";
	}

}
