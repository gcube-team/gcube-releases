package org.gcube.portlets.admin.accountingmanager.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * Accounting Period Request Event
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class AccountingPeriodRequestEvent
		extends
		GwtEvent<AccountingPeriodRequestEvent.AccountingPeriodRequestEventHandler> {

	public static Type<AccountingPeriodRequestEventHandler> TYPE = new Type<AccountingPeriodRequestEventHandler>();

	public interface AccountingPeriodRequestEventHandler extends EventHandler {
		void onRequest(AccountingPeriodRequestEvent event);
	}

	public interface HasAccountingPeriodRequestEventHandler extends HasHandlers {
		public HandlerRegistration addAccountingPeriodRequestEventHandler(
				AccountingPeriodRequestEventHandler handler);
	}

	public AccountingPeriodRequestEvent() {
	}

	@Override
	protected void dispatch(AccountingPeriodRequestEventHandler handler) {
		handler.onRequest(this);
	}

	@Override
	public Type<AccountingPeriodRequestEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<AccountingPeriodRequestEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			AccountingPeriodRequestEvent event) {
		source.fireEvent(event);
	}

	@Override
	public String toString() {
		return "AccountingPeriodRequestEvent []";
	}

}
