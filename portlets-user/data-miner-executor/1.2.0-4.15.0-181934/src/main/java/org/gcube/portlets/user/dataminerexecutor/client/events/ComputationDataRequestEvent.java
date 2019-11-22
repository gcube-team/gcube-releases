package org.gcube.portlets.user.dataminerexecutor.client.events;

import org.gcube.data.analysis.dataminermanagercl.shared.workspace.ItemDescription;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Output Show Request Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ComputationDataRequestEvent
		extends
		GwtEvent<ComputationDataRequestEvent.ComputationDataRequestEventHandler> {

	public static Type<ComputationDataRequestEventHandler> TYPE = new Type<ComputationDataRequestEventHandler>();
	private ItemDescription itemDescription;

	public interface ComputationDataRequestEventHandler extends EventHandler {
		void onComputationDataRequest(ComputationDataRequestEvent event);
	}

	public interface HasComputationDataRequestEventHandler extends HasHandlers {
		public HandlerRegistration addComputationDataRequestEventHandler(
				ComputationDataRequestEventHandler handler);
	}

	public ComputationDataRequestEvent(ItemDescription itemDescription) {
		this.itemDescription = itemDescription;
	}

	@Override
	protected void dispatch(ComputationDataRequestEventHandler handler) {
		handler.onComputationDataRequest(this);
	}

	@Override
	public Type<ComputationDataRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ComputationDataRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			ComputationDataRequestEvent event) {
		source.fireEvent(event);
	}

	public ItemDescription getItemDescription() {
		return itemDescription;
	}

	@Override
	public String toString() {
		return "ComputationDataRequestEvent [itemDescription="
				+ itemDescription + "]";
	}

}
