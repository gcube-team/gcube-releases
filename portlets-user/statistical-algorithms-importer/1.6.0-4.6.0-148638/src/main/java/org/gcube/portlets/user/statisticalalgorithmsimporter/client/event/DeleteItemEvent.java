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
public class DeleteItemEvent extends
		GwtEvent<DeleteItemEvent.DeleteItemEventHandler> {

	public static Type<DeleteItemEventHandler> TYPE = new Type<DeleteItemEventHandler>();
	private ItemDescription itemDescription;

	public interface DeleteItemEventHandler extends EventHandler {
		void onDelete(DeleteItemEvent event);
	}

	public interface HasDeleteItemEventHandler extends HasHandlers {
		public HandlerRegistration addDeleteItemEventHandler(
				DeleteItemEventHandler handler);
	}

	public DeleteItemEvent(ItemDescription itemDescription) {
		this.itemDescription = itemDescription;
	}

	@Override
	protected void dispatch(DeleteItemEventHandler handler) {
		handler.onDelete(this);
	}

	@Override
	public Type<DeleteItemEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<DeleteItemEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, DeleteItemEvent deleteItemEvent) {
		source.fireEvent(deleteItemEvent);
	}

	public ItemDescription getItemDescription() {
		return itemDescription;
	}

	@Override
	public String toString() {
		return "DeleteItemEvent [itemDescription=" + itemDescription + "]";
	}

	
	

}
