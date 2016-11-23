package org.gcube.portlets.user.dataminermanager.client.events;

import org.gcube.portlets.user.dataminermanager.shared.data.computations.ComputationData;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Computation Data Event
 * 
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ComputationDataEvent extends
		GwtEvent<ComputationDataEvent.ComputationDataEventHandler> {

	public static Type<ComputationDataEventHandler> TYPE = new Type<ComputationDataEventHandler>();
	private ComputationData computationData;

	public interface ComputationDataEventHandler extends EventHandler {
		void onComputationData(ComputationDataEvent event);
	}

	public interface HasOutputShowResourceEventHandler extends HasHandlers {
		public HandlerRegistration addOutputShowResourceEventHandler(
				ComputationDataEventHandler handler);
	}

	public ComputationDataEvent(ComputationData computationData) {
		this.computationData = computationData;
	}

	@Override
	protected void dispatch(ComputationDataEventHandler handler) {
		handler.onComputationData(this);
	}

	@Override
	public Type<ComputationDataEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ComputationDataEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, ComputationDataEvent event) {
		source.fireEvent(event);
	}

	public ComputationData getComputationData() {
		return computationData;
	}

	@Override
	public String toString() {
		return "ComputationDataEvent [computationData=" + computationData + "]";
	}

}
