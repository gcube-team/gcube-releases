package org.gcube.portlets.widgets.dataminermanagerwidget.client.events;



import org.gcube.data.analysis.dataminermanagercl.shared.workspace.ItemDescription;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Resubmit Computation Execution Request Event
 * 
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ResubmitComputationExecutionRequestEvent
		extends
		GwtEvent<ResubmitComputationExecutionRequestEvent.ResubmitComputationExecutionRequestEventHandler> {

	public static Type<ResubmitComputationExecutionRequestEventHandler> TYPE = new Type<ResubmitComputationExecutionRequestEventHandler>();
	private ItemDescription itemDescription;

	public interface ResubmitComputationExecutionRequestEventHandler extends
			EventHandler {
		void onResubmit(ResubmitComputationExecutionRequestEvent event);
	}

	public interface HasResubmitComputationExecutionRequestEventHandler extends
			HasHandlers {
		public HandlerRegistration addResubmitComputationExecutionRequestEventHandler(
				ResubmitComputationExecutionRequestEventHandler handler);
	}

	public ResubmitComputationExecutionRequestEvent(
			ItemDescription itemDescription) {
		this.itemDescription = itemDescription;
	}

	@Override
	protected void dispatch(
			ResubmitComputationExecutionRequestEventHandler handler) {
		handler.onResubmit(this);
	}

	@Override
	public Type<ResubmitComputationExecutionRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ResubmitComputationExecutionRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			ResubmitComputationExecutionRequestEvent event) {
		source.fireEvent(event);
	}

	public ItemDescription getItemDescription() {
		return itemDescription;
	}

	@Override
	public String toString() {
		return "ResubmitComputationExecutionRequestEvent [itemDescription="
				+ itemDescription + "]";
	}

}
