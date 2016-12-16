package org.gcube.portlets.admin.accountingmanager.client.event;

import org.gcube.portlets.admin.accountingmanager.client.state.AccountingClientStateData;
import org.gcube.portlets.admin.accountingmanager.client.type.StateChangeType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class StateChangeEvent extends
		GwtEvent<StateChangeEvent.StateChangeEventHandler> {

	public static Type<StateChangeEventHandler> TYPE = new Type<StateChangeEventHandler>();
	private StateChangeType stateChangeType;
	private AccountingClientStateData accountingStateData;

	public interface StateChangeEventHandler extends EventHandler {
		void onStateChange(StateChangeEvent event);
	}

	public interface HasStateChangeEventHandler extends HasHandlers {
		public HandlerRegistration addStateChangeEventHandler(
				StateChangeEventHandler handler);
	}

	public StateChangeEvent(StateChangeType stateChangeType,
			AccountingClientStateData accountingStateData) {
		this.stateChangeType = stateChangeType;
		this.accountingStateData = accountingStateData;
	}

	@Override
	protected void dispatch(StateChangeEventHandler handler) {
		handler.onStateChange(this);
	}

	@Override
	public Type<StateChangeEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<StateChangeEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			StateChangeEvent stateChangeEvent) {
		source.fireEvent(stateChangeEvent);
	}

	public StateChangeType getStateChangeType() {
		return stateChangeType;
	}

	public AccountingClientStateData getAccountingStateData() {
		return accountingStateData;
	}

	@Override
	public String toString() {
		return "StateChangeEvent [stateChangeType=" + stateChangeType
				+ ", accountingStateData=" + accountingStateData + "]";
	}
	
	

}
