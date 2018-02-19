package org.gcube.portlets.admin.accountingmanager.client.event;

import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class AccountingMenuEvent extends
		GwtEvent<AccountingMenuEvent.AccountingMenuEventHandler> {

	public static Type<AccountingMenuEventHandler> TYPE = new Type<AccountingMenuEventHandler>();
	private AccountingType accountingType;

	public interface AccountingMenuEventHandler extends EventHandler {
		void onMenu(AccountingMenuEvent event);
	}

	public interface HasAccountingMenuEventHandler extends HasHandlers {
		public HandlerRegistration addAccountingMenuEventHandler(
				AccountingMenuEventHandler handler);
	}

	public AccountingMenuEvent(AccountingType accountingType) {
		this.accountingType = accountingType;
	}

	@Override
	protected void dispatch(AccountingMenuEventHandler handler) {
		handler.onMenu(this);
	}

	@Override
	public Type<AccountingMenuEventHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<AccountingMenuEventHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source,
			AccountingMenuEvent accountingMenuEvent) {
		source.fireEvent(accountingMenuEvent);
	}

	public AccountingType getAccountingType() {
		return accountingType;
	}

	@Override
	public String toString() {
		return "AccountingMenuEvent [accountingType=" + accountingType + "]";
	}

}
