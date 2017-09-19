package org.gcube.portlets.admin.accountingmanager.client.event;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingPeriod;

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
public class AccountingPeriodEvent extends
		GwtEvent<AccountingPeriodEvent.AccountingPeriodEventHandler> {

	public static Type<AccountingPeriodEventHandler> TYPE = new Type<AccountingPeriodEventHandler>();
	private AccountingPeriod accountingPeriod;

	public interface AccountingPeriodEventHandler extends EventHandler {
		void onPeriod(AccountingPeriodEvent event);
	}

	public interface HasAccountingPeriodEventHandler extends HasHandlers {
		public HandlerRegistration addAccountingPeriodEventHandler(
				AccountingPeriodEventHandler handler);
	}

	public AccountingPeriodEvent(AccountingPeriod accountingPeriod) {
		this.accountingPeriod = accountingPeriod;
	}

	@Override
	protected void dispatch(AccountingPeriodEventHandler handler) {
		handler.onPeriod(this);
	}

	@Override
	public Type<AccountingPeriodEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<AccountingPeriodEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, AccountingPeriodEvent event) {
		source.fireEvent(event);
	}

	public AccountingPeriod getAccountingPeriod() {
		return accountingPeriod;
	}

	@Override
	public String toString() {
		return "AccountingPeriodEvent [accountingPeriod=" + accountingPeriod
				+ "]";
	}

}
