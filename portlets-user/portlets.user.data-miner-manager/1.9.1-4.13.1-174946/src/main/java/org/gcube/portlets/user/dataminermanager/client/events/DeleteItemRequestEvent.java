package org.gcube.portlets.user.dataminermanager.client.events;

import org.gcube.portlets.user.dataminermanager.client.type.DataMinerWorkAreaElementType;
import org.gcube.data.analysis.dataminermanagercl.shared.workspace.ItemDescription;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Delete Item Request Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class DeleteItemRequestEvent extends
		GwtEvent<DeleteItemRequestEvent.DeleteItemRequestEventHandler> {

	public static Type<DeleteItemRequestEventHandler> TYPE = new Type<DeleteItemRequestEventHandler>();
	private DataMinerWorkAreaElementType dataMinerWorkAreaElementType;
	private ItemDescription itemDescription;

	public interface DeleteItemRequestEventHandler extends EventHandler {
		void onDeleteRequest(DeleteItemRequestEvent event);
	}

	public interface HasDeleteItemRequestEventHandler extends HasHandlers {
		public HandlerRegistration addDeleteItemRequestEventHandler(
				DeleteItemRequestEventHandler handler);
	}

	public DeleteItemRequestEvent(
			DataMinerWorkAreaElementType dataMinerWorkAreaElementType,
			ItemDescription itemDescription) {
		this.itemDescription = itemDescription;
		this.dataMinerWorkAreaElementType = dataMinerWorkAreaElementType;
	}

	@Override
	protected void dispatch(DeleteItemRequestEventHandler handler) {
		handler.onDeleteRequest(this);
	}

	@Override
	public Type<DeleteItemRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<DeleteItemRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			DeleteItemRequestEvent deleteItemEvent) {
		source.fireEvent(deleteItemEvent);
	}

	public ItemDescription getItemDescription() {
		return itemDescription;
	}

	public DataMinerWorkAreaElementType getDataMinerWorkAreaElementType() {
		return dataMinerWorkAreaElementType;
	}

	@Override
	public String toString() {
		return "DeleteItemRequestEvent [dataMinerWorkAreaElementType="
				+ dataMinerWorkAreaElementType + ", itemDescription="
				+ itemDescription + "]";
	}

}
