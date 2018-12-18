package org.gcube.portlets.widgets.dataminermanagerwidget.client.events;



import org.gcube.data.analysis.dataminermanagercl.shared.workspace.ItemDescription;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Cancel Execution From Computations Request Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class CancelExecutionFromComputationsRequestEvent
		extends
		GwtEvent<CancelExecutionFromComputationsRequestEvent.CancelExecutionFromComputationsRequestEventHandler> {

	public static Type<CancelExecutionFromComputationsRequestEventHandler> TYPE = new Type<CancelExecutionFromComputationsRequestEventHandler>();
	private ItemDescription itemDescription;

	public interface CancelExecutionFromComputationsRequestEventHandler extends
			EventHandler {
		void onCancel(CancelExecutionFromComputationsRequestEvent event);
	}

	public interface HasCancelExecutionFromComputationsRequestEventHandler
			extends HasHandlers {
		public HandlerRegistration addCancelExecutionFromComputationsRequestEventHandler(
				CancelExecutionFromComputationsRequestEventHandler handler);
	}

	public CancelExecutionFromComputationsRequestEvent(
			ItemDescription itemDescription) {
		this.itemDescription = itemDescription;
	}

	@Override
	protected void dispatch(
			CancelExecutionFromComputationsRequestEventHandler handler) {
		handler.onCancel(this);
	}

	@Override
	public Type<CancelExecutionFromComputationsRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<CancelExecutionFromComputationsRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			CancelExecutionFromComputationsRequestEvent event) {
		source.fireEvent(event);
	}

	public ItemDescription getItemDescription() {
		return itemDescription;
	}

	@Override
	public String toString() {
		return "CancelExecutionFromComputationsRequestEvent [itemDescription="
				+ itemDescription + "]";
	}

}
