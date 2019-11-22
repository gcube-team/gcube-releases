package org.gcube.portlets.user.statisticalalgorithmsimporter.client.event;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.workspace.ItemDescription;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Binary Code Set Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class BinaryCodeSetEvent extends GwtEvent<BinaryCodeSetEvent.BinaryCodeSetEventHandler> {

	public static Type<BinaryCodeSetEventHandler> TYPE = new Type<BinaryCodeSetEventHandler>();
	private ItemDescription itemDescription;

	public interface BinaryCodeSetEventHandler extends EventHandler {
		void onBinaryCodeSet(BinaryCodeSetEvent event);
	}

	public interface HasBinaryCodeSetEventHandler extends HasHandlers {
		public HandlerRegistration addBinaryCodeSetEventHandler(BinaryCodeSetEventHandler handler);
	}

	public BinaryCodeSetEvent(ItemDescription itemDescription) {
		this.itemDescription = itemDescription;
	}

	@Override
	protected void dispatch(BinaryCodeSetEventHandler handler) {
		handler.onBinaryCodeSet(this);
	}

	@Override
	public Type<BinaryCodeSetEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<BinaryCodeSetEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, BinaryCodeSetEvent binaryCodeSetEvent) {
		source.fireEvent(binaryCodeSetEvent);
	}

	public ItemDescription getItemDescription() {
		return itemDescription;
	}

	@Override
	public String toString() {
		return "BinaryCodeSetEvent [itemDescription=" + itemDescription + "]";
	}

}
